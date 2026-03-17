# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
mvn clean package          # Build JAR
mvn spring-boot:run        # Run the application
mvn test                   # Run tests
```

**Prerequisites:**
- Java 25+ (GraalVM compatible)
- PostgreSQL with pgVector extension on `localhost:5432`
- Kafka broker on `localhost:9092`
- `OPENAI_API_KEY` environment variable set

## Architecture

Spring Boot 4.0.3 application that implements a Spanish-language dog adoption AI assistant for the Latin American market. It combines RAG (retrieval-augmented generation), conversational memory, AI tool invocation, and Kafka messaging.

### Key Components

**`MyBot.java`** — Spring Boot entry point. Configures `PromptChatMemoryAdvisor` with JDBC-backed persistent memory.

**`adoption/controller/MyAdoptionDog.java`** — Two REST endpoints:
- `GET /{user}/assistant?question=` — Chat with the AI adoption assistant
- `GET /{user}/notify?question=` — Send a Kafka notification

The controller wires three Spring AI advisors together: chat memory (per-user conversation history), vector store (pgVector RAG over dog records), and tools (scheduling/email).

**`adoption/database/`** — `Dog` record entity, `DogAdoptionSuggestion` model, and `DogRepository` (Spring Data JDBC `ListCrudRepository`). Dogs are loaded into the vector store on first request.

**`adoption/DogAdoptionScheduler.java`** — Spring AI `@Tool`-annotated methods that the LLM can invoke directly:
- `schedule()` — returns an appointment date 3 days from now
- `emailNotification()` — sends email notifications

**`producers/MyTopicProducer.java`** — Kafka producer that publishes to `my-topic-1`. Flushes and closes gracefully on application shutdown.

### Data Flow

1. User query → `MyAdoptionDog` controller
2. Spring AI chains: memory advisor → vector store advisor (semantic search over pgVector) → tools advisor → OpenAI model
3. If scheduling/email needed, the model invokes `DogAdoptionScheduler` tools
4. For notifications: controller → `MyTopicProducer` → Kafka topic `my-topic-1`

### Infrastructure

- **Vector embeddings**: pgVector with 1536 dimensions (OpenAI embedding size); schema auto-initialized
- **Chat memory**: JDBC-backed, schema initialized automatically (`spring.ai.chat.memory.repository.jdbc.initialize-schema=always`)
- **Native image**: GraalVM Native Maven Plugin configured for low-latency deployments

## API Testing

Bruno collection in `bruno/MyBotDog/` covers all endpoints. Load it in [Bruno](https://www.usebruno.com/) for manual API testing.
