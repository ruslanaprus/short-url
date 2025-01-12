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

    private static List<User> userWithInvalidPasswords;
    private static List<User> userWithValidPasswords;


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

        userWithInvalidPasswords = List.of(
                User.builder().email("test@test.com").password("").build(),
                User.builder().email("test@test.com").password("short1S").build(),
                User.builder().email("test@test.com").password("alllowercase1").build(),
                User.builder().email("test@test.com").password("ALLUPPERCASE1").build(),
                User.builder().email("test@test.com").password("12345678").build(),
                User.builder().email("test@test.com").password("Password").build(),
                User.builder().email("test@test.com").password("Пароль123").build(),
                User.builder().email("test@test.com").password("Password!@#").build()
        );

        userWithValidPasswords = List.of(
                User.builder().email("test@test.com").password("Valid123").build(),
                User.builder().email("test@test.com").password("Password123").build(),
                User.builder().email("test@test.com").password("MySecurePassword1").build(),
                User.builder().email("test@test.com").password("Abcdefg123").build(),
                User.builder().email("test@test.com").password("Z1a2b3c4D").build(),
                User.builder().email("test@test.com").password("Z1a2b3c4D#").build()
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

    @Test
    void shouldRejectUser_WhenPasswordIsInvalid() {
        for (User user : userWithInvalidPasswords) {
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertFalse(violations.isEmpty(), "Expected validation error for password: " + user.getPassword());
        }
    }

    @Test
    void shouldValidateUser_WhenPasswordIsValid() {
        for (User user : userWithValidPasswords) {
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertTrue(violations.isEmpty(), "Expected no validation errors for password: " + user.getPassword());
        }
    }


}