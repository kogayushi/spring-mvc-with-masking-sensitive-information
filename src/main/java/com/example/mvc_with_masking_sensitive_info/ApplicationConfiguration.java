package com.example.mvc_with_masking_sensitive_info;

import com.example.mvc_with_masking_sensitive_info.infrastructure.filter.MaskingRequestLoggingFilter;
import com.example.mvc_with_masking_sensitive_info.infrastructure.filter.MaskingSensitiveInformationProperties;
import com.example.mvc_with_masking_sensitive_info.infrastructure.filter.MaskingServiceSelector;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;


@RequiredArgsConstructor
@Configuration
public class ApplicationConfiguration {
    private final MaskingSensitiveInformationProperties prop;

    @Bean
    public MaskingServiceSelector maskingServiceSelector() {
        return new MaskingServiceSelector(prop);
    }

    @Bean
    public Filter filter() {
        MaskingRequestLoggingFilter filter = new MaskingRequestLoggingFilter(maskingServiceSelector());
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        filter.setAfterMessagePrefix("Request [");
        filter.setMaxPayloadLength(4096);

        return filter;
    }
}
