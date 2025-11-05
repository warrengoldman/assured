package com.rest.assured.service;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class OAuthTest {
    private RequestSpecification spec;
    private static final String BASE_URI = "";
    private static final String CLIENT_ID = "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "erZOWM9g3UtwNRj340YYaK_W";
    private static final String GET_AUTH_URI = "https://rahulshettyacademy.com/oauthapi/oauth2/resourceOwner/token";
    private static final String GET_CRED_URI = "https://rahulshettyacademy.com/oauthapi/getCourseDetails";

    @BeforeMethod
    public void testSetup() {
        spec = given().spec(new RequestSpecBuilder().setBaseUri(BASE_URI).build());
    }

    @Test
    public void getAuth_getCred() {
        ValidatableResponse assertThat = spec
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("grant_type", "client_credentials")
                .formParam("scope", "trust")
                .when()
                .post(GET_AUTH_URI)
                .then();
        assertThat.statusCode(200);
        String access_token = assertThat.extract().body().jsonPath().get("access_token");

        RequestSpecification getCredSpec = given().spec(new RequestSpecBuilder().setBaseUri(BASE_URI).build());
        ValidatableResponse credCall = getCredSpec.param("access_token", access_token).when().get(GET_CRED_URI).then();
        System.out.println(credCall.extract().body().asString());
    }
}
