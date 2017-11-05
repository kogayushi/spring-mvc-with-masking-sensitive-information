package com.example.mvc_with_masking_sensitive_info.service;

import java.text.MessageFormat;
import java.util.List;

class MaskingFormService extends MaskingService {
    MaskingFormService(List<String> headerKeywords, List<String> payloadKeywords) {
        super(headerKeywords, payloadKeywords);
    }

    @Override
    protected String createSpecializedRegexInternal(String keyword) {
        return MessageFormat.format("({0}=)([^&]+)", keyword);
    }
}
