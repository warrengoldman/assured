package com.rest.assured.service;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class JiraAPITest {
    private RequestSpecification spec;
    // Environment variables that must be setup (at the user level is fine)
    private static final String AUTHORIZATION = "Basic " + System.getenv("JiraAPI_key");
    private static final String BASE_URI = System.getenv("JiraAPI_URI");
    // URI variables
    private static final String REST_API_URI = "/rest/api/3/issue";
    private static final String REST_API_GET_URI = REST_API_URI + "/{key}";
    private static final String REST_API_ATTACHMENTS_URI = REST_API_GET_URI +"/attachments";
    // STATIC ticket info (this will need to be altered). These are used by 'get' and 'attach'
    private static final String KNOWN_JIRA_KEY = "SCRUM-5";
    private static final String KNOWN_JIRA_ID = "10006";
    // STATIC file to be used to attach to jira ticket.
    private static final String KNOWN_FILE_PATH = "src/main/resources/application.properties";
    // create body
    private static final String CREATE_BODY = """
        {
            "fields": {
               "project":
               {
                  "key": "SCRUM"
               },
               "summary": "a new entry by rest assured",
               "issuetype": {
                  "name": "Bug"
               }
           }
        }
        """;

    @BeforeMethod
    public void testSetup() {
        spec = given().spec(new RequestSpecBuilder().setBaseUri(BASE_URI).build())
                .header("Authorization", AUTHORIZATION);
    }

    @DataProvider(name="jiraTickets")
    private String[][] getJiraTickets() {
        // TODO could make this grab tickets from jira based on something, instead of hardcoding
        return new String[][]{ {KNOWN_JIRA_KEY, KNOWN_JIRA_ID}};
    }

    @Test(dataProvider = "jiraTickets")
    public void getJiraTicket(String key, String expectedId) {
        ValidatableResponse assertThat = spec
                .header("Content-Type", "application/json")
                .pathParam("key", key)
                .get(REST_API_GET_URI)
                .then();
        assertThat.statusCode(200);
        assertThat.body("id", equalTo(expectedId));
        System.out.println(assertThat.extract().response().body().asString());
    }

    @Test
    public void createJiraTicket() {
        ValidatableResponse assertThat = spec
                .header("Content-Type", "application/json")
                .body(CREATE_BODY)
                .post(REST_API_URI).then();
        assertThat.statusCode(201);
        String id = assertThat.extract().response().jsonPath().get("id");
        String key = assertThat.extract().response().jsonPath().get("key");
        System.out.println("id:" + id + ", key:" + key);
    }

    @DataProvider(name="jiraTicketAttachments")
    private Object[][] getJiraTicketAttachments() {
        File file = new File(KNOWN_FILE_PATH);
        if (!file.exists()) {
            throw new RuntimeException("Cannot find file:" + KNOWN_FILE_PATH);
        }
        // TODO could make this grab tickets from jira based on something, instead of hardcoding
        return new Object[][]{ {KNOWN_JIRA_KEY, KNOWN_JIRA_ID, file}};
    }

    @Test(dataProvider = "jiraTicketAttachments")
    public void updateJiraTicketAttachment(String key, String expectedId, File file) {
        ValidatableResponse assertThat = spec
                .pathParam("key", expectedId)
                .header("X-Atlassian-Token", "no-check")
                .multiPart("file", file)
                .post(REST_API_ATTACHMENTS_URI)
                .then();
        assertThat.statusCode(200);
    }
}