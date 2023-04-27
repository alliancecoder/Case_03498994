package org.electricaltrainingalliance.partners.companies.boundary;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.UUID;

import org.electricaltrainingalliance.partners.companies.entity.TrainingPartner;
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
@TestHTTPEndpoint(TrainingPartnersService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TrainingPartnersServiceIT {
    
    private TrainingPartner validPartner;
    private TrainingPartner invalidPartner;
    private UUID insertedId;

    @BeforeAll
    public void init() {
        this.invalidPartner = new TrainingPartner();
        invalidPartner.setPartnerName(null);
        this.validPartner = new TrainingPartner();
        validPartner.setPartnerName("VALID TRAINING PARTNER");
        
    }

	@Test
    @Order(1)
    public void invalid_post_returns_400() {
        given().contentType(ContentType.JSON).body(this.invalidPartner).when().post().then().statusCode(is(400))
        .body(is("The Training Partner is not valid, please refer to the API Documentation."));
    }

	@Test
    @Order(2)
    public void valid_post_returns_200() {
        Response response = given().contentType(ContentType.JSON).body(this.validPartner).when().post().then().statusCode(is(201))
        .body("partnerName", is(validPartner.getPartnerName())).extract().response();
        this.insertedId = UUID.fromString(response.path("partnerId"));
    }

	@Test
    @Order(3)
    public void duplicate_post_returns_409() {
        given().contentType(ContentType.JSON).body(this.validPartner).when().post().then().statusCode(is(409));
    }

    @Test
    @Order(4)
    public void get_with_valid_id_returns_200_with_json_payload() {
        given().when().get("/" + UUID.fromString("5c89a467-ae77-42e2-82db-076039f9b386"))
        .then().statusCode(is(200))
        .body("partnerName", is("TEST TRAINING PARTNER ONE"));
    }

    @Test
    @Order(5)
    public void get_with_invalid_id_returns_404() {
        given().when().get("/" + UUID.randomUUID()).then().statusCode(is(404));
    }

	@Test
	@Order(6)
	public void put_with_invalid_update_returns_400() {
		UUID id = UUID.fromString("5c89a467-ae77-42e2-82db-076039f9b386");
        TrainingPartner invalidUpdate = new TrainingPartner(null);
        invalidUpdate.setPartnerId(id);
		given().contentType(ContentType.JSON).body(invalidUpdate).when().put("/" + id).then().statusCode(is(400))
				.body(is("The Training Partner is not valid, please refer to the API Documentation."));
	}

	@Test
	@Order(7)
	public void put_with_valid_update_returns_200() {
		UUID id = UUID.fromString("5c89a467-ae77-42e2-82db-076039f9b386");
        TrainingPartner validUpdate = new TrainingPartner("TEST TRAINING PARTNER ONE - MODIFIED");
        validUpdate.setPartnerId(id);
		given().contentType(ContentType.JSON).body(validUpdate).when().put("/" + id)
        .then().statusCode(is(200))
		.body("partnerName", is(validUpdate.getPartnerName()));
	}

	@Test
	@Order(8)
	public void in_use_delete_returns_409() {
		UUID id = UUID.fromString("5c89a467-ae77-42e2-82db-076039f9b386");
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
