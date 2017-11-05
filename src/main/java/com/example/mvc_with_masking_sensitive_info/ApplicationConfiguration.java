package com.example.mvc_with_masking_sensitive_info;

import com.example.mvc_with_masking_sensitive_info.infrastructure.filter.masking.MaskingLoggingFilter;
import com.example.mvc_with_masking_sensitive_info.service.MaskingServiceSelector;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;


@RequiredArgsConstructor
@Configuration
public class ApplicationConfiguration {
    @Bean
    public Filter filter(MaskingServiceSelector maskingServiceSelector) {
        MaskingLoggingFilter filter = new MaskingLoggingFilter(maskingServiceSelector);
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        return filter;
    }

}
