# Phase 04: Login API & Frontend Authentication Flow

## Purpose
Implement the complete login feature for the Java + MySQL web application, spanning the backend data layer, HTTP endpoint routing, password verification, and matching glassmorphic frontend UI. The implementation enforces safe credential handling, BCrypt password hash verification, generic failure responses, and an empty response body contract on authentication outcomes.

---

## Starting State
- **Phase Context Precedents**:
  - `context/phase-01-boilerplate-setup.md`: Initial project setup.
  - `context/phase-02-registration-data-layer.md`: Implemented `User`, `UserRepository.save()`, `PasswordHasher`, and database connection.
  - `context/phase-03-registration-api.md`: Implemented `RegistrationHandler`, `/api/register` route on `AppServer`, and initial registration frontend.
- **Repository Architecture**:
  - Backend in `d:\aiapp\backend`, frontend in `d:\aiapp\frontend`, database schema in `d:\aiapp\database\schema.sql`.
  - Framework-free native Java HTTP server (`com.sun.net.httpserver.HttpServer`).
  - MySQL database (`aiapp_db.users`) with unique `email` column as the identity convention.
  - BCrypt (`at.favre.lib:bcrypt:0.10.2`) cost factor 12 via `PasswordHasher`.
  - Gradle 8.13 wrapper targeting Java 21 toolchain.
- **Missing Functionality Before Phase 4**:
  - `UserRepository` only had `save(User)`; no query method existed to look up a user by identifier.
  - No login handler or route existed on `AppServer`.
  - No login HTML page or login JavaScript logic existed.
  - Registration UI (`index.html`) lacked links to any login interface.

---

## Final Outcome
The complete login pipeline is fully implemented, tested, and running:
1. **Data Layer**: `UserRepository.findByEmail(String email)` safely queries MySQL for user records using a parameterized `PreparedStatement` and returns `Optional<User>`.
2. **API Endpoint**: `POST /api/login` handles `application/x-www-form-urlencoded` credentials (`email` and `password`).
   - `200 OK` (empty response body) on valid credentials.
   - `400 Bad Request` (empty body) on missing or blank parameters.
   - `401 Unauthorized` (empty body) for nonexistent emails or mismatched passwords (unified rejection to prevent user enumeration).
   - `405 Method Not Allowed` (empty body) for non-POST HTTP methods.
   - `500 Internal Server Error` (empty body) on unexpected exceptions.
3. **AppServer**: `/api/login` route is registered and mounted. Static frontend file routing in `AppServer` serves `../frontend/login.html` and `../frontend/login.js`.
4. **Frontend UI & Logic**:
   - `login.html`: Matching glassmorphism design with animated ambient spheres, Inter font, email and password inputs, submit button, status message container, and bidirectional navigation links to `index.html`.
   - `login.js`: Asynchronous form submission using `fetch()` and `URLSearchParams`, client-side loading indicators, and status-driven UI messages without expecting response body content.
   - `style.css`: Added reusable `.auth-footer` navigation styles.

---

## Implementation Journey

The phase proceeded through a strict TDD and guided phase-by-phase workflow:

### Step 1: UserRepository `findByEmail` (TDD)
- **Goal**: Add database query to look up existing user records by email safely.
- **Red Step**: Designed and added `testFindByEmailReturnsEmptyWhenUserNotFound` to `UserRepositoryTest.java`. Running `./gradlew test --tests com.aiapp.UserRepositoryTest` produced a compilation error (`cannot find symbol: method findByEmail(String)`), confirming Red state.
- **Human Error & Recovery**: The production implementation was mistakenly pasted directly into `UserRepositoryTest.java`, wiping out the test class and causing Gradle to fail with `No tests found for given includes: [com.aiapp.UserRepositoryTest]`. The test file was restored, and production code was placed in `backend/src/main/java/com/aiapp/UserRepository.java`.
- **Green Step**: Implemented `findByEmail(String email)` using `PreparedStatement` with `SELECT id, name, phone_number, email, password_hash FROM users WHERE email = ?`. Re-ran `./gradlew test --tests com.aiapp.UserRepositoryTest`, achieving `BUILD SUCCESSFUL`.

