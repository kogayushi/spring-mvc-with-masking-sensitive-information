package com.example.mvc_with_masking_sensitive_info.infrastructure.filter;

public class NoMaskingSensitiveInformationService extends MaskingSensitiveInformationService {

    public NoMaskingSensitiveInformationService(MaskingSensitiveInformationProperties prop) {
        super(prop);
    }

    @Override
    protected String createSpecializedRegexInternal(String target) {
        return "^$";
    }
}
