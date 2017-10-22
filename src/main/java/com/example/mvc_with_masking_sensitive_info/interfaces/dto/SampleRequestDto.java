package com.example.mvc_with_masking_sensitive_info.interfaces.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SampleRequestDto {
    private String userId;
    private String password;
}
