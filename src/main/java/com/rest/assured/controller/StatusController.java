package com.rest.assured.controller;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.assured.RelatedObject;
import com.rest.assured.SomeObject;

@RestController
@RequestMapping("/status")
public class StatusController {
    @PreAuthorize("hasAuthority('SCOPE_lme.admin')")
    @GetMapping(path = "/internal")
    public ResponseEntity<SomeObject> getCarrierStatusInternal(
            @RequestParam(value = "carrierId") String carrierId) throws Exception {
        return getObjects("status-internal", carrierId, null);
    }

    @GetMapping( )
    public ResponseEntity<SomeObject>  getCarrierStatus(@RequestBody(required = false) String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        return getObjects("status", jsonNode.get("carrierId").asText(), json);
    }

    @GetMapping(path = "/external")
    public ResponseEntity<SomeObject> getCarrierStatusExternal(
            @RequestParam(value = "carrierId") String carrierId) throws Exception {
        return getObjects("status-external", carrierId, null);
    }

    private ResponseEntity<SomeObject> getObjects(@PathVariable(name="some") String some, @RequestParam(name="key", required = false) String key, @RequestBody(required = false) String json) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        if (json != null && !json.isEmpty()) {
            jsonNode = mapper.readTree(json);
        }
        return ResponseEntity.ok(
            new SomeObject("widget1-"+some , "123-"+some, some,
                new RelatedObject(456, key == null || key.isEmpty() ? "cousin": key, true, new Date(),
                    jsonNode
                )
            )
        );
    }
}