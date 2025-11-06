package com.rest.assured.service;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class JiraAPITest {
    // Environment variables that must be setup (at the user level is fine)
    private static final String AUTHORIZATION = "Basic " + System.getenv("JiraAPI_key");
    private static final String BASE_URI = System.getenv("JiraAPI_URI");
    // URI variables
    private static final String REST_API_URI = "/rest/api/3/issue";
    private static final String REST_API_GET_URI = REST_API_URI + "/{key}";
    private static final String REST_API_DELETE_URI = REST_API_GET_URI;
    private static final String REST_API_ATTACHMENTS_URI = REST_API_GET_URI +"/attachments";
    // STATIC file to be used to attach to jira ticket. This may need to change.
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

    private RequestSpecification getSpec() {
        return given().spec(new RequestSpecBuilder().setBaseUri(BASE_URI).build())
                .header("Authorization", AUTHORIZATION);
    }

    @DataProvider(name="jiraTickets")
    private String[][] getJiraTickets() {
        String[] jiraKeyId = createJiraTicket();
        return new String[][]{ {jiraKeyId[0], jiraKeyId[1]}};
    }

    @Test(dataProvider = "jiraTickets")
    public void getJiraTickest(String key, String expectedId) {
        ValidatableResponse assertThat = getJiraTicketInternal(key, expectedId);
        assertThat.statusCode(200);
        assertThat.body("id", equalTo(expectedId));
        deleteJiraTicket(key, expectedId);
    }
    private ValidatableResponse getJiraTicketInternal(String key, String expectedId) {
        ValidatableResponse response = getSpec()
                .header("Content-Type", "application/json")
                .pathParam("key", key)
                .get(REST_API_GET_URI)
                .then();
        return response;
    }

    @DataProvider(name="createJiraTicket")
    public String[] createJiraTicket() {
        ValidatableResponse assertThat = getSpec()
                .header("Content-Type", "application/json")
                .body(CREATE_BODY)
                .post(REST_API_URI).then();
        assertThat.statusCode(201);
        String id = assertThat.extract().response().jsonPath().get("id");
        String key = assertThat.extract().response().jsonPath().get("key");
        return new String[]{key, id};
    }

    @DataProvider(name="jiraTicketAttachments")
    private Object[][] getJiraTicketAttachments() {
        File file = new File(KNOWN_FILE_PATH);
        if (!file.exists()) {
            throw new RuntimeException("Cannot find file:" + KNOWN_FILE_PATH);
        }
        String[] jiraKeyId = createJiraTicket();
        return new Object[][]{ {jiraKeyId[0], jiraKeyId[1], file}};
    }

    @Test(dataProvider = "jiraTicketAttachments")
    public void updateJiraTicketAttachment(String key, String expectedId, File file) {
        ValidatableResponse assertThat = getSpec()
                .pathParam("key", expectedId)
                .header("X-Atlassian-Token", "no-check")
                .multiPart("file", file)
                .post(REST_API_ATTACHMENTS_URI)
                .then();
        assertThat.statusCode(200);
        // verify that ticket has attached file
        getJiraTicketInternal(key, expectedId).body("fields.attachment[0].filename", equalTo(file.getName()));
        deleteJiraTicket(key, expectedId);
    }
    private void deleteJiraTicket (String key, String expectedId) {
        getSpec().pathParam("key", expectedId).delete(REST_API_DELETE_URI).then();
        getJiraTicketInternal(key, expectedId).statusCode(404);
    }
}