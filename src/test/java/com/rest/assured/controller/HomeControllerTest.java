package com.rest.assured.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class HomeControllerTest {
    private RequestSpecification spec;
    private static final String BASE_URI = "http://localhost:8080/home";

    @BeforeMethod
    public void testSetup() {
        spec = given().spec(new RequestSpecBuilder().setBaseUri(BASE_URI).build());
    }

    @Test
    public void doGetObject() throws Exception {
        Response response =
                spec
                        .queryParam("key", "somefilter")
                        .header("Content-Type", "application/json")
                        .body("""
                    {
                        "product": "Cheese Grater",
                        "product-data": {
                            "price": 12.54,
                            "weight": "12 lbs."
                        }
                    }
                    """)
                        .when().get("/get/product/object");
        ValidatableResponse assertThat = response.then();
        assertThat.statusCode(200);
        assertThat.body("httpType", equalTo("get"));
        assertThat.body("body.product", equalTo("Cheese Grater"));
        assertThat.body("body.product-data.price", equalTo(12.54));
        assertThat.body("body.product-data.weight", equalTo("12 lbs."));
    }

    @Test
    public void doGet() throws Exception {
        Response response =
                spec
                        .queryParam("key", "somefilter")
                        .header("Content-Type", "application/json")
                        .body("""
                    {
                        "product": "Cheese Grater"
                    }
                    """)
                        .when().get("/get/product");
        response.then().assertThat().statusCode(200);
        String jsonBody = response.body().asString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonBody);
        assertThat(jsonNode.get("httpType").asText(), equalTo("get"));
        assertThat(jsonNode.get("body").get("product").asText(), equalTo("Cheese Grater"));
    }

    @Test // test only exists to illustrate where logging can be placed to allow for debugging
    public void doGet_withLogging() throws Exception {
        Response response =
                spec
                    .log().all() //  this will log the request data, some options besides all
                        // method, uri, body, parameters, cookies, headers
                    .queryParam("key", "somefilter")
                    .header("Content-Type", "application/json")
                    .body("""
                        {
                            "product": "Cheese Grater"
                        }
                    """)
                    .when().get("/get/product");
        response.then()
                .log().all() //  this will log the response data, some options besides all
                // body, cookies, headers, status
                .assertThat().statusCode(200);
    }
}
