package com.aiapp;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LoginHandlerTest {

    private UserRepository mockRepo;
    private LoginHandler handler;

    // Lightweight test stub for UserRepository
    static class FakeUserRepository extends UserRepository {
        private final User userToReturn;

        public FakeUserRepository(User userToReturn) {
            this.userToReturn = userToReturn;
        }

        @Override
        public Optional<User> findByEmail(String email) {
            if (userToReturn != null && userToReturn.getEmail().equalsIgnoreCase(email)) {
                return Optional.of(userToReturn);
            }
            return Optional.empty();
        }
    }

    // Lightweight mock HttpExchange for pure unit testing without spinning a socket
    static class TestHttpExchange extends HttpExchange {
        private final String method;
        private final InputStream requestBody;
        private final ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        private final Headers requestHeaders = new Headers();
        private final Headers responseHeaders = new Headers();
        private int responseCode = -1;
        private long responseLength = -1;

        public TestHttpExchange(String method, String body) {
            this.method = method;
            this.requestBody = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        }

        @Override public Headers getRequestHeaders() { return requestHeaders; }
        @Override public Headers getResponseHeaders() { return responseHeaders; }
        @Override public URI getRequestURI() { return URI.create("/api/login"); }
        @Override public String getRequestMethod() { return method; }
        @Override public HttpContext getHttpContext() { return null; }
        @Override public void close() {}
        @Override public InputStream getRequestBody() { return requestBody; }
        @Override public OutputStream getResponseBody() { return responseBody; }
        @Override
        public void sendResponseHeaders(int rCode, long rLength) throws IOException {
            this.responseCode = rCode;
            this.responseLength = rLength;
        }
        @Override public InetSocketAddress getRemoteAddress() { return null; }
        @Override public InetSocketAddress getLocalAddress() { return null; }
        @Override public String getProtocol() { return "HTTP/1.1"; }
        @Override public Object getAttribute(String name) { return null; }
        @Override public void setAttribute(String name, Object value) {}
        @Override public void setStreams(InputStream i, OutputStream o) {}
        @Override public HttpPrincipal getPrincipal() { return null; }

        public int getResponseCode() { return responseCode; }
        public String getResponseBodyText() { return responseBody.toString(StandardCharsets.UTF_8); }
    }

    @Test
    public void testNonPostReturns405WithEmptyBody() throws IOException {
        LoginHandler handler = new LoginHandler(new FakeUserRepository(null));
        TestHttpExchange exchange = new TestHttpExchange("GET", "");

        handler.handle(exchange);

        assertEquals(405, exchange.getResponseCode(), "Non-POST request must return 405 Method Not Allowed");
        assertEquals("", exchange.getResponseBodyText(), "Body must be empty");
    }

    @Test
    public void testMissingParametersReturns400WithEmptyBody() throws IOException {
        LoginHandler handler = new LoginHandler(new FakeUserRepository(null));
        TestHttpExchange exchange = new TestHttpExchange("POST", "email=test@example.com");

        handler.handle(exchange);

        assertEquals(400, exchange.getResponseCode(), "Missing password must return 400 Bad Request");
        assertEquals("", exchange.getResponseBodyText(), "Body must be empty");
    }

    @Test
    public void testUserNotFoundReturns401WithEmptyBody() throws IOException {
        LoginHandler handler = new LoginHandler(new FakeUserRepository(null));
        TestHttpExchange exchange = new TestHttpExchange("POST", "email=unknown@example.com&password=secretPassword");

        handler.handle(exchange);

        assertEquals(401, exchange.getResponseCode(), "Nonexistent user must return 401 Unauthorized");
        assertEquals("", exchange.getResponseBodyText(), "Body must be empty");
    }

    @Test
    public void testWrongPasswordReturns401WithEmptyBody() throws IOException {
        String hashedPassword = PasswordHasher.hash("correctPassword");
        User user = new User(1L, "Alice", "1234567890", "alice@example.com", hashedPassword);
        LoginHandler handler = new LoginHandler(new FakeUserRepository(user));

        TestHttpExchange exchange = new TestHttpExchange("POST", "email=alice@example.com&password=wrongPassword");

        handler.handle(exchange);

        assertEquals(401, exchange.getResponseCode(), "Incorrect password must return 401 Unauthorized");
        assertEquals("", exchange.getResponseBodyText(), "Body must be empty");
    }

    @Test
    public void testSuccessfulLoginReturns200WithEmptyBody() throws IOException {
        String hashedPassword = PasswordHasher.hash("correctPassword");
        User user = new User(1L, "Alice", "1234567890", "alice@example.com", hashedPassword);
        LoginHandler handler = new LoginHandler(new FakeUserRepository(user));

        TestHttpExchange exchange = new TestHttpExchange("POST", "email=alice@example.com&password=correctPassword");

        handler.handle(exchange);

        assertEquals(200, exchange.getResponseCode(), "Valid credentials must return 200 OK");
        assertEquals("", exchange.getResponseBodyText(), "Body must be empty");
    }
}
