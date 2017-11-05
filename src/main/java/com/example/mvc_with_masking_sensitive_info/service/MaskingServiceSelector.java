package com.example.mvc_with_masking_sensitive_info.service;

import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MaskingServiceSelector {
    private final Map<MediaType, MaskingService> maskingServices;

    public MaskingServiceSelector(List<String> headerKeywords, List<String> payloadKeywords) {

        Map<MediaType, MaskingService> services = new ConcurrentHashMap<>();
        services.put(MediaType.APPLICATION_JSON, new MaskingJsonService(headerKeywords, payloadKeywords));
        services.put(MediaType.APPLICATION_FORM_URLENCODED, new MaskingFormService(headerKeywords, payloadKeywords));
        this.maskingServices = Collections.unmodifiableMap(services);
    }

    public MaskingService select(MediaType targetMediaType) {
        for (MediaType mediaType : maskingServices.keySet()) {
            if (targetMediaType.isCompatibleWith(mediaType)) {
                return maskingServices.get(mediaType);
            }
        }
        return new MaskingNoneService();
    }
}
