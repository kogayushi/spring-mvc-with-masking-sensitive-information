package com.example.mvc_with_masking_sensitive_info.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SampleResponseDto {
    private final String userId;
    private final String password;
    private String elze = "else";
}