### Step 2: LoginHandler & Contract Tests (TDD)
- **Goal**: Implement HTTP handling, input validation, and BCrypt credential verification.
- **Red Step**: Created `LoginHandlerTest.java` utilizing lightweight `HttpExchange` test doubles and a `FakeUserRepository` stub to test:
  - Non-POST request rejection (`405`).
  - Missing field handling (`400`).
  - Nonexistent user rejection (`401`).
  - Password mismatch rejection (`401`).
  - Successful credential verification (`200 OK`).
  - Verification that all responses return empty response bodies.
  - Running Gradle failed with `cannot find symbol: class LoginHandler` across 11 error locations.
- **Green Step**: Created `backend/src/main/java/com/aiapp/LoginHandler.java`. Implemented `handle(HttpExchange)` with `sendEmptyResponse(exchange, statusCode)` using `exchange.sendResponseHeaders(statusCode, -1)`. All tests in `LoginHandlerTest` passed with `exit code 0`.

### Step 3: AppServer Route Integration (TDD)
- **Goal**: Expose `/api/login` on `AppServer`.
- **Red Step**: Added `testLoginEndpointIsRegistered()` in `backend/src/test/java/com/aiapp/AppServerTest.java`. Running the test failed with `AssertionFailedError` because requests to `/api/login` fell through to the static file handler and returned `404 Not Found`.
- **Green Step**: Added `server.createContext("/api/login", new LoginHandler(new UserRepository()));` to `AppServer.java`. Running `./gradlew test --tests com.aiapp.AppServerTest` passed with `exit code 0`.

### Step 4: Full Backend Suite Verification
- Ran `./gradlew test` across all backend test suites (`UserRepositoryTest`, `LoginHandlerTest`, `AppServerTest`, `PasswordHasherTest`, `RegistrationHandlerTest`). All passed with `BUILD SUCCESSFUL` (0 failures).

### Step 5: Frontend UI Inspection & Design System Alignment
- Inspected `frontend/index.html`, `frontend/style.css`, and `frontend/app.js` to extract color palettes, CSS keyframe animations, glassmorphism backdrop filters, form element IDs, and class conventions (`.hidden`, `.success`, `.error`).
- Noticed architectural difference: `RegistrationHandler` returned text messages in response bodies, whereas `LoginHandler` emits strictly empty response bodies. Adjusted frontend client architecture to infer user messages entirely from HTTP status codes.

### Step 6: Frontend Implementation
- **Styles**: Appended `.auth-footer` rules to `frontend/style.css`.
- **Registration Form**: Added sign-in navigation link `<p class="auth-footer">Already have an account? <a href="login.html">Sign In</a></p>` to `frontend/index.html`. Removed duplicate `#status-message` element introduced during edit.
- **Login Markup**: Created `frontend/login.html` mirroring registration card hierarchy, with `login-form`, `email`, `password`, `submit-btn`, and back-link to `index.html`.
- **Login Client Logic**: Created `frontend/login.js` using `URLSearchParams`, setting `Content-Type: application/x-www-form-urlencoded`, mapping HTTP statuses:
  - 200: "Login successful!" (`.success`) and `form.reset()`
  - 400: "Please fill in all required fields." (`.error`)
  - 401: "Invalid email or password." (`.error`)
  - Other: "Login failed. Please try again." (`.error`)
  - Loading state toggles button disabled state and restores text in a `finally` block.

---

## Repository Changes

### Files Created
- `backend/src/main/java/com/aiapp/LoginHandler.java`: HTTP handler for `/api/login`.
- `backend/src/test/java/com/aiapp/LoginHandlerTest.java`: Unit tests for login HTTP statuses and empty body enforcement.
- `frontend/login.html`: Login page UI.
- `frontend/login.js`: Frontend submission and status handling script.
- `context/phase-04-login-authentication.md`: This durable context record.

### Files Modified
- `backend/src/main/java/com/aiapp/UserRepository.java`: Added `findByEmail(String email)`.
- `backend/src/test/java/com/aiapp/UserRepositoryTest.java`: Added test for nonexistent email lookup.
- `backend/src/main/java/com/aiapp/AppServer.java`: Registered `/api/login` route.
- `backend/src/test/java/com/aiapp/AppServerTest.java`: Added integration test for `/api/login`.
- `frontend/style.css`: Added `.auth-footer` link styles.
- `frontend/index.html`: Added footer link to `login.html`.

