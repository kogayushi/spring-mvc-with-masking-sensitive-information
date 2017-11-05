package com.example.mvc_with_masking_sensitive_info.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class MaskingConfiguration {

    @Bean
    public MaskingServiceSelector maskingServiceSelector(MaskingProperties prop) {
        return new MaskingServiceSelector(
                Collections.unmodifiableList(prop.getHeaderKeywords()),
                Collections.unmodifiableList(prop.getPayloadKeywords()));
    }
}
