package com.rest.assured.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rest.assured.SomeObject;

@RestController
public class CarrierController {
    @GetMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE, headers = "X-API-DOMAIN-CONTEXT=88063dcf-847d-4581-8c8b-565e0aa5d8c2")
    public ResponseEntity<SomeObject> getCarrier(@RequestParam(value = "carrierId") String carrierId) {
        return ResponseEntity.ok(new SomeObject("info", carrierId, "getCarrier", null));
    }
}
