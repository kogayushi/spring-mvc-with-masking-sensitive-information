package com.example.mvc_with_masking_sensitive_info.infrastructure.filter.masking;


import com.example.mvc_with_masking_sensitive_info.service.MaskingService;
import com.example.mvc_with_masking_sensitive_info.service.MaskingServiceSelector;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class MaskingLoggingFilter extends OncePerRequestFilter {

    public static final String DEFAULT_BEFORE_MESSAGE_PREFIX = "Before request [";

    public static final String DEFAULT_BEFORE_MESSAGE_SUFFIX = "]";

    public static final String DEFAULT_AFTER_MESSAGE_PREFIX = "After request [";

    public static final String DEFAULT_AFTER_MESSAGE_SUFFIX = "]";

    private final MaskingServiceSelector maskingServiceSelector;

    private boolean includeQueryString = false;

    private boolean includeClientInfo = false;

    private boolean includeHeaders = false;

    private boolean includePayload = false;

    private String beforeMessagePrefix = DEFAULT_BEFORE_MESSAGE_PREFIX;

    private String beforeMessageSuffix = DEFAULT_BEFORE_MESSAGE_SUFFIX;

    private String afterMessagePrefix = DEFAULT_AFTER_MESSAGE_PREFIX;

    private String afterMessageSuffix = DEFAULT_AFTER_MESSAGE_SUFFIX;

    public MaskingLoggingFilter(MaskingServiceSelector maskingServiceSelector){
        this.maskingServiceSelector = maskingServiceSelector;
    }

    /**
     * Set whether the query string should be included in the log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includeQueryString" in the filter definition in {@code web.xml}.
     */
    public void setIncludeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    /**
     * Return whether the query string should be included in the log message.
     */
    protected boolean isIncludeQueryString() {
        return this.includeQueryString;
    }

    /**
     * Set whether the client address and session id should be included in the
     * log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includeClientInfo" in the filter definition in {@code web.xml}.
     */
    public void setIncludeClientInfo(boolean includeClientInfo) {
        this.includeClientInfo = includeClientInfo;
    }

    /**
     * Return whether the client address and session id should be included in the
     * log message.
     */
    protected boolean isIncludeClientInfo() {
        return this.includeClientInfo;
    }

    /**
     * Set whether the request headers should be included in the log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includeHeaders" in the filter definition in {@code web.xml}.
     * @since 4.3
     */
    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    /**
     * Return whether the request headers should be included in the log message.
     * @since 4.3
     */
    public boolean isIncludeHeaders() {
        return this.includeHeaders;
    }

    /**
     * Set whether the request payload (body) should be included in the log message.
     * <p>Should be configured using an {@code <init-param>} for parameter name
     * "includePayload" in the filter definition in {@code web.xml}.
     * @since 3.0
     */
    public void setIncludePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }

    /**
     * Return whether the request payload (body) should be included in the log message.
     * @since 3.0
     */
    protected boolean isIncludePayload() {
        return this.includePayload;
    }

    /**
     * Set the maximum length of the payload body to be included in the log message.
     * Default is 50 characters.
     * @since 3.0
     */
