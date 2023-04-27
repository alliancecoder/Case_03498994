package org.electricaltrainingalliance.partners.companies.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TrainingPartnerTest {

    private TrainingPartner cut;
    
    @Test
    public void can_create_valid_partner() {
        this.cut = new TrainingPartner();
        cut.setPartnerName("TEST PARTNER");
        Assertions.assertTrue(cut.isValid());
    }

    @Test
    public void missing_name_is_invalid() {
        this.cut = new TrainingPartner();
        // missing
        Assertions.assertFalse(cut.isValid());
        // empty
        cut.setPartnerName("");
        Assertions.assertFalse(cut.isValid());
        // blank
        cut.setPartnerName("     ");
        Assertions.assertFalse(cut.isValid());
    }
    
}
