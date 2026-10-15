package com.aiapp;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties properties = new Properties();
    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find db.properties");
            }
            properties.load(input);      
            Class.forName(properties.getProperty("db.driver"));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to initialize database configuration", ex);
        }
    }
    public static Properties getProperties() {
        return properties;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
        );
    }
}