### Files Intentionally Untouched
- `backend/src/main/java/com/aiapp/RegistrationHandler.java`: Unchanged to maintain registration stability.
- `backend/src/main/java/com/aiapp/PasswordHasher.java`: Reused existing `verify` method without modification.
- `backend/src/main/java/com/aiapp/DatabaseConnection.java`: Reused existing connection pool / manager.
- `backend/src/main/java/com/aiapp/User.java`: POJO model already contained all necessary fields.
- `database/schema.sql`: Existing `users` table schema already supported email lookup and hash storage.
- `backend/build.gradle`: No additional libraries or frameworks were introduced.

---

## Important Code and Configuration

### Endpoints and HTTP Contracts

#### 1. Login Endpoint (`POST /api/login`)
- **Route**: Mounted in `AppServer.java` via `server.createContext("/api/login", new LoginHandler(new UserRepository()));`
- **Request Content-Type**: `application/x-www-form-urlencoded`
- **Form Parameters**: `email`, `password`
- **Response Contract**:
  - Code 200: Empty body (`exchange.sendResponseHeaders(200, -1)`)
  - Code 400: Empty body (`exchange.sendResponseHeaders(400, -1)`)
  - Code 401: Empty body (`exchange.sendResponseHeaders(401, -1)`)
  - Code 405: Empty body (`exchange.sendResponseHeaders(405, -1)`)
  - Code 500: Empty body (`exchange.sendResponseHeaders(500, -1)`)

#### 2. Static File Route (`GET /*`)
- Resolves files relative to server process working directory: `java.io.File file = new java.io.File("../frontend" + path);`
- Serves `.html`, `.css`, `.js` with proper `Content-Type` headers.
- When running from `d:\aiapp\backend`, `../frontend` resolves to `d:\aiapp\frontend`.

### Database Query
```sql
SELECT id, name, phone_number, email, password_hash FROM users WHERE email = ?
```
- Executed via `java.sql.PreparedStatement` to ensure SQL injection protection.

### Password Verification
- Verified via `PasswordHasher.verify(password, user.getPasswordHash())`.
- BCrypt implementation: `at.favre.lib.crypto.bcrypt.BCrypt.verifyer().verify(...)`.
- Passwords are never logged, compared in plaintext, or serialized to responses.

---

## Commands and Operations

| Command | Working Directory | Observed Result |
| :--- | :--- | :--- |
| `./gradlew test --tests com.aiapp.UserRepositoryTest` | `d:\aiapp\backend` | Red step failed (missing method); after file restoration, passed (`exit code 0`). |
| `./gradlew test --tests com.aiapp.LoginHandlerTest` | `d:\aiapp\backend` | Red step failed (missing class); after handler creation, passed (`exit code 0`). |
| `./gradlew test --tests com.aiapp.AppServerTest` | `d:\aiapp\backend` | Red step failed with 404; after routing added, passed (`exit code 0`). |
| `./gradlew test` | `d:\aiapp\backend` | Full test suite passed (`BUILD SUCCESSFUL`). |
| `./gradlew run` | `d:\aiapp\backend` | Server started on `http://localhost:8080` (currently running as active daemon). |

---

## Verification

### Automated Unit & Integration Tests (Verified)
- **`UserRepositoryTest`**: Verifies instantiation and `Optional.empty()` return on unknown emails.
- **`LoginHandlerTest`**: Verifies:
  - Non-POST request returns `405` + empty body.
  - Missing parameters return `400` + empty body.
  - Nonexistent user returns `401` + empty body.
  - Incorrect password returns `401` + empty body.
  - Valid user and password return `200` + empty body.
- **`AppServerTest`**: Verifies `POST /api/login` is registered on the HTTP server and rejects dummy credentials with `401` + empty body.
- **`PasswordHasherTest`**: Verifies BCrypt hash format and valid/invalid password verification.
- **`RegistrationHandlerTest`**: Verifies instantiation.

### Manual End-to-End Verification (Verified)
- Server process running at `http://localhost:8080`.
- Navigated between `http://localhost:8080/index.html` and `http://localhost:8080/login.html`.
- Tested client-side submission and error displays.

---

## Failures and Fixes

