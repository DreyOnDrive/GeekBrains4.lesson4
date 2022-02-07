package com.geekbrains.test.spoonacular;

import java.io.IOException;

import com.geekbrains.test.AddingResponse;
import com.geekbrains.test.ConnectingResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpoonacularTest extends AbstractTest {

    private static final String API_KEY = "1cbcde369d1243cfb7f586b8c5b84fa2";
    private static RequestSpecification BASE_SPEC;
    private static ResponseSpecification RESPONSE_SPEC;
    private static ConnectingResponse responseConnecting = new ConnectingResponse();

    @BeforeAll
    static void beforeAll() {

        RestAssured.baseURI = "https://api.spoonacular.com";

        BASE_SPEC = new RequestSpecBuilder()
                .addQueryParam("apiKey", API_KEY)
                .log(LogDetail.ALL)
                .build();

        RESPONSE_SPEC = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .log(LogDetail.ALL)
                .build();

    }

    @Test
    @Order(1)
    void testUserConnecting() throws IOException {

        String connected = getResourceAsString("connected.json");

        responseConnecting =
                given()
                .spec(BASE_SPEC)
                .log()
                .all()
                .body(connected)
                .expect()
                .body("status", is("success"))
                .body("username",notNullValue())
                .body("spoonacularPassword",notNullValue())
                .body("hash",notNullValue())
                .spec(RESPONSE_SPEC)
                .log()
                .all()
                .when()
                .post("users/connect")
                .as(ConnectingResponse.class);

        ConnectingResponse expectedConnecting = ConnectingResponse.builder()
                .status("success")
                .build();

        Assertions.assertEquals(expectedConnecting.getStatus(), responseConnecting.getStatus());
    }

    @Test
    @Order(2)
    void testMealAddToPlan() throws IOException {
        String added = getResourceAsString("added.json");

        AddingResponse responseAdding =
                given()
                        .spec(BASE_SPEC)
                        .log()
                        .all()
                        .queryParam("hash", responseConnecting.getHash())
                        .body(added)
                        .expect()
                        .body("status", is("success"))
                        .body("id",notNullValue())
                        .spec(RESPONSE_SPEC)
                        .log()
                        .all()
                        .when()
                        .post("mealplanner/" + responseConnecting.getUsername() + "/items")
                        .as(AddingResponse.class);

        ConnectingResponse expectedAdding = ConnectingResponse.builder()
                .status("success")
                .build();

        Assertions.assertEquals(expectedAdding.getStatus(), responseAdding.getStatus());
    }

    @Test
    @Order(3)
    void testMealAddToPlanTemplate() throws IOException {
        String plan = getResourceAsString("plan.json");

                given()
                        .spec(BASE_SPEC)
                        .log()
                        .all()
                        .queryParam("hash", responseConnecting.getHash())
                        .body(plan)
                        .expect()
                        .body("status", is("success"))
                        .body("mealPlan.id",notNullValue())
                        .body("mealPlan.name",notNullValue())
                        .body("mealPlan.days",notNullValue())
                        .spec(RESPONSE_SPEC)
                        .log()
                        .all()
                        .when()
                        .post("mealplanner/" + responseConnecting.getUsername() + "/templates");
    }

    @Test
    @Order(4)
    void testMealAddToShoppingList() throws IOException {
        String shop = getResourceAsString("shop.json");

        given()
                .spec(BASE_SPEC)
                .log()
                .all()
                .queryParam("hash", responseConnecting.getHash())
                .body(shop)
                .expect()
                .body("id", notNullValue())
                .body("name", containsStringIgnoringCase("baking powder"))
                .body("cost", is(greaterThan(0f)))
                .body("ingredientId", notNullValue() )
                .spec(RESPONSE_SPEC)
                .log()
                .all()
                .when()
                .post("mealplanner/" + responseConnecting.getUsername() + "/shopping-list/items");
    }
}