package com.example.mvc_with_masking_sensitive_info.infrastructure.filter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("masking.sensitive-information")
public class MaskingSensitiveInformationProperties {

    private List<String> headerKeywords;
    private List<String> payloadKeywords;
}
