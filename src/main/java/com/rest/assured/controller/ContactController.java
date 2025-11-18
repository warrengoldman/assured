package com.rest.assured.controller;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rest.assured.SomeObject;

@RestController
@RequestMapping("/contact")
public class ContactController {
    @GetMapping(path = "contact/{contactIds}", produces = MediaType.APPLICATION_JSON_VALUE, headers = "X-API-DOMAIN-CONTEXT=88063dcf-847d-4581-8c8b-565e0aa5d8c2")
    public ResponseEntity<List<SomeObject>> getContactInfo(@PathVariable("contactIds") @NotEmpty List<String> contactIds,
            @RequestParam(value = "carrierId") String carrierId) {
        List<SomeObject> carrierContactInfos = contactIds.stream().map(a -> new SomeObject(a, carrierId, "carrierContactInfo", null)).toList();
        return ResponseEntity.ok(carrierContactInfos);
    }
}