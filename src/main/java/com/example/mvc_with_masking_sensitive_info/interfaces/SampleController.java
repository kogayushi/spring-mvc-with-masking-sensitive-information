package com.example.mvc_with_masking_sensitive_info.interfaces;

import com.example.mvc_with_masking_sensitive_info.interfaces.dto.SampleRequestDto;
import com.example.mvc_with_masking_sensitive_info.interfaces.dto.SampleResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class SampleController {

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping("/form")
    public SampleResponseDto sample(@RequestParam String userId, @RequestParam String password) {
        log.info("I'm in form");
        return new SampleResponseDto(userId, password);

    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping(value = "/json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public SampleResponseDto sample(@RequestBody SampleRequestDto payload) {
        log.info("I'm in json");
        SampleResponseDto response = new SampleResponseDto(payload.getUserId(), payload.getPassword());
        return response;

    }
}
