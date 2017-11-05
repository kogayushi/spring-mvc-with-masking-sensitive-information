package com.example.mvc_with_masking_sensitive_info.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@Data
@Component
@ConfigurationProperties("masking")
public class MaskingProperties {

    @NotNull
    private List<String> headerKeywords;
    @NotNull
    private List<String> payloadKeywords;
}
