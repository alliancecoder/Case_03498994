package org.electricaltrainingalliance.partners.contacts.boundary;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.util.UUID;

import org.electricaltrainingalliance.partners.companies.entity.TrainingPartner;
import org.electricaltrainingalliance.partners.contacts.entity.PartnerContact;
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
@TestHTTPEndpoint(PartnerContactsService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PartnerContactsServiceIT {

    private TrainingPartner partner;
    private ContactType type;
    private PartnerContact validContact;
    private PartnerContact invalidContact;
    private UUID insertedId;

    @BeforeAll
    public void init() {
        this.partner = new TrainingPartner("TEST TRAINING PARTNER ONE");
        partner.setPartnerId(UUID.fromString("5c89a467-ae77-42e2-82db-076039f9b386"));
        this.type = new ContactType("FIRST");
        type.setTypeId(UUID.fromString("422daaba-be43-4405-8a85-521b5ecfb26a"));
        this.validContact = new PartnerContact();
        validContact.setFirstName("Develeopment");
        validContact.setLastName("Tester");
        validContact.setEmailAddress("userb@company.com");
        validContact.setTrainingPartner(partner);
        validContact.setType(type);
        this.invalidContact = new PartnerContact();
        invalidContact.setTrainingPartner(partner);
    }

	@Test
    @Order(1)
    public void invalid_post_returns_400() {
        given().contentType(ContentType.JSON).body(this.invalidContact).when().post().then().statusCode(is(400))
        .body(is("The Contact is not valid, please refer to the API Documentation."));
    }

	@Test
    @Order(2)
    public void valid_post_returns_200() {
        Response response = given().contentType(ContentType.JSON).body(this.validContact).when().post().then().statusCode(is(201))
        .body("firstName", is(validContact.getFirstName())).extract().response();
        this.insertedId = UUID.fromString(response.path("contactId"));
    }

	@Test
    @Order(3)
    public void duplicate_post_returns_409() {
        given().contentType(ContentType.JSON).body(this.validContact).when().post().then().statusCode(is(409));
    }

    @Test
    @Order(4)
    public void get_with_valid_id_returns_200_with_json_payload() {
        given().when().get("/" + UUID.fromString("422daaba-be43-4405-8a85-521b5ecfb26a"))
        .then().statusCode(is(200))
        .body("firstName", is("FIRST"));
    }

    @Test
    @Order(5)
    public void get_with_invalid_id_returns_404() {
        given().when().get("/" + UUID.randomUUID()).then().statusCode(is(404));
    }

    @Test
    @Order(6)
    public void get_returns_200_with_payload() {
        given().when().get()
        .then().statusCode(is(200))
        .body("size()", is(2));
    }

	@Test
	@Order(7)
	public void put_with_invalid_update_returns_400() {
		UUID id = UUID.fromString("422daaba-be43-4405-8a85-521b5ecfb26a");
        PartnerContact invalidUpdate = new PartnerContact();
        invalidUpdate.setContactId(id);
		given().contentType(ContentType.JSON).body(invalidUpdate).when().put("/" + id).then().statusCode(is(400))
				.body(is("The Contact is not valid, please refer to the API Documentation."));
	}

	@Test
	@Order(8)
	public void put_with_valid_update_returns_200() {
        this.validContact.setContactId(insertedId);
        validContact.setMobilePhone("1234567890");
		given().contentType(ContentType.JSON).body(validContact).when().put("/" + insertedId)
        .then().statusCode(is(200))
		.body("mobilePhone", is(validContact.getMobilePhone()));
	}

    // TODO uncomment test after in-use is in place
	// @Test
	// @Order(9)
	// public void in_use_delete_returns_409() {
	// 	UUID id = UUID.fromString("422daaba-be43-4405-8a85-521b5ecfb26a");
	// 	given().when().delete("/" + id)
    //     .then().statusCode(is(409));
	// }

	@Test
	@Order(10)
	public void delete_returns_204() {;
		given().when().delete("/" + this.insertedId)
        .then().statusCode(is(204));
	}
        
}
