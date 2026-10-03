# Phase 1: Web Application Boilerplate Setup

> **Context Record** — Created: 2026-10-03
> **Purpose**: Durable handoff document for a future Antigravity/Gemini agent or human engineer continuing this project. Records the exact state at the end of Phase 1, including chronology, errors, fixes, decisions, and the resume point for Phase 2.

---

## Purpose

Establish a working, compilable, testable project skeleton for a full-stack web application built with:

- Java HTTP server (no frameworks)
- HTML5 / CSS3 / Vanilla JavaScript frontend (no frameworks)
- MySQL database (JDBC, no ORM) — connection deferred to next phase
- Gradle build system

The phase was intentionally limited to scaffolding and verification. No authentication, registration, or database connection was implemented.

---

## Starting State

The repository at `d:\aiapp` was **empty** — no files existed before this phase began.

### Environment Constraints Established

| Concern | Constraint |
|---|---|
| Backend language | Java (OpenJDK 25.0.3, Temurin) |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Build tool | Gradle 8.10 |
| Database | MySQL via JDBC (MySQL Connector/J 9.0.0) |
| HTTP server | `com.sun.net.httpserver.HttpServer` (JDK built-in) |
| Backend frameworks | **None** — no Spring, Spring Boot, or equivalent |
| Frontend frameworks | **None** — no React, Vue, Angular, or equivalent |
| ORM | **None** — raw JDBC only |
| Testing | JUnit 5 (`junit-bom:5.10.2`, `junit-jupiter`) |

---

## Final Outcome

At the end of Phase 1 the following is true (verified from repository):

- Gradle 8.10 build system is configured and working.
- `AppServer.java` compiles and starts a basic HTTP server on port 8080.
- `GET /api/health` returns HTTP 200 with body `OK`.
- `AppServerTest.java` tests that endpoint on port 8081 and **passes** (`gradle test` -> `BUILD SUCCESSFUL`).
- Frontend boilerplate (`index.html`, `style.css`, `app.js`) exists under `src/main/resources/public/`.
- `db.properties` placeholder exists with connection template values.
- `.gitignore` is present at the project root.
- No Git repository has been initialised (confirmed: `fatal: not a git repository`).

---

## Implementation Journey (Chronological)

### Step 1 — Gradle Unavailable

The first attempt to use Gradle failed because the `gradle` command was not found globally.

An SDKMAN-based installation was considered but could not proceed because Git Bash lacked the `zip` utility required by SDKMAN.

**Resolution**: Gradle 8.10 was downloaded and installed directly to `C:\gradle\gradle-8.10` on Windows. After reopening the terminal, `gradle -v` confirmed availability.

> **Note**: The Gradle wrapper (`gradlew` / `gradlew.bat`) and wrapper JAR files were **not** created in this phase. The `gradle/wrapper/` directory exists but is **empty** (verified). All `gradle` invocations used the globally installed Gradle 8.10 binary.

---

### Step 2 — Project Initialisation

`build.gradle` and `settings.gradle` were created manually (not via `gradle init`).

**Placement error**: `build.gradle` was temporarily placed inside the `.gradle/` cache directory instead of the project root. This was identified and corrected.

---

### Step 3 — AppServer.java

`AppServer.java` was created to implement a minimal HTTP server using the JDK built-in `com.sun.net.httpserver.HttpServer`.

**Source-placement error**: `AppServer.java` was temporarily placed under the test source directory (`src/test/...`) instead of `src/main/java/com/aiapp/`. This was identified and corrected.

**Capitalisation typo**: The constructor was initially typed as `Appserver` (lowercase `s`). The correct class name is `AppServer`.

---

### Step 4 — AppServerTest.java

`AppServerTest.java` was created to verify the health endpoint.

**Typos found and fixed during implementation**:

- Test class initially named `AppserverTest` (lowercase `s`) -> corrected to `AppServerTest`.
- Method `server.start()` was initially typed as `staart()` -> corrected.

The test binds to port **8081** (not 8080) to avoid conflicts with a potentially running server instance.

---

### Step 5 — Frontend Files

Three frontend files were created under `src/main/resources/public/`:

- `index.html`
- `style.css`
- `app.js`

**Errors found and fixed**:

