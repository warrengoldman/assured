package com.rest.assured;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

public record RelatedObject(Integer id, String name, Boolean active, Date createDate, JsonNode requestBody) {}