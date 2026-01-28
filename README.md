# Authify

A lightweight and extensible Java authentication library for token-based auth, session handling, and common auth-related utilities. Authify provides a simple API to generate, sign, and verify tokens, manage user sessions, and integrate with common identity providers.

> NOTE: This README is intentionally generic so it fits the current repository structure. Adjust the examples, package names, and configuration to match your implementation.

## Features

- Generate and verify JWT tokens
- Configurable token signing (HMAC, RSA)//changes done
- Session management helpers
- Pluggable user store interface (in-memory, JDBC, etc.)
- Utilities for password hashing and validation
- Easy-to-use Java API with Maven and Gradle examples

## Requirements

- Java 11+ (or the Java version your project targets)
- Maven or Gradle for building
- (Optional) A keystore or environment variables for signing keys

## Quick Start

Add Authify to your project:

Maven (pom.xml)
```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>authify</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle (build.gradle)
```groovy
implementation 'com.example:authify:0.1.0'
```

Basic usage (example)
```java
// Example usage — replace with real classes from this repo
AuthifyConfig config = AuthifyConfig.builder()
    .secret(System.getenv("AUTHIFY_SECRET"))
    .tokenExpiryMinutes(60)
    .build();

Authify authify = new Authify(config);

// Generate a token for user
String token = authify.generateToken(new UserPrincipal("user-id-123"));

// Validate token and get principal
Optional<UserPrincipal> principal = authify.verifyToken(token);
principal.ifPresent(p -> System.out.println("Authenticated: " + p.getId()));
```

Adjust the above sample to use the concrete classes and package names in this repository.

## Configuration

Typical configuration options

- AUTHIFY_SECRET — HMAC secret key (or path to private key for RSA)
- AUTHIFY_TOKEN_EXPIRY — Token lifetime (minutes)
- AUTHIFY_ISSUER — Token issuer string
- AUTHIFY_AUDIENCE — Token audience

Example environment variables (Unix)
```bash
export AUTHIFY_SECRET="your-very-secure-secret"
export AUTHIFY_TOKEN_EXPIRY="60"
export AUTHIFY_ISSUER="your-app"
```

Or use a properties file / application config if your project uses Spring Boot or similar frameworks.

## Integration tips

- For production use, prefer RSA/ECDSA key pairs and secure key management (KMS, Vault).
- Rotate keys and support key identifiers (kid) in tokens for smooth rotation.
- Store refresh tokens securely, and implement revocation lists if needed.
- Use strong password hashing (e.g., bcrypt, Argon2) for stored credentials.

## Testing

Run unit tests with Maven:
```bash
mvn test
```

Or with Gradle:
```bash
gradle test
```

Include additional integration tests that exercise token signing and validation.

## Contributing

Contributions are welcome!

- Fork the repo and create a feature branch (`feature/awesome`)
- Write tests and ensure they pass locally
- Open a Pull Request with a clear description of changes
- Follow Java coding conventions and include Javadoc for public APIs

Please open issues for bugs or feature requests.

## Roadmap (suggested)

- Add built-in adapters for JDBC and Redis session stores
- Add OAuth2 client helpers for common providers
- Provide Spring Boot starter module
- Improve docs and add more examples

## License

This repository doesn't specify a license yet. Add a license file (e.g., `LICENSE` with the MIT license) to make terms explicit.

Example (MIT):
```
MIT License
...
```

## Contact / Support

- Open issues on this repository: https://github.com/Jagdish6666/Authify/issues
- Include reproduction steps and minimal code samples when reporting bugs.

---

If you'd like, I can:
- Commit this README.md directly to the `master` branch of your repository, or
- Create a branch and open a pull request with the new README.

Tell me which option you prefer and whether you want any changes to the content (project name, package names, examples, license).
