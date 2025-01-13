package org.goit.urlshortener.model.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.goit.urlshortener.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserValidationTest {
    private static Validator validator;

    private static List<User> userWithInvalidEmails;
    private static List<User> userWithValidEmails;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        userWithInvalidEmails = List.of(
                User.builder().email("").password("12345Test").build(),
                User.builder().email("invalidemail").password("12345Test").build(),
                User.builder().email("@example.com").password("12345Test").build(),
                User.builder().email("user@.com").password("12345Test").build(),
                User.builder().email("user@domain..com").password("12345Test").build(),
                User.builder().email("user@domain.c").password("12345Test").build(),
                User.builder().email("user@domain,com").password("12345Test").build(),
                User.builder().email("user@domain@another.com").password("12345Test").build(),
                User.builder().email("user@123.123.123.123.456").password("12345Test").build()
        );

        userWithValidEmails = List.of(
                User.builder().email("john.doe@example.com").password("12345Test").build(),
                User.builder().email("user+tag@example.com").password("12345Test").build(),
                User.builder().email("user.name+tag@domain.co.uk").password("12345Test").build(),
                User.builder().email("support@company.io").password("12345Test").build() ,
                User.builder().email("user.name@example.travel").password("12345Test").build(),
                User.builder().email("user-name@domain.name").password("12345Test").build(),
                User.builder().email("contact@domain.academy").password("12345Test").build(),
                User.builder().email("example_email@domain.info").password("12345Test").build(),
                User.builder().email("admin@mailserver.com").password("12345Test").build()
        );
    }

    @Test
    void shouldRejectUser_WhenEmailIsInvalid() {
        for (User user : userWithInvalidEmails) {
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertFalse(violations.isEmpty(), "Expected validation error for email: " + user.getEmail());
        }
    }

    @Test
    void shouldValidateUser_WhenEmailIsValid() {
        for (User user : userWithValidEmails) {
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Expected no validation errors for email: " + user.getEmail());
        }
    }
}