//    public void setMaxPayloadLength(int maxPayloadLength) {
//        Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' should be larger than or equal to 0");
//        this.maxPayloadLength = maxPayloadLength;
//    }

    /**
     * Return the maximum length of the payload body to be included in the log message.
     * @since 3.0
//     */
//    protected int getMaxPayloadLength() {
//        return this.maxPayloadLength;
//    }

    /**
     * Set the value that should be prepended to the log message written
     * <i>before</i> a request is processed.
     */
    public void setBeforeMessagePrefix(String beforeMessagePrefix) {
        this.beforeMessagePrefix = beforeMessagePrefix;
    }

    /**
     * Set the value that should be appended to the log message written
     * <i>before</i> a request is processed.
     */
    public void setBeforeMessageSuffix(String beforeMessageSuffix) {
        this.beforeMessageSuffix = beforeMessageSuffix;
    }

    /**
     * Set the value that should be prepended to the log message written
     * <i>after</i> a request is processed.
     */
    public void setAfterMessagePrefix(String afterMessagePrefix) {
        this.afterMessagePrefix = afterMessagePrefix;
    }

    /**
     * Set the value that should be appended to the log message written
     * <i>after</i> a request is processed.
     */
    public void setAfterMessageSuffix(String afterMessageSuffix) {
        this.afterMessageSuffix = afterMessageSuffix;
    }


    /**
     * The default value is "false" so that the filter may log a "before" message
     * at the start of request processing and an "after" message at the end from
     * when the last asynchronously dispatched thread is exiting.
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    /**
     * Forwards the request to the next filter in the chain and delegates down to the subclasses
     * to perform the actual request logging both before and after the request is processed.
     * @see #beforeRequest
     * @see #afterRequest
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean isFirstRequest = !isAsyncDispatch(request);
        HttpServletRequest requestToUse = request;
        HttpServletResponse responseToUse = response;

        if (isIncludePayload() && isFirstRequest && !(request instanceof CachedHttpServletRequest)) {
            requestToUse = new CachedHttpServletRequest(request);
        }

        if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingResponseWrapper)) {
            responseToUse = new ContentCachingResponseWrapper(response);
        }

        boolean shouldLog = shouldLog();
        if (shouldLog && isFirstRequest) {
            beforeRequest(getBeforeMessage(requestToUse));
        }
        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            if (shouldLog && isFirstRequest) {
                ((CachedHttpServletRequest) requestToUse).cleanup();
                if (includePayload) {
                    afterRequest(getAfterMessage(responseToUse));
                }
            }
        }
    }

    /**
     * Get the message to write to the log before the request.
     * @see #createMessage
     */
    private String getBeforeMessage(HttpServletRequest request) {
        return createMessage(request, this.beforeMessagePrefix, this.beforeMessageSuffix);
    }

    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append("uri=").append(request.getRequestURI());

        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                msg.append('?').append(queryString);
            }
        }

        if (isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                msg.append(";client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append(";session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append(";user=").append(user);
            }
        }

        if (isIncludeHeaders()) {
            msg.append(";headers=").append(new ServletServerHttpRequest(request).getHeaders());
        }

        if (isIncludePayload()) {
            CachedHttpServletRequest wrapper = WebUtils.getNativeRequest(request, CachedHttpServletRequest.class);
            if (wrapper != null) {
                StringBuilder sb = new StringBuilder();
                try {
                    BufferedReader br = wrapper.getReader();
                    String str;
                    while ((str = br.readLine()) != null) {
                        sb.append(str);
                    }

                } catch (IOException ex) {
                    sb.append("[unknown]");
                }
                msg.append(";payload=").append(sb.toString());

            }
        }

        msg.append(suffix);
        MaskingService masking = maskingServiceSelector.select(MediaType.parseMediaType(request.getContentType()));
        return masking.mask(msg.toString());
    }

    /**
     * Get the message to write to the log after the request.
     * @see #createMessage
     */
    private String getAfterMessage(HttpServletResponse request) {
        return createMessage(request, this.afterMessagePrefix, this.afterMessageSuffix);
    }

    protected String createMessage(HttpServletResponse response, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);

        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        msg.append("http-status=").append(httpStatus);

        String contentType = response.getContentType();
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (isIncludeHeaders()) {
            msg.append(";headers={");
            if (httpStatus != HttpStatus.NO_CONTENT){
                msg.append("encoding=[").append(response.getCharacterEncoding()).append("]")
                        .append(",content-type=[").append(contentType).append("]");
            }
            List<String> headerNames = new ArrayList<>(new LinkedHashSet<>(response.getHeaderNames()));
            if (headerNames.isEmpty() == false) {
                for (int i = 0; i < headerNames.size(); i++) {
                    String header = headerNames.get(i);
                    msg.append(",").append(header).append("=").append(response.getHeaders(header));
                }
            }
            msg.append("}");
        }

        if (httpStatus != HttpStatus.NO_CONTENT && isIncludePayload()) {

            if (wrapper != null) {

                String payload;
                try {
                    byte[] buf = wrapper.getContentAsByteArray();
                    wrapper.copyBodyToResponse();
                    String res = new String(buf, wrapper.getCharacterEncoding());
                    payload = res;
                } catch (IOException ex) {
                    payload = "[unknown]";
                }

                if (StringUtils.isEmpty(payload) == false) {
                    msg.append(";payload=").append(payload);
                }
            }
        }

        msg.append(suffix);
        if(StringUtils.isEmpty(contentType)) {
            return msg.toString();
        }
        MaskingService masking = maskingServiceSelector.select(MediaType.parseMediaType(contentType));
        return masking.mask(msg.toString());
    }

    /**
     * Determine whether to call the {@link #beforeRequest}/{@link #afterRequest}
     * methods for the current request, i.e. whether logging is currently active
     * (and the log message is worth building).
     * <p>The default implementation always returns {@code true}. Subclasses may
     * override this with a log level check.
     * @return {@code true} if the before/after method should get called;
     * {@code false} otherwise
     * @since 4.1.5
     */
    protected boolean shouldLog() {
        return logger.isInfoEnabled();
    }

    /**
     * Writes a log message before the request is processed.
     */
    protected void beforeRequest(String message) {
        logger.info(message);
    }

    /**
     * Writes a log message after the request is processed.
     */
    protected void afterRequest(String message) {
        logger.info(message);
    }

}
