package com.aiapp;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class RegistrationHandlerTest {
    @Test
    public void testHandlerExists() {
        RegistrationHandler handler = new RegistrationHandler(new UserRepository());
        assertNotNull(handler, "RegistrationHandler should be instantiable");
    }
}
