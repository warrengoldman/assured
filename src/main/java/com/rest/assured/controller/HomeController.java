package com.rest.assured.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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