package com.example.mvc_with_masking_sensitive_info.service;

import java.util.Collections;

class MaskingNoneService extends MaskingService {

    MaskingNoneService() {
        super(Collections.emptyList(),Collections.emptyList());
    }

    @Override
    protected String createSpecializedRegexInternal(String target) {
        return "^$";
    }
}
