package com.aiapp;

import org.junit.jupiter.api.Test;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    @Test
    public void testPropertiesAreLoaded() {
        Properties props = DatabaseConnection.getProperties();
        
        assertNotNull(props, "Properties should not be null");
        assertNotNull(props.getProperty("db.url"), "db.url should be present");
        assertNotNull(props.getProperty("db.username"), "db.username should be present");
        assertEquals("com.mysql.cj.jdbc.Driver", props.getProperty("db.driver"));
    }
}

