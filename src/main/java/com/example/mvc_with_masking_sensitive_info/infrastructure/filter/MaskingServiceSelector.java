package com.example.mvc_with_masking_sensitive_info.infrastructure.filter;

import com.example.mvc_with_masking_sensitive_info.infrastructure.filter.impl.MaskingSensitiveInformationInFormService;
import com.example.mvc_with_masking_sensitive_info.infrastructure.filter.impl.MaskingSensitiveInformationInJsonService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MaskingServiceSelector {
    private final MaskingSensitiveInformationProperties prop;
    private final Map<MediaType, MaskingSensitiveInformationService> maskingServices;

    public MaskingServiceSelector(MaskingSensitiveInformationProperties prop) {
        this.prop = prop;

        Map<MediaType, MaskingSensitiveInformationService> services = new ConcurrentHashMap<>();
        services.put(MediaType.APPLICATION_JSON, new MaskingSensitiveInformationInJsonService(prop));
        services.put(MediaType.APPLICATION_FORM_URLENCODED, new MaskingSensitiveInformationInFormService(prop));
        this.maskingServices = Collections.unmodifiableMap(services);
    }

    public MaskingSensitiveInformationService select(MediaType mediaType) {
        for (MediaType targetMediaType : maskingServices.keySet()) {
            if (targetMediaType.isCompatibleWith(mediaType)) {
                return maskingServices.get(targetMediaType);
            }
        }
        return new NoMaskingSensitiveInformationService(prop);
    }
}
