# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/com/example/Music_streaming`: feature folders (`user`, `playlist`, `track`, `playback`, `spotify`, `youtube`, `comment`, `like`, `config`) with controller/service/repository/DTO separation.
- `src/main/resources/application.properties` holds DB/JWT/Spotify/YouTube config; static assets live in `resources/static`.
- Tests reside in `src/test/java/com/example/Music_streaming`, mirroring feature packages.
- Gradle wrapper/scripts (`build.gradle`, `gradlew*`) and project docs (`docs/`) sit at the repo root.

## Build, Test, and Development Commands
- `./gradlew clean build` — compile all modules and run unit/integration tests.
- `./gradlew test` — execute the JUnit suite; add `--info` for verbose logs or `--tests <Pattern>` to target classes.
- `./gradlew bootRun` — start the Spring Boot app locally (Java 21 toolchain).
- `./gradlew bootJar` — produce the runnable JAR in `build/libs/`.

## Coding Style & Naming Conventions
- Java 21 + Spring Boot 3.5.x, 4-space indentation, Unix line endings.
- Package by feature; keep controllers thin, move logic to services, and use repositories for persistence.
- Use PascalCase for classes; DTOs end with `Request`/`Response`; tests end with `Tests`.
- Leverage Lombok (`@Getter`, `@Builder`) for boilerplate; prefer constructor injection and guard external API calls with null/error handling.

## Testing Guidelines
- Framework: JUnit 5 via `spring-boot-starter-test`.
- Place unit tests alongside features under `src/test/java/com/example/Music_streaming/<feature>`.
- Name test methods `should...`; cover service rules, security flows, and integrations with mocked Spotify/YouTube/DB clients; reserve `@SpringBootTest` for cross-layer scenarios.
- Keep tests deterministic—use in-memory data/mocks instead of live external services.

## Commit & Pull Request Guidelines
- Follow the short, imperative style in history (e.g., `feat: ...`, `fix: ...`, `chore: ...`, `docs: ...`).
- Commits should bundle code + tests + docs for a single concern.
- PRs must include a summary, test commands/output, API contract changes, screenshots for UI/static updates, and any new env/config requirements.
- Link related issues and call out database or migration impacts.

## Security & Configuration Tips
- Do not commit real secrets—move values from `src/main/resources/application.properties` into environment vars or profile-specific `application-*.properties`.
- Set `SPRING_PROFILES_ACTIVE=local` for local runs; manage MySQL/JWT/Spotify/YouTube keys via `.env` or CI secrets.
- Lock down DB access to trusted hosts and configure external API redirect/whitelist settings per environment.
