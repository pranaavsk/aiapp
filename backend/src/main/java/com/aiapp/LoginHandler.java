package com.aiapp;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class LoginHandler implements HttpHandler {

    private final UserRepository userRepository;

    public LoginHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 1. Only allow POST requests for login
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendEmptyResponse(exchange, 405);
            return;
        }

        try {
            // 2. Read request body
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // 3. Extract form parameters
            String email = extractParam(body, "email");
            String password = extractParam(body, "password");

            // 4. Validate presence
            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                sendEmptyResponse(exchange, 400);
                return;
            }

            // 5. Look up user by email
            Optional<User> optionalUser = userRepository.findByEmail(email.trim());
            if (optionalUser.isEmpty()) {
                sendEmptyResponse(exchange, 401);
                return;
            }

            // 6. Verify password hash using PasswordHasher
            User user = optionalUser.get();
            boolean passwordMatches = PasswordHasher.verify(password, user.getPasswordHash());
            if (!passwordMatches) {
                sendEmptyResponse(exchange, 401);
                return;
            }

            // 7. Successful login: 200 OK with empty body
            sendEmptyResponse(exchange, 200);

        } catch (Exception e) {
            e.printStackTrace();
            sendEmptyResponse(exchange, 500);
        }
    }

    // Helper to extract x-www-form-urlencoded parameters
    private String extractParam(String body, String paramName) {
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    // Send HTTP status with an empty response body
    private void sendEmptyResponse(HttpExchange exchange, int statusCode) throws IOException {
        // In HttpExchange, passing -1 indicates no response body
        exchange.sendResponseHeaders(statusCode, -1);
        exchange.close();
    }
}