1. `app.js` used `statuss` (double `s`) instead of the correct `status` when calling `document.getElementById('status')`.
2. `app.js` had an incorrect JavaScript variable usage pattern (not further detailed in history).
3. `index.html` was missing `<script src="app.js"></script>` entirely. This was identified and added before the close of `</body>`.

---

### Step 6 — db.properties

A database configuration placeholder was created at `src/main/resources/db.properties`.

**Filename typo**: The file was initially created as `db.propertites` (extra `t`). This was identified and the correct name `db.properties` was used in the final state.

---

### Step 7 — .gitignore

`.gitignore` was created.

**Placement error**: `.gitignore` was initially created inside the frontend public directory (`src/main/resources/public/.gitignore`) instead of the project root. This was identified and moved to `d:\aiapp\.gitignore`.

---

### Step 8 — Build Verification

`gradle test` was run and produced `BUILD SUCCESSFUL`. This is the terminal verification of the phase.

> **Important**: The `gradle test` success cannot be independently re-verified by inspecting the repository alone at this moment; it was reported as successful during the implementation session. The compiled `.class` files present under `bin/` are consistent with a successful prior compilation, but the test was not re-run during context recording.

---

## Repository Changes

All files below were **created** during this phase (the repository was empty before Phase 1).

| File | Role |
|---|---|
| `build.gradle` | Gradle build configuration — plugins, Java toolchain, dependencies, test runner |
| `settings.gradle` | Gradle settings — root project name `aiapp-backend` |
| `.gitignore` | Excludes build artifacts, credentials, IDE files |
| `gradle/wrapper/` | Directory exists, **empty** — no wrapper JAR or properties file present |
| `src/main/java/com/aiapp/AppServer.java` | Java HTTP server — `GET /api/health` endpoint, `main()` entry point |
| `src/test/java/com/aiapp/AppServerTest.java` | JUnit 5 integration test for health endpoint |
| `src/main/resources/public/index.html` | Frontend HTML boilerplate |
| `src/main/resources/public/style.css` | Frontend CSS boilerplate |
| `src/main/resources/public/app.js` | Frontend JavaScript boilerplate |
| `src/main/resources/db.properties` | Database connection placeholder (gitignored — must not be committed) |

No files were deleted. No files pre-existed.

---

## Important Code and Configuration

### build.gradle (verified)

```groovy
plugins{
    id 'java'
}
group ='com.aiapp'
version='1.0-SNAPSHOT'

java{
    toolchain{
        languageVersion=JavaLanguageVersion.of(25)
    }
}
repositories{
    mavenCentral()
}
dependencies
{
    implementation 'com.mysql:mysql-connector-j:9.0.0'
    testImplementation platform('org.junit:junit-bom:5.10.2')
    testImplementation 'org.junit.jupiter:junit-jupiter'
}
test
{
    useJUnitPlatform()
}
```

### settings.gradle (verified)

```groovy
rootProject.name='aiapp-backend'
```

### AppServer.java (verified)

```java
package com.aiapp;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class AppServer {
    private final HttpServer server;

    public AppServer(int port ) throws IOException{
        server =HttpServer.create(new InetSocketAddress(port),0);
        server.createContext("/api/health",exchange ->
{
    String response="OK";
    exchange.sendResponseHeaders(200,response.getBytes().length);
  try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        server.setExecutor(null);
    }
    public void start()
    {
        server.start();
    }
    public void stop()
    {
        server.stop(0);
    }
    public static void main(String[]args) throws IOException{
        AppServer appServer =new AppServer(8080);
        appServer.start();
        System.out.println("Server started on http://localhost:8080");
    }
}
```

**Key design points**:

- Uses `com.sun.net.httpserver.HttpServer` — a JDK internal API, not a third-party framework. Satisfies the no-framework constraint.
- Port is passed at construction time. `main()` uses port 8080. The test uses port 8081.
- `server.setExecutor(null)` -> JDK default single-threaded executor. Adequate for development; not production-safe under load.
- Response charset: `response.getBytes()` uses the JVM default charset. Should use `StandardCharsets.UTF_8` explicitly in future (deferred).

### AppServerTest.java (verified)

```java
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
```

**Test behaviour**: Starts a real `AppServer` on port 8081 before each test, sends a real HTTP GET to `/api/health`, asserts HTTP 200 and body `"OK"`, then stops the server.

