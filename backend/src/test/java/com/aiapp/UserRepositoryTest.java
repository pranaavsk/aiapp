package com.aiapp;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {

    @Test
    public void testRepositoryCanBeInstantiated() {
        UserRepository repo = new UserRepository();
        assertNotNull(repo, "UserRepository should instantiate correctly");
    }

    @Test
    public void testFindByEmailReturnsEmptyWhenUserNotFound() {
        UserRepository repo = new UserRepository();
        // Querying an email that does not exist should return empty without throwing
        Optional<User> user = repo.findByEmail("nonexistent_user_for_test@example.com");
        assertTrue(user.isEmpty(), "Expected no user to be found for dummy email");
    }
}