### 1. File Overwrite During Phase 1 Guided Implementation
- **Symptom**: Running `./gradlew test --tests com.aiapp.UserRepositoryTest` failed with `Execution failed for task ':test'. No tests found for given includes: [com.aiapp.UserRepositoryTest]`.
- **Root Cause**: The production code for `UserRepository.java` was accidentally pasted into `UserRepositoryTest.java`, wiping out the test methods.
- **Fix**: Restored the JUnit test suite to `src/test/java/com/aiapp/UserRepositoryTest.java` and applied the `findByEmail` method to `src/main/java/com/aiapp/UserRepository.java`.

### 2. Duplicate DOM Element in `index.html`
- **Symptom**: During navigation link addition, two consecutive `<div id="status-message" class="hidden"></div>` tags were present.
- **Fix**: Removed the duplicate element line from `frontend/index.html`.

### 3. Historical Database Unique Key Constraint Conflict
- **Symptom**: During a prior registration test run, `java.sql.SQLIntegrityConstraintViolationException: Duplicate entry '...' for key 'users.email'` was emitted to the terminal log.
- **Explanation**: Expected MySQL behavior enforcing the `UNIQUE` constraint on the `email` column in `users`. Testing registration requires distinct email addresses.

---

## Decisions and Rationale

1. **Empty Body on API Responses**:
   - The task specification strictly mandated empty response bodies for both successful (`200 OK`) and rejected (`400`, `401`, `405`) authentication requests.
   - The handler uses `exchange.sendResponseHeaders(statusCode, -1)` which instructs `com.sun.net.httpserver.HttpExchange` to omit response body streams.
2. **Generic Credential Failure (`401`)**:
   - Both non-existent emails and invalid passwords return `401 Unauthorized` with identical empty payloads. This mitigates user enumeration and timing leak attacks.
3. **No Frameworks**:
   - Reused vanilla Java HTTP handlers, `URLSearchParams`, and `fetch()` without bringing in Spring, React, Jackson, or external routing engines.
4. **File Path Resolution in `AppServer`**:
   - Kept the hard drive relative path `../frontend` rather than embedding frontend files as jar classpath resources, allowing immediate live frontend updates without recompiling.

---

## Technical Distinctions & Caveats

- **No Active Session Created**: Returning HTTP `200 OK` indicates credential validity only. It **does not** set a cookie, create an in-memory session, or issue a JWT. The application remains completely stateless at this stage.
- **No Protected Routes**: The frontend success message (`"Login successful!"`) is purely visual; no redirection to a dashboard or protected member area occurs because authorization middleware and session management have not been built.
- **Empty Response Contract**: Unlike `RegistrationHandler` (which sends error text in the body), `LoginHandler` emits empty bodies. Frontend code must never call `response.text()` or `response.json()` on `/api/login`.

---

## Deferred / Out of Scope

- Session cookies (`Set-Cookie`, `JSESSIONID`), token issuance (JWT, Bearer tokens).
- Session storage in database or memory.
- Protected route redirection (e.g., `/dashboard.html`).
- Authorization middleware checking session tokens.
- User logout endpoint (`POST /api/logout`).
- Password reset, account lockout, rate limiting, or CAPTCHA.

---

## Commit Linkage & Repository Status

- **Current Git Branch**: `master`
- **Latest Commit**: `8cbe804` (`added context`)
- **Working Tree State**:
  - The repository has uncommitted changes and untracked files reflecting the reorganization of the project into `backend/`, `frontend/`, and `database/` subdirectories:
    - Deleted files in root `src/` and `bin/` (legacy paths).
    - Untracked directories: `backend/`, `frontend/`, `database/`.
    - Untracked context records: `context/phase-02-registration-data-layer.md`, `context/phase-03-registration-api.md`, `context/phase-04-login-authentication.md`.
  - All changes made during this phase reside cleanly within `backend/`, `frontend/`, and `context/`.

---

## Resume From Here

A future engineer or AI agent resuming work from this point can rely on the following:
1. Both user registration (`POST /api/register`) and credential authentication (`POST /api/login`) are fully implemented and passing tests.
2. The user identity identifier is **`email`**.
3. Passwords are encrypted with BCrypt cost 12.
4. The immediate next logical phase is **Phase 5: Session Management & Authentication State**:
   - Introduce session tokens or secure HTTP-only cookies upon successful `200 OK` login.
   - Store active session IDs in MySQL or memory.
   - Build a protected endpoint or dashboard page to verify authentication state.
   - Implement a logout endpoint (`POST /api/logout`) that invalidates the session.