### Frontend — index.html (verified)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AI App</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <header>
        <h1>
            AI App
        </h1>
    </header>
    <main>
        <p>Clean boilerplate - ready to build on.</p>
        <div id="status">Checking server status...</div>
    </main>
      <script src="app.js"></script>
</body>
</html>
```

The `<script src="app.js"></script>` tag is present just before `</body>`. This was **missing** in an earlier version and was explicitly added as a fix.

### Frontend — style.css (verified)

```css
body{
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
    line-height: 1.6;
    margin: 0;
    padding : 2rem;
    background-color:  #f4f4f9;
    color: #333;
}
header{
    border-bottom: 2px solid #ddd;
    margin-bottom: 1.5rem;
    padding-bottom: 0.5rem;
}
h1{
    margin: 0;
}
#status
{
    margin-top: 1rem;
    padding: 10px 15px;
    border-radius: 4px;
    background-color: #e2e8f0;
    display: inline-block;
    font-weight: bold;
}
```

### Frontend — app.js (verified)

```javascript
document.addEventListener('DOMContentLoaded',()=>{
    const status =document.getElementById('status');
     status.textContent= 'Frontend loaded successfully';
});
```

**Current behaviour**: On DOM load, selects the `#status` div and sets its text to `"Frontend loaded successfully"`. This is a DOM-presence check only; it does **not** make any HTTP call to the backend health endpoint. The variable is named `status` — the earlier typo `statuss` was corrected.

### db.properties (verified)

```properties
db.url=jdbc:mysql://localhost:3306/aiapp_db
db.username=root
db.password=your_password_here
db.driver=com.mysql.cj.jdbc.Driver
```

> **Security**: `db.properties` is listed in `.gitignore` and must never be committed. The `your_password_here` placeholder must be replaced with real credentials before any database work is attempted.

### .gitignore (verified)

```
# Gradle
.gradle/
build/
# Database credentials - never commit real passwords
src/main/resources/db.properties
# IDE files
.idea/
*.iml
.vscode/
*.class
```

---

## Commands and Operations

| Command | Observed Result |
|---|---|
| `gradle` (initial) | `command not found` — Gradle not installed |
| SDKMAN install attempt | Abandoned — Git Bash lacked `zip` utility |
| Gradle 8.10 installed to `C:\gradle\gradle-8.10` | Manual Windows install |
| Terminal reopened | `gradle -v` confirmed Gradle 8.10 available |
| `gradle test` | Reported `BUILD SUCCESSFUL` during implementation session |
| `gradle -v` (context recording) | Confirmed Gradle 8.10 — details below |
| `java -version` (context recording) | OpenJDK 25.0.3, Temurin-25.0.3+9 |
| `git status` (context recording) | `fatal: not a git repository` |

### Gradle version detail (verified during context recording)

```
Gradle 8.10
Build time:   2024-08-14 11:07:45 UTC
Revision:     fef2edbed8af1022cefaf44d4c0514c5f89d7b78
Kotlin:       1.9.24
Groovy:       3.0.22
Launcher JVM: 25.0.3 (Eclipse Adoptium 25.0.3+9-LTS)
Daemon JVM:   C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot
OS:           Windows 11 10.0 amd64
```

**Note**: Gradle 8.10 with JDK 25 produces a startup warning:
`WARNING: A restricted method in java.lang.System has been called`
This is caused by `native-platform-0.22-milestone-26.jar`. It does not prevent compilation or testing. It can be suppressed by adding `--enable-native-access=ALL-UNNAMED` to `org.gradle.jvmargs` in a `gradle.properties` file.

---

## Verification

| Verification | Status |
|---|---|
| `gradle test` -> `BUILD SUCCESSFUL` | Reported successful during implementation; **not re-run** during context recording |
| `.class` files in `bin/` consistent with prior build | Verified by inspection — `AppServer.class` and `AppServerTest.class` present |
| All source files readable and well-formed | Verified by inspection during context recording |
| `gradle -v` -> Gradle 8.10 | Verified during context recording |
| `java -version` -> OpenJDK 25.0.3 Temurin | Verified during context recording |
| Git repository exists | **Not verified** — `git status` returned `fatal: not a git repository` |

---

## Failures and Fixes

### 1. Gradle Not Found
- **Error**: `gradle: command not found`
- **Attempted fix**: SDKMAN — abandoned (Git Bash lacked `zip`)
- **Resolution**: Manual Windows install of Gradle 8.10 to `C:\gradle\gradle-8.10`; terminal reopened

