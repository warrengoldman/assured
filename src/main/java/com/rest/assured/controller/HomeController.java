package com.rest.assured.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/home")
public class HomeController {
    @GetMapping("/welcome")
    public ResponseEntity<String> getStatus(Principal principal) {
        return ResponseEntity.ok("Welcome to REST Assured testing application %s!".formatted(principal.getName()));
    }
}
