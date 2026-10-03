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
        server =new AppServer(8081);
        server.start();
    
    }
    @AfterEach
        void tearDown()
        {
            server.stop();
        }
        @Test
        void testHealthCheckReturns200() throws Exception{
            HttpClient client =HttpClient.newHttpClient();
            HttpRequest request =HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8081/api/health"))
            .GET()
            .build();
               HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());

        }
    
}

