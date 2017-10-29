package com.example.mvc_with_masking_sensitive_info.interfaces;

import com.example.mvc_with_masking_sensitive_info.interfaces.dto.SampleRequestDto;
import com.example.mvc_with_masking_sensitive_info.interfaces.dto.SampleResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@SessionAttributes(types = {})
public class SampleController {

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping(value = "/form", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public SampleResponseDto sample(@RequestParam String userId, @RequestParam String password) {
        log.info("I'm in form");
        return new SampleResponseDto(userId, password);

    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @PostMapping(value = "/json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public SampleResponseDto sample(@Validated @RequestBody SampleRequestDto payload) {
        log.info("I'm in json");
        SampleResponseDto response = new SampleResponseDto(payload.getUserId(), payload.getPassword());
        return response;

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handle(MissingServletRequestParameterException ex) {
        log.error("exception", ex);
        return ex.getMessage();
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handle2(MethodArgumentNotValidException ex) {
        log.error("exception", ex);
        return ex.getMessage();
    }
    //MethodArgumentNotValidException

    @RequestMapping(params = "_step=1")
}
