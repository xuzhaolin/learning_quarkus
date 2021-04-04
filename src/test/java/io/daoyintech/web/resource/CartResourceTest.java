package io.daoyintech.web.resource;

import io.daoyintech.domain.enums.CartStatus;
import io.daoyintech.utils.TestContainerResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.Test;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import java.sql.SQLException;
import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static javax.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;


@DisabledOnNativeImage
@QuarkusTest
@QuarkusTestResource(TestContainerResource.class)
public class CartResourceTest {

    private static final String INSERT_WRONG_CART_IN_DB =
            "insert into carts values (999, current_timestamp, current_timestamp, 'NEW', 3)";

    private static final String DELETE_WRONG_CART_IN_DB =
            "delete from carts where id = 999";

    @Inject
    DataSource dataSource;

    @Test
    void testFindAll() {
        get("/carts")
                .then()
                .statusCode(OK.getStatusCode())
                .body("size()", greaterThan(0));
    }

    @Test
    void testFindAllActiveCarts() {
        get("/carts/active").then().statusCode(OK.getStatusCode());
    }

    @Test
    void testGetActiveCartForCustomer() {
        get("/carts/customer/3").then().statusCode(OK.getStatusCode()).body(containsString("Peter"));
    }

    @Test
    void testFindById() {
        get("/carts/3").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("status"))
                .body(containsString("NEW"));

        get("/carts/100").then()
                .statusCode(NO_CONTENT.getStatusCode())
                .body(emptyOrNullString());
    }

    @Test
    void testDelete() {
        get("/carts/active").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("NEW"));

        delete("/carts/1").then()
                .statusCode(NO_CONTENT.getStatusCode());

        get("/carts/1").then()
                .statusCode(OK.getStatusCode())
                .body(containsString("Jason"))
                .body(containsString("CANCELED"));

    }

    @Test
    void testCreateCart() {
        var requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");

        var newCustomerId = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams).post("/customers").then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getInt("id");

        var response = post("/carts/customer/" + newCustomerId).then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getMap("$");

        assertThat(response.get("id")).isNotNull();
        assertThat(response).containsEntry("status", CartStatus.NEW.name());

        delete("/carts/" + response.get("id")).then()
                .statusCode(NO_CONTENT.getStatusCode());

        delete("/customers/" + newCustomerId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }


    @Test
    void testFailCreateCartWhileHavingAlreadyActiveCart() {
        var requestParams = new HashMap<>();
        requestParams.put("firstName", "Saul");
        requestParams.put("lastName", "Berenson");
        requestParams.put("email", "call.saul@mail.com");
        var newCustomerId = given().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(requestParams)
                .post("/customers")
                .then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        var newCartId = post("/carts/customer/" + newCustomerId).then()
                .statusCode(OK.getStatusCode())
                .extract()
                .jsonPath()
                .getLong("id");

        post("/carts/customer/" + newCustomerId).then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body(containsString(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body(containsString("There is already an active cart"));

        assertThat(newCartId).isNotNull();

        delete("/carts/" + newCartId).then()
                .statusCode(NO_CONTENT.getStatusCode());

        delete("/customers/" + newCustomerId).then()
                .statusCode(NO_CONTENT.getStatusCode());
    }

    @Test
    void testGetActiveCartForCustomerWhenThereAreTwoCartsInDB() {
        executeSQL(INSERT_WRONG_CART_IN_DB);

        get("/carts/customer/3").then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body(containsString(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body(containsString("Many active carts detected !!!"));

        executeSQL(DELETE_WRONG_CART_IN_DB);
    }

    private void executeSQL(String sql) {
        try (var connection = dataSource.getConnection()){
            var statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
