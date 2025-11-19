package com.rest.assured.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Date;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class EligibilityController {
    @GetMapping(path = "/validate")
    public ResponseEntity<SomeObject> getCheckCarrierEligibility(
            @RequestParam(value = "movementId") String movementId, @RequestBody String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        String carrierId = jsonNode.get("carrierId").asText();
        String carrierContactId = jsonNode.get("carrierContactId").asText();
        // to store the arbitrary key, name of IdentityAccessor, set to active, create date today, IdentityAccessor jsonNode
        RelatedObject ro = new RelatedObject(456, "IdentityAccessor", true, new Date(),
                jsonNode
        );
        return ResponseEntity.ok(new SomeObject(movementId, carrierId, carrierContactId, ro));
    }

    @GetMapping(path = "/validate/external")
    public ResponseEntity<SomeObject> getCheckCarrierEligibilityExternal(
            @RequestParam(value = "movementId") String movementId,
            @NotBlank @RequestParam(value = "carrierId") String carrierId,
            @NotBlank @RequestParam(value = "carrierContactId") String carrierContactId) throws Exception {
        return ResponseEntity.ok(new SomeObject(movementId, carrierId, carrierContactId, null));
    }
}