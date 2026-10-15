package com.aiapp;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class AppServer {
    private final HttpServer server;

    public AppServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // 1. Existing health endpoint
        server.createContext("/api/health", exchange -> {
            String response = "OK";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });

        // 2. Registration endpoint
        server.createContext("/api/register", new RegistrationHandler(new UserRepository()));

        // 3. Login endpoint
        server.createContext("/api/login", new LoginHandler(new UserRepository()));

        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }
            
            // Read from the frontend folder on the hard drive instead of the classpath!
            java.io.File file = new java.io.File("../frontend" + path);
            
            if (!file.exists() || file.isDirectory()) {
                String response = "404 Not Found";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }
            
            try (java.io.FileInputStream is = new java.io.FileInputStream(file)) {
                byte[] bytes = is.readAllBytes();
                String contentType = "text/plain";
                if (path.endsWith(".html")) contentType = "text/html";
                else if (path.endsWith(".css")) contentType = "text/css";
                else if (path.endsWith(".js")) contentType = "application/javascript";
                
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
                exchange.close();
            }
        });

        server.setExecutor(null);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {
        AppServer appServer = new AppServer(8080);
        appServer.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
