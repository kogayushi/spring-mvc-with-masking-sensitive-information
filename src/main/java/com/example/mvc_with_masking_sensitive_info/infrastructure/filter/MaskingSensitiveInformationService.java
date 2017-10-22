package com.example.mvc_with_masking_sensitive_info.infrastructure.filter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public abstract class MaskingSensitiveInformationService {

    protected final List<Pattern> regexToMaskForHeader;
    protected final List<Pattern> regexToMaskForPayload;

    public MaskingSensitiveInformationService(MaskingSensitiveInformationProperties prop) {
        List<Pattern> maskingKeywordsForHeaders = new ArrayList<>();
        for (String keyword : prop.getHeaderKeywords()) {
            maskingKeywordsForHeaders.add(Pattern.compile(MessageFormat.format("({0}=\\[)([^\\]]+)", keyword), Pattern.CASE_INSENSITIVE));
        }

        this.regexToMaskForHeader = Collections.unmodifiableList(maskingKeywordsForHeaders);

        List<Pattern> maskingKeywordsForPayload = new ArrayList<>();
        for (String keyword : prop.getPayloadKeywords()) {
            String regex = createSpecializedRegexInternal(keyword);
            maskingKeywordsForPayload.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
        }
        this.regexToMaskForPayload = Collections.unmodifiableList(maskingKeywordsForPayload);
    }

    protected abstract String createSpecializedRegexInternal(String target);

    public final String mask(String message) {
        String maskedMessage = message;
        for (Pattern pattern : this.regexToMaskForHeader) {
            maskedMessage = pattern.matcher(maskedMessage).replaceAll("$1*******");
        }
        for (Pattern pattern : this.regexToMaskForPayload) {
            maskedMessage = pattern.matcher(maskedMessage).replaceAll("$1*******");
        }
        return maskedMessage;
    }
}