package org.electricaltrainingalliance.sessions.types.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SessionTypeTest {

    private SessionType cut;
    
    @Test
    public void can_create_valid_session_type() {
        this.cut = new SessionType();
        cut.setTypeName("TEST TYPE");
        Assertions.assertTrue(cut.isValid());
    }

    @Test
    public void missing_name_is_invalid() {
        this.cut = new SessionType();
        // missing
        Assertions.assertFalse(cut.isValid());
        // empty
        cut.setTypeName("");
        Assertions.assertFalse(cut.isValid());
        // blank
        cut.setTypeName("     ");
        Assertions.assertFalse(cut.isValid());
    }
}
