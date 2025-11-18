package com.rest.assured.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class RootController {
    @GetMapping()
    public ResponseEntity<String> getWelcomeMessage() {
        return ResponseEntity.ok("Welcome to the Root Controller!");
    }
    @GetMapping("/status/health")
    public ResponseEntity<String> getStatusHealth() {
        return ResponseEntity.ok("Application is up and running!");
    }
}