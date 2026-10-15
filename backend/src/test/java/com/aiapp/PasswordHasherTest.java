package com.aiapp;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    private final PasswordHasher hasher = new PasswordHasher();

    @Test
    void testPasswordIsHashedAndNotPlaintext() {
        String plainPassword = "SuperSecretPassword123!";
        String hash = hasher.hash(plainPassword);
        assertNotNull(hash);
        assertNotEquals(plainPassword, hash, "Hash must not equal the plaintext password");
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"),
                "Should be a valid bcrypt hash format");
    }

    @Test
    void testPasswordVerificationSuccess() {
        String plainPassword = "SuperSecretPassword123!";
        String hash = hasher.hash(plainPassword);
        assertTrue(hasher.verify(plainPassword, hash), "Correct password should verify successfully");
    }

    @Test
    void testPasswordVerificationFailure() {
        String plainPassword = "SuperSecretPassword123!";
        String wrongPassword = "WrongPassword!";
        String hash = hasher.hash(plainPassword);
        assertFalse(hasher.verify(wrongPassword, hash), "Incorrect password should fail verification");
    }
}

