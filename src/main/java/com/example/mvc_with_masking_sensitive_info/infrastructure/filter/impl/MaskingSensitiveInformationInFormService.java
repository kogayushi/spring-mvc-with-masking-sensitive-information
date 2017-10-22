package com.example.mvc_with_masking_sensitive_info.infrastructure.filter.impl;


import com.example.mvc_with_masking_sensitive_info.infrastructure.filter.MaskingSensitiveInformationProperties;
import com.example.mvc_with_masking_sensitive_info.infrastructure.filter.MaskingSensitiveInformationService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class MaskingSensitiveInformationInFormService extends MaskingSensitiveInformationService {

    public MaskingSensitiveInformationInFormService(MaskingSensitiveInformationProperties prop) {
        super(prop);
    }

    @Override
    protected String createSpecializedRegexInternal(String keyword) {
        return MessageFormat.format("({0}=)([^&]+)",keyword);
    }
}
