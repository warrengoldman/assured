package com.rest.assured;

import com.fasterxml.jackson.databind.JsonNode;

public record JsonResponse(String httpType, String path, String key, JsonNode body){}