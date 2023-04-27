package org.electricaltrainingalliance.partners.types.boundary;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.UUID;

import org.electricaltrainingalliance.partners.types.entity.ContactType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
@TestHTTPEndpoint(ContactTypesService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactTypesServiceIT {

    private ContactType validType;
    private ContactType invalidType;
    private UUID insertedId;

    @BeforeAll
    public void init() {
        this.invalidType = new ContactType();
        invalidType.setTypeName(null);
        this.validType = new ContactType();
        validType.setTypeName("VALID CONTACT TYPE");
        
    }

	@Test
    @Order(1)
    public void invalid_post_returns_400() {
        given().contentType(ContentType.JSON).body(this.invalidType).when().post().then().statusCode(is(400))
        .body(is("The Contact Type is not valid, please refer to the API Documentation."));
    }

	@Test
    @Order(2)
    public void valid_post_returns_200() {
        Response response = given().contentType(ContentType.JSON).body(this.validType).when().post().then().statusCode(is(201))
        .body("typeName", is(validType.getTypeName())).extract().response();
        this.insertedId = UUID.fromString(response.path("typeId"));
    }

	@Test
    @Order(3)
    public void duplicate_post_returns_409() {
        given().contentType(ContentType.JSON).body(this.validType).when().post().then().statusCode(is(409));
    }

    @Test
    @Order(4)
    public void get_with_valid_id_returns_200_with_json_payload() {
        given().when().get("/" + UUID.fromString("85d0baa8-736d-43be-b1aa-c29f06cfeb24"))
        .then().statusCode(is(200))
        .body("typeName", is("TEST CONTACT TYPE ONE"));
    }

    @Test
    @Order(5)
    public void get_with_invalid_id_returns_404() {
        given().when().get("/" + UUID.randomUUID()).then().statusCode(is(404));
    }

	@Test
	@Order(6)
	public void put_with_invalid_update_returns_400() {
		UUID id = UUID.fromString("85d0baa8-736d-43be-b1aa-c29f06cfeb24");
        ContactType invalidUpdate = new ContactType(null);
        invalidUpdate.setTypeId(id);
		given().contentType(ContentType.JSON).body(invalidUpdate).when().put("/" + id).then().statusCode(is(400))
				.body(is("The Contact Type is not valid, please refer to the API Documentation."));
	}

	@Test
	@Order(7)
	public void put_with_valid_update_returns_200() {
		UUID id = UUID.fromString("85d0baa8-736d-43be-b1aa-c29f06cfeb24");
        ContactType validUpdate = new ContactType("TEST CONTACT TYPE ONE - MODIFIED");
        validUpdate.setTypeId(id);
		given().contentType(ContentType.JSON).body(validUpdate).when().put("/" + id)
        .then().statusCode(is(200))
		.body("typeName", is(validUpdate.getTypeName()));
	}

	@Test
	@Order(8)
	public void in_use_delete_returns_409() {
		UUID id = UUID.fromString("85d0baa8-736d-43be-b1aa-c29f06cfeb24");
		given().when().delete("/" + id)
        .then().statusCode(is(409));
	}

	@Test
	@Order(9)
	public void delete_returns_204() {;
		given().when().delete("/" + this.insertedId)
        .then().statusCode(is(204));
	}
    
}
