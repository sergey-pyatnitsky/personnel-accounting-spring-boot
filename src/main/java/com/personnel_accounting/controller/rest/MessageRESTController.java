package com.personnel_accounting.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@RestController
public class MessageRESTController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/api/messages/{lang}/{property}")
    public ResponseEntity<?> getMessages(@PathVariable String lang, @PathVariable String property) {
        return ResponseEntity.ok(
                (messageSource.getMessage(property, null, Locale.forLanguageTag(lang)))
                        .getBytes(StandardCharsets.UTF_8)
        );
    }
}
