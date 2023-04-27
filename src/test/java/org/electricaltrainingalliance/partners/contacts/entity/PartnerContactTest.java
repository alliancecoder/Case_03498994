package org.electricaltrainingalliance.partners.contacts.entity;

import org.electricaltrainingalliance.partners.companies.entity.TrainingPartner;
import org.electricaltrainingalliance.partners.types.entity.ContactType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
public class PartnerContactTest {

    private TrainingPartner partner;
    private ContactType type;
    private PartnerContact cut;

    @BeforeAll
    public void init() {  
        this.partner = new TrainingPartner("VALID PARTNER");
        this.type = new ContactType("VALID TYPE");
    }
    
    @Test
    public void can_create_valid_contact() {
        this.cut = new PartnerContact();
        cut.setType(type);
        cut.setTrainingPartner(partner);
        cut.setFirstName("FIRST");
        cut.setLastName("LAST");
        cut.setEmailAddress("userc@company.com");
        Assertions.assertTrue(cut.isValid());
    }

    @Test
    public void missing_type_is_invalid() {
        this.cut = new PartnerContact();
        cut.setTrainingPartner(partner);
        cut.setFirstName("FIRST");
        cut.setLastName("LAST");
        cut.setEmailAddress("userc@company.com");
        Assertions.assertFalse(cut.isValid());
    }

    @Test
    public void missing_partner_is_invalid() {
        this.cut = new PartnerContact();
        cut.setType(type);
        cut.setFirstName("FIRST");
        cut.setLastName("LAST");
        cut.setEmailAddress("userc@company.com");
        Assertions.assertFalse(cut.isValid());
    }

    @Test
    public void missing_first_is_invalid() {
        this.cut = new PartnerContact();
        cut.setType(type);
        cut.setTrainingPartner(partner);
        cut.setLastName("LAST");
        cut.setEmailAddress("userc@company.com");
        // missing
        Assertions.assertFalse(cut.isValid());
        // empty
        cut.setFirstName("");
        Assertions.assertFalse(cut.isValid());
        // blank
        cut.setFirstName("     ");
        Assertions.assertFalse(cut.isValid());
    }

    @Test
    public void missing_last_is_invalid() {
        this.cut = new PartnerContact();
        cut.setType(type);
        cut.setTrainingPartner(partner);
        cut.setFirstName("FIRST");
        cut.setEmailAddress("userc@company.com");
        // missing
        Assertions.assertFalse(cut.isValid());
        // empty
        cut.setLastName("");
        Assertions.assertFalse(cut.isValid());
        // blank
        cut.setLastName("     ");
        Assertions.assertFalse(cut.isValid());
    }

    @Test
    public void missing_email_is_invalid() {
        this.cut = new PartnerContact();
        cut.setType(type);
        cut.setTrainingPartner(partner);
        cut.setFirstName("FIRST");
        cut.setLastName("LAST");
        // missing
        Assertions.assertFalse(cut.isValid());
        // empty
        cut.setEmailAddress("");
        Assertions.assertFalse(cut.isValid());
        // blank
        cut.setEmailAddress("     ");
        Assertions.assertFalse(cut.isValid());
    }
    
}
