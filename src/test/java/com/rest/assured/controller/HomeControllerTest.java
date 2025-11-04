package com.rest.assured.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

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
    public void doGetObject() {
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
    public void doGet_withLogging() {
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

    @Test
    public void doGet_extract_response()  {
        Response request =
            spec
                .queryParam("key", "somefilter")
                .header("Content-Type", "application/json")
                .body("""
                   {
                        "product": "Cheese Grater",
                        "orders":
                            [
                                {
                                    "qty": 1,
                                    "price": 12.55,
                                    "custId": "Jack Smith-123"
                                },
                                {
                                    "qty": 23,
                                    "price": 8.55,
                                    "custId": "Big Company-346"
                                },
                                {
                                    "qty": 5,
                                    "price": 10.55,
                                    "custId": "Med Company-789"
                                },
                                {
                                    "qty": 44,
                                    "price": 7.55,
                                    "custId": "Big Company-346"
                                }
                            ]
                    }
                """)
                .when().get("/get/product/object");
        ValidatableResponse response = request.then();
        response.body("httpType", equalTo("get"));
        response.body("body.product", equalTo("Cheese Grater"));
        response.body("body.orders.size()", equalTo(4));
        ExtractableResponse<Response> extractedResponse = response.extract();
        assertThat(extractedResponse.contentType(), equalTo("application/json"));
        ResponseBodyExtractionOptions body = extractedResponse.body();
        assertThat(body.jsonPath().get("httpType"), equalTo("get"));
        System.out.println("Price:" + body.jsonPath().get("body.orders[1].price"));
        System.out.println("CustId:" + body.jsonPath().get("body.orders[2].custId"));
        Collection<LinkedHashMap<String, ?>> orders = body.jsonPath().get("body.orders");
        for (LinkedHashMap<String, ?> order : orders) {
            System.out.println(order.get("qty"));
            System.out.println(order.get("price"));
            System.out.println(order.get("custId"));
        }
    }

    public record Order(int orderId, int qty, double price, String custId) {}
    public record Product(String product, List<Order> orders) {}
    private List<Order> getOrders() {
        return List.of(
                new Order(1,1, 12.55, "Jim Smits-123"),
                new Order(2, 22, 10.55, "Davey Jones-456"),
                new Order(3,12, 11.55, "Marco Polo-789"),
                new Order(4,44, 8.55, "Davey Jones-456")
        );
    }
    private Product getProduct() {
        return new Product("Cheese Grater", getOrders());
    }

    @Test
    public void doGet_passing_object()  {
        Product expectedProduct = getProduct();
        String expectedPath = "product";
        String expectedKey = "some filter";
        String expectedHttpType = "get";
        Response request =
                spec
                        .queryParam("key", expectedKey)
                        .header("Content-Type", "application/json")
                        .body(expectedProduct)
                        .when().get("/"+expectedHttpType+"/"+expectedPath+"/object");
        ValidatableResponse response = request.then();
        response.body("httpType", equalTo(expectedHttpType));
        response.body("path", equalTo(expectedPath));
        response.body("key", equalTo(expectedKey));
        response.body("body.product", equalTo(expectedProduct.product));
        Collection<LinkedHashMap<String, ?>> orders = response.extract().body().jsonPath().get("body.orders");
        // fail if they are not the same size
        assertThat(orders.size(), equalTo(expectedProduct.orders.size()));
        List<?> changedElements =
                orders.stream()
                        .filter(obj1 -> expectedProduct.orders.stream()
                                .anyMatch(obj2 ->
                                        (int)obj1.get("orderId") == obj2.orderId()
                                                && (int)obj1.get("qty") != obj2.qty()
                                                && (float)obj1.get("price") != (float)obj2.price()
                                                && !obj1.get("custId").equals(obj2.custId())))
                        .toList();
        assertThat(changedElements.size(), equalTo(0));
    }

    @Test
    public void doGet_missing_object()  {
        Product expectedProduct = getProduct();
        String expectedPath = "product";
        String expectedKey = "some filter";
        String expectedHttpType = "get";
        Response request =
                spec
                        .queryParam("key", expectedKey)
                        .header("Content-Type", "application/json")
                        .body(expectedProduct)
                        // the .get call dictates the actual http method
                        // however in our test controller, the httpType is
                        // ALSO part of the path (mainly for documentation)
                        .when().get("/"+expectedHttpType+"/"+expectedPath+"/object");
        ValidatableResponse response = request.then();
        response.body("httpType", equalTo(expectedHttpType));
        response.body("path", equalTo(expectedPath));
        response.body("key", equalTo(expectedKey));
        response.body("body.product", equalTo(expectedProduct.product));
        Collection<LinkedHashMap<String, ?>> orders = response.extract().body().jsonPath().get("body.orders");
        assertThat(orders.size(), equalTo(expectedProduct.orders.size()));
        List<?> changedElements =
                orders.stream()
                        .filter(obj1 -> expectedProduct.orders.stream()
                                .anyMatch(obj2 ->
                                        (int)obj1.get("orderId") == obj2.orderId()
                                                && (int)obj1.get("qty") != obj2.qty()
                                                && (float)obj1.get("price") != (float)obj2.price()
                                                && !obj1.get("custId").equals(obj2.custId())))
                        .toList();
        assertThat(changedElements.size(), equalTo(0));
    }
}
