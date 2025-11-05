package com.rest.assured.service;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

import static io.restassured.RestAssured.given;

public class OAuthTest {
    private RequestSpecification spec;
    private static final String BASE_URI = "https://rahulshettyacademy.com";
    private static final String CLIENT_ID = "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "erZOWM9g3UtwNRj340YYaK_W";
    private static final String GET_AUTH_URI = "/oauthapi/oauth2/resourceOwner/token";
    private static final String GET_CRED_URI = "/oauthapi/getCourseDetails";

    @BeforeMethod
    public void testSetup() {
        spec = given().spec(new RequestSpecBuilder().setBaseUri(BASE_URI).build());
    }

    @DataProvider(name="getAuth_getCred")
    public ExtractableResponse[] getAuth_getCred() {
        if (spec == null) { testSetup(); }
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
        return new ExtractableResponse[]{credCall.extract()};
    }

    public record Course(String courseTitle, String price) {}
    public record Courses(List<Course> webAutomation, List<Course> api, List<Course> mobile){}
    public record Curriculum(String instructor, String url, String services, String expertise, Courses courses, String linkedIn) {}

    @Test(dataProvider = "getAuth_getCred")
    public void turnToObject(ExtractableResponse extractableResponse) {
        Curriculum curriculum = extractableResponse.as(Curriculum.class);
        Consumer<Course> printCourse = course ->
                System.out.println(course.courseTitle + ":" + course.price);

        System.out.println("Instructor: " + curriculum.instructor);
        System.out.println("Url: " + curriculum.url);
        System.out.println("Services: " + curriculum.services);
        System.out.println("Expertise: "  + curriculum.expertise);
        System.out.println("\nwebAutomation courses:");
        curriculum.courses.webAutomation.forEach(printCourse);
        System.out.println("\napi courses:");
        curriculum.courses.api.forEach(printCourse);
        System.out.println("\nmobile courses:");
        curriculum.courses.mobile.forEach(printCourse);
        System.out.println("\nLinkedIn: " + curriculum.linkedIn);
    }

    @Test(dataProvider = "getAuth_getCred")
    public void printJsonPath(ExtractableResponse extractableResponse) {
        JsonPath jsonPath = extractableResponse.jsonPath();
        Collection<LinkedHashMap<String, ?>> webAutomations = jsonPath.get("courses.webAutomation");
        Collection<LinkedHashMap<String, ?>> apis = jsonPath.get("courses.api");
        Collection<LinkedHashMap<String, ?>> mobiles = jsonPath.get("courses.mobile");
        Consumer<LinkedHashMap<String, ?>> printCourse = course ->
            System.out.println(course.get("courseTitle") + ":" + course.get("price"));

        System.out.println("Instructor: " + jsonPath.get("instructor").toString());
        System.out.println("Url: " + jsonPath.get("url").toString());
        System.out.println("Services: " + jsonPath.get("services").toString());
        System.out.println("Expertise: " + jsonPath.get("expertise").toString());
        System.out.println("\nwebAutomation courses:");
        webAutomations.forEach(printCourse);
        System.out.println("\napi courses:");
        apis.forEach(printCourse);
        System.out.println("\nmobile courses:");
        mobiles.forEach(printCourse);
        System.out.println("\nLinkedIn: " + jsonPath.get("linkedIn").toString());
    }
}
