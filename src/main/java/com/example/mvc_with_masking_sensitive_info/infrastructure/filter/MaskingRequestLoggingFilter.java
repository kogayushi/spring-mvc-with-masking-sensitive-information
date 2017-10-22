package com.example.mvc_with_masking_sensitive_info.infrastructure.filter;


import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
public class MaskingRequestLoggingFilter extends CommonsRequestLoggingFilter {

    private final MaskingServiceSelector selector;

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return logger.isDebugEnabled();
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        // do nothing
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        MediaType requestMediaType = getMediaTypeFrom(request);
        String maskedMessage = maskMessage(requestMediaType, message);
        logger.debug(maskedMessage);
    }

    private MediaType getMediaTypeFrom(HttpServletRequest request) {
        return MediaType.parseMediaType(request.getHeader("content-type"));
    }

    private String maskMessage(MediaType requestMediaType, String message) {
        MaskingSensitiveInformationService maskingService = selector.select(requestMediaType);
        return maskingService.mask(message);
    }
}