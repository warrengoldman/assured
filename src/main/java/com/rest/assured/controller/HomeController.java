package com.rest.assured.controller;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/home")
public class HomeController {
    @GetMapping("/welcome")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Welcome to REST Assured testing application!");
    }

    @GetMapping("/get/{some}")
    public ResponseEntity<String> doGet(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        return getStringResponseEntity("get", some, key, json);
    }

    @PreAuthorize("hasAuthority('SCOPE_lme.admin')")
    @GetMapping(path = "/status/internal")
    public ResponseEntity<SomeObject> getCarrierStatusInternal(
            @RequestParam(value = "carrierId") String carrierId) throws Exception {
        return getObjects("status-internal", carrierId, null);
    }

        @GetMapping("/{some}")
    public ResponseEntity<SomeObject> getObjects(@PathVariable(name="some") String some, @RequestParam(name="key", required = false) String key, @RequestBody(required = false) String json) throws Exception{
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
    public record SomeObject(String name, String key, String type, RelatedObject relatedObject) {}
    public record RelatedObject(Integer id, String name, Boolean active, Date createDate, JsonNode requestBody) {}
    public record JsonResponse(String httpType, String path, String key, JsonNode body){}

    @GetMapping("/get/{some}/object")
    public ResponseEntity<JsonResponse> doGetRetObj(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        JsonNode jsonNode = new ObjectMapper().readTree(json);
        return ResponseEntity.ok(new JsonResponse("get", some, key, jsonNode));
    }

    @GetMapping("/get/{some}/error")
    public ResponseEntity<JsonResponse> doGetRetRemovesNode(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        JsonNode jsonNode = new ObjectMapper().readTree(json);
        JsonNode arr = jsonNode.get(key);
        if (arr.isArray()) {
            ((ArrayNode)arr).remove(0);
        }
        return ResponseEntity.ok(new JsonResponse("get", some, key, jsonNode));
    }

    @PutMapping("/put/{some}")
    public ResponseEntity<String> doPut(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        return getStringResponseEntity("put", some, key, json);
    }

    @PostMapping("/post/{some}")
    public ResponseEntity<String> doPost(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        return getStringResponseEntity("post", some, key, json);
    }

    @PatchMapping("/patch/{some}")
    public ResponseEntity<String> doPatch(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        return getStringResponseEntity("patch", some, key, json);
    }

    @DeleteMapping("/delete/{some}")
    public ResponseEntity<String> doDelete(@PathVariable(name="some") String some, @RequestParam(name="key") String key, @RequestBody String json) throws Exception{
        return getStringResponseEntity("delete", some, key, json);
    }

    private ResponseEntity<String> getStringResponseEntity(String httpType, String some, String key, String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode on = mapper.createObjectNode();
        on.put("httpType", httpType);
        on.put("path", some);
        on.put("key", key);
        on.set("body", mapper.readTree(json));
        return ResponseEntity.ok(mapper.writeValueAsString(on));
    }
}