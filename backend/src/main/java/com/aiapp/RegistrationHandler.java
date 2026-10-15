package com.aiapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RegistrationHandler implements HttpHandler {

    private final UserRepository userRepository;

    public RegistrationHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // We only allow POST requests for registration
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            // Read the request body
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Very basic form parsing (Format expected: name=John&phoneNumber=123&email=test@test.com&password=pass)
            // In a real app, you'd use a JSON library like Jackson or a robust form parser
            String name = extractParam(body, "name");
            String phone = extractParam(body, "phoneNumber");
            String email = extractParam(body, "email");
            String password = extractParam(body, "password");

            if (name == null || phone == null || email == null || password == null) {
                sendResponse(exchange, 400, "Missing required fields");
                return;
            }

            // Secure the password
            String hashedPassword = PasswordHasher.hash(password);

            // Save to database
            User newUser = new User(null, name, phone, email, hashedPassword);
            userRepository.save(newUser);

            sendResponse(exchange, 201, "User registered successfully");

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    // Helper to extract x-www-form-urlencoded parameters
    private String extractParam(String body, String paramName) {
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
