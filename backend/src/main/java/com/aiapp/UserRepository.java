package com.aiapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class UserRepository {

    public void save(User user) {
        String sql = "INSERT INTO users (name, phone_number, email, password_hash) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhoneNumber());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Creating user failed, no rows affected.");
            }

            // Retrieve the auto-incremented ID and assign it back to the user object
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new RuntimeException("Creating user failed, no ID obtained.");
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Database error saving user", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, phone_number, email, password_hash FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("password_hash")
                    );
                    return Optional.of(user);
                }
                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Database error finding user by email", e);
        }
    }
}
