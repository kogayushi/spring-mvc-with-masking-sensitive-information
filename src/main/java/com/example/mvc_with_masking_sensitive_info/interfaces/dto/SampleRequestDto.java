package com.example.mvc_with_masking_sensitive_info.interfaces.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class SampleRequestDto {
    @NotEmpty
    private String userId;
    @NotEmpty
    private String password;
}
