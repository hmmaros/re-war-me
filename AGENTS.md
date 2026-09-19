# AGENTS.md

Guidelines for AI agents and collaborators working on **ReWarMe**.

## Project at a glance

- Pure **Java Swing** desktop app (no frameworks, no external dependencies).
- Watches a build output directory for new/modified `.war` files and redeploys
  them to a local **Apache Tomcat** by running a stop → delete → copy → start cycle.
- Built with **Maven** (Java 8) producing a runnable JAR — no external dependencies.

## Architecture & conventions

Directory roles (lightweight MVC):

| Directory | Responsibility |
| --- | --- |
| `src/main` | Entry point (`Main`) — keep it tiny. |
| `src/graphics` | All Swing UI. No business logic. |
| `src/controller` | Polling loop, deploy pipeline, OS command execution. |
| `src/model` | Shared state (selected paths). |
| `src/finals` | `Finals` (constants) and `Texts` (user-facing strings). |
| `src/logger` | Logging setup. |

Hard rules contributors must follow:

1. **No new third-party dependencies.** JDK + Swing only.
2. **No literals in code.** Every user-facing string goes into `finals.Texts`;
   every number/path/command prefix goes into `finals.Finals`.
3. **Keep the layering.** UI code only touches `controller.Functions`; OS
   commands live exclusively in `controller.ExecuteCommands`.
4. **OS-awareness.** Any new operation must handle Windows, Linux, and macOS
   consistently (see `ExecuteCommands` for the pattern). Non-Windows code paths
   currently carry `// TODO test` — keep the TODO when the new path is untested.
5. **Javadoc & comments:** The existing codebase is un-commented. Preserve that
   style unless the user asks for comments. Use descriptive method names instead.

## Code style

- Java 8 syntax (no records/lambdas-advanced features beyond what is already used).
- Package-private/protected where possible; only entry points are public.
- `java.util.logging` for all diagnostics (see `MyLogger`).

## Known weak spots (candidates for improvement)

- `findPID`/`killPID` are not implemented for Linux/macOS (TODO).
- `copyWarFileToTomcat` uses shell `cp`/`copy` — prefer NIO `Files.copy` for safety.
- Raw types (`Map`, `HashMap` without generics) throughout — safe to generify.
- Resource icon is loaded from a relative path; not bundled into the JAR.

## Assets

- `assets/header.svg` and `assets/mockup.svg` are README showcase graphics.
  Keep them in sync with the README; do not remove without updating it.

## How to run

```bash
mvn clean package
java -jar target/re-war-me-0.9.jar
```

Deployment/testing requires a local Tomcat; never point the tool at a shared or
production server.

## Workflow

- Work on `main` via small commits with clear messages.
- When asked to "optimize/update the project", prefer safe, behavior-preserving
  refactors (cf. known weak spots) and confirm before touching the deploy logic.
- Update `README.md` and `AGENTS.md` whenever structure or conventions change.