### 2. `build.gradle` in Wrong Directory
- **Error**: `build.gradle` placed inside `.gradle/` cache directory
- **Fix**: Moved to project root `d:\aiapp\build.gradle`

### 3. `AppServer.java` in Wrong Source Tree
- **Error**: `AppServer.java` placed under `src/test/...`
- **Fix**: Moved to `src/main/java/com/aiapp/AppServer.java`

### 4. Class Name Capitalisation Typo
- **Error**: Constructor referenced as `Appserver` (lowercase `s`)
- **Fix**: Corrected to `AppServer`

### 5. Test Class Capitalisation Typo
- **Error**: Test class named `AppserverTest`
- **Fix**: Corrected to `AppServerTest`

### 6. Typo in Test Method Call
- **Error**: `server.staart()` instead of `server.start()`
- **Fix**: Corrected to `server.start()`

### 7. `app.js` Variable Name Typo
- **Error**: `document.getElementById('statuss')` (double `s`)
- **Fix**: Corrected to `document.getElementById('status')`

### 8. `app.js` Incorrect Variable Usage
- **Error**: Incorrect JavaScript variable usage pattern (exact form not preserved in history)
- **Fix**: Corrected; final form uses `const status = document.getElementById('status'); status.textContent = ...`

### 9. `index.html` Missing Script Tag
- **Error**: `<script src="app.js"></script>` was absent; JavaScript would never load
- **Fix**: Tag added before `</body>`

### 10. `db.properties` Filename Typo
- **Error**: File created as `db.propertites` (extra `t`)
- **Fix**: Renamed to `db.properties`

### 11. `.gitignore` in Wrong Directory
- **Error**: `.gitignore` created inside `src/main/resources/public/`
- **Fix**: Moved to project root `d:\aiapp\.gitignore`

---

## Decisions and Rationale

| Decision | Rationale |
|---|---|
| `com.sun.net.httpserver.HttpServer` | Satisfies no-framework constraint using only JDK built-ins |
| No Spring / Spring Boot | Explicit project constraint |
| No ORM | Explicit project constraint — raw JDBC only |
| No frontend framework | Explicit project constraint |
| MySQL Connector/J 9.0.0 | Latest stable at time of setup; uses `com.mysql.cj.jdbc.Driver` (modern driver class) |
| JUnit 5 via BOM | Clean version management for test dependencies |
| Java 25 toolchain in Gradle | Matches the installed JDK; uses Gradle toolchain API for portability |
| Test uses port 8081 | Avoids collision with a potentially running server on port 8080 |
| `db.properties` in `.gitignore` | Security — real credentials must never be committed |
| Global Gradle install (not wrapper) | Gradle wrapper was not generated; all `gradle` commands require the globally installed binary |

---

## Final Technical State

### Verified Repository Structure

```
d:\aiapp\
├── .gitignore                               (161 bytes)
├── build.gradle                             (431 bytes)
├── settings.gradle                          (32 bytes)
├── phase-01-boilerplate-setup.md            <- this file (added during context recording)
├── .gradle/                                 (Gradle daemon cache — gitignored)
├── bin/                                     (IDE compiled output — NOT standard Gradle layout)
│   ├── main/
│   │   ├── com/aiapp/AppServer.class
│   │   ├── db.properties
│   │   └── public/
│   │       ├── app.js
│   │       ├── index.html
│   │       └── style.css
│   └── test/
│       └── com/aiapp/AppServerTest.class
├── build/                                   (Gradle build output — gitignored)
├── gradle/
│   └── wrapper/                             (EMPTY — no gradlew scripts or wrapper JAR)
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/aiapp/
    │   │       └── AppServer.java           (1028 bytes)
    │   └── resources/
    │       ├── db.properties                (129 bytes — gitignored)
    │       └── public/
    │           ├── index.html               (507 bytes)
    │           ├── style.css                (505 bytes)
    │           └── app.js                   (167 bytes)
    └── test/
        └── java/
            └── com/aiapp/
                └── AppServerTest.java       (1163 bytes)
```

