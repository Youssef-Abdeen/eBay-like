package com.example.a3130project.registration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class registrationUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }



    @Test
    public void checkIfEmailIsValid() {
        assertTrue(RegistrationActivity.isValidEmailAddress("12345@gmail.com"));
    }

    @Test
    public void checkIfEmailIsInValid() {
        assertFalse(RegistrationActivity.isValidEmailAddress("12345gmail.com"));
    }

    @Test
    public void checkIfPasswordIsValid() {
        assertTrue(RegistrationActivity.isValidPassword("123456"));
    }

    @Test
    public void checkIfPasswordIsInValid() {
        assertFalse(RegistrationActivity.isValidPassword("123"));
    }


}