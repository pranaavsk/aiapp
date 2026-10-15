package com.aiapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;  

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppServerTest
{
    private AppServer server;

    @BeforeEach
    void setup() throws Exception
    {
        server = new AppServer(8081);
        server.start();
    }

    @AfterEach
    void tearDown()
    {
        server.stop();
    }

    @Test
    void testHealthCheckReturns200() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/api/health"))
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());
    }

    @Test
    void testLoginEndpointIsRegistered() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/api/login"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString("email=nonexistent@example.com&password=password123"))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Since it is not registered yet, it will fall through to the static file handler (404)
        // Once registered, it should return 401 with an empty body
        assertEquals(401, response.statusCode());
        assertEquals("", response.body());
    }
}
