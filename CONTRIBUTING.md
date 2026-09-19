# Contributing to ReWarMe

Thanks for your interest! This is a legacy project kept for posterity, but
issues and pull requests are welcome.

## Before you start

Read [`AGENTS.md`](AGENTS.md) — it documents this codebase's architecture and
hard rules. In short:

- Keep the layering: UI in `graphics`, logic in `controller`, state in `model`,
  constants/strings in `finals`.
- No new third-party dependencies (JDK + Swing only).
- Match the existing Java 8 style.

## Build & verify

```bash
mkdir -p out/production
javac -d out/production $(find src -name '*.java')
jar cfe re-war-me.jar main.Main -C out/production .
```

The [Build workflow](.github/workflows/build.yml) compiles the project on every
push and pull request.

## Workflow

1. Fork the repo and create a branch: `git checkout -b feature/your-feature`.
2. Make your change and confirm it compiles.
3. Commit with a clear message, push, and open a pull request.

## License

There is currently no explicit license file. By contributing, you agree your
changes may be distributed with the project.
