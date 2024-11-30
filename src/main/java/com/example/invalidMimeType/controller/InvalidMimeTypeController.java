package com.example.invalidMimeType.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class InvalidMimeTypeController {

    @GetMapping()
    public ResponseEntity<String> test() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "csv");
        return ResponseEntity.ok().headers(headers).body("test");
    }
}