**Structural discrepancy with expected structure**: The expected structure in the implementation prompt listed only the `gradle/wrapper/` directory. The actual repository also contains a `bin/` directory with compiled class files. This is consistent with IDE compilation (VS Code or Eclipse Java extension) rather than Gradle compilation (which writes to `build/`). The `bin/` directory is **not** excluded by `.gitignore`.

---

## Deferred / Out of Scope

1. **MySQL database connection** — JDBC `Connection`, `PreparedStatement`, connection pooling
2. **User registration** — name, phone number, email, password persistence to MySQL
3. **Authentication** — login, session management, password hashing
4. **Frontend forms** — registration and login UI
5. **Static file serving from Java** — no handler to serve files from `src/main/resources/public/`
6. **Gradle wrapper setup** — `gradlew`, `gradlew.bat`, `gradle-wrapper.jar` not generated
7. **Git repository initialisation** — `git init` was never run; no commits exist
8. **Charset safety** — `response.getBytes()` should specify `StandardCharsets.UTF_8`
9. **Executor configuration** — single-threaded executor not safe for concurrent use
10. **`bin/` added to `.gitignore`** — IDE output directory not yet excluded

---

## Known Risks and Open Questions

1. **`bin/` directory not gitignored**: Will be staged at first `git add .`. Add `bin/` to `.gitignore` before initialising Git.

2. **No Gradle wrapper**: Reproducing the build on another machine requires Gradle 8.10 globally installed. Consider running `gradle wrapper` before Phase 2.

3. **`com.sun.net.httpserver` is a JDK-internal API**: Available in Java 25 but historically non-public. Adequate for this project scope; evaluate for production use.

4. **Static file serving not implemented**: A browser navigating to `http://localhost:8080/` will receive a 404. The frontend cannot be used in-browser until static file serving is added to `AppServer.java`.

5. **Gradle + Java 25 native-access warning**: Does not break builds currently but may become a blocker with future Java versions. Mitigatable via `gradle.properties`.

6. **`db.properties` placeholder password**: Must be replaced with real credentials before Phase 2 database work begins.

---

## Commit Linkage

**No Git repository exists.**

`git status` returned `fatal: not a git repository (or any of the parent directories): .git`.

No commits, no branches, no tags. This context document is the only persistent record of Phase 1 implementation history.

> **Recommendation**: Run `git init`, update `.gitignore` to include `bin/`, and make an initial commit before beginning Phase 2.

---

## Resume From Here

### What is working now
- `gradle test` passes (`BUILD SUCCESSFUL` — verified during implementation session).
- `GET http://localhost:8081/api/health` returns 200 OK (verified by the passing test).
- All source files are in correct locations and well-formed.
- Build system is operational.

### What is NOT built yet
- The server cannot serve the frontend (no static file handler).
- There is no database connection of any kind.
- There is no registration or authentication logic.

### Phase 2 goal
Implement the **user registration data foundation**:
- Collect: `name`, `phone number`, `email`, `password`
- Persist to MySQL database `aiapp_db` (schema must be created)
- Raw JDBC only — no ORM, no Spring
- Password must be hashed (never store plaintext)

### Immediate pre-work before Phase 2
1. Append `bin/` to `.gitignore` (before any `git init`)
2. Run `gradle wrapper` to create wrapper scripts (recommended)
3. Run `git init` at `d:\aiapp`
4. Stage and commit all Phase 1 files: `git commit -m "Phase 1: boilerplate setup"`
5. Update `src/main/resources/db.properties` with real MySQL credentials
6. Create the `aiapp_db` MySQL database and verify JDBC connectivity

### Key paths
| Path | Purpose |
|---|---|
| `d:\aiapp\src\main\java\com\aiapp\AppServer.java` | HTTP server — add new route handlers here |
| `d:\aiapp\src\test\java\com\aiapp\AppServerTest.java` | Test file — add new tests here |
| `d:\aiapp\src\main\resources\public\` | Frontend files — extend for registration UI |
| `d:\aiapp\src\main\resources\db.properties` | Database credentials — never commit |
| `d:\aiapp\build.gradle` | Add dependencies here (e.g., bcrypt library for password hashing) |

### Java package
All Java source is in package `com.aiapp`.

### MySQL JDBC driver class
`com.mysql.cj.jdbc.Driver` — do NOT use the deprecated `com.mysql.jdbc.Driver`.

### JDBC URL template
`jdbc:mysql://localhost:3306/aiapp_db`

---

*End of Phase 1 context record.*
