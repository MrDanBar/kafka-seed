# Unit Test Plan

## 1. Dependencies to Add (`pom.xml`)

The current `pom.xml` is missing `spring-boot-starter-test`, which provides JUnit 5 and Mockito. Add:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

> The existing `spring-boot-starter-webmvc-test` covers MockMvc slice tests, but `spring-boot-starter-test` is needed for standalone Mockito-based unit tests.

---

## 2. Test Classes

### 2.1 `DogAdoptionSchedulerTest`
**File:** `src/test/java/com/aydlabs/bot/adoption/DogAdoptionSchedulerTest.java`
**Type:** Pure unit test (no Spring context)

| Test | What to assert |
|------|---------------|
| `schedule_returnsDateThreeDaysFromNow` | Parse returned `Instant` string; assert it is between `now + 2d 23h` and `now + 3d 1h` |
| `emailNotification_returnsDateThreeDaysFromNow` | Same bound check as above |

No mocks needed — both methods are stateless logic only.

---

### 2.2 `MyTopicProducerTest`
**File:** `src/test/java/com/aydlabs/bot/producers/MyTopicProducerTest.java`
**Type:** Unit test with Mockito (`@ExtendWith(MockitoExtension.class)`)

The constructor builds a real `KafkaProducer`, so the test must either:
- Use `@Value` injection via `ReflectionTestUtils`, or
- Refactor the constructor to accept a `KafkaProducer<String, String>` directly (preferred for testability — note this in the plan as a prerequisite refactor).

| Test | What to assert |
|------|---------------|
| `send_withKeyAndMessage_callsProducerSend` | Mock `KafkaProducer`; verify `producer.send(record, callback)` is called once with the correct topic, key, and value |
| `send_withMessageOnly_usesNullKey` | Call `send(message)`; verify the `ProducerRecord` key is `null` |
| `close_flushesAndClosesProducer` | Call `close()`; verify `producer.flush()` then `producer.close()` are called in order |

---

### 2.3 `MyAdoptionDogTest`
**File:** `src/test/java/com/aydlabs/bot/adoption/controller/MyAdoptionDogTest.java`
**Type:** MockMvc slice test (`@WebMvcTest(MyAdoptionDog.class)`)

Mock the following beans: `PromptChatMemoryAdvisor`, `ChatClient.Builder`, `JdbcClient`, `DogRepository`, `VectorStore`, `DogAdoptionScheduler`, `MyTopicProducer`.

| Test | What to assert |
|------|---------------|
| `inquire_returnsAiResponse` | `GET /testUser/assistant?question=hola` → stub `ChatClient` chain to return `"Hola!"` → assert 200 and body `"Hola!"` |
| `inquire_passesConversationIdFromPathVariable` | Verify `ChatMemory.CONVERSATION_ID` param is set to the path variable value |
| `sendMessage_callsProducerAndReturnsOK` | `GET /testUser/notify?question=test` → verify `producer.send("testUser", "test")` called → assert 200 body `"OK"` |
| `sendMessage_doesNotInvokeAi` | Verify `ChatClient` is never called during a `/notify` request |

---

### 2.4 `DogTest`
**File:** `src/test/java/com/aydlabs/bot/adoption/database/DogTest.java`
**Type:** Pure unit test

| Test | What to assert |
|------|---------------|
| `dog_recordAccessors` | Construct `new Dog(1, "Rex", "owner", "desc")` and assert each accessor returns the expected value |
| `dog_equalityAndHashCode` | Two `Dog` instances with same fields are equal and share the same hash code |

---

### 2.5 `DogAdoptionSuggestionTest`
**File:** `src/test/java/com/aydlabs/bot/adoption/database/DogAdoptionSuggestionTest.java`
**Type:** Pure unit test

| Test | What to assert |
|------|---------------|
| `suggestion_recordAccessors` | Construct and assert all three fields |
| `suggestion_equalityAndHashCode` | Two instances with same fields are equal |

---

### 2.6 `MyBotTest`
**File:** `src/test/java/com/aydlabs/bot/MyBotTest.java`
**Type:** Unit test with Mockito

| Test | What to assert |
|------|---------------|
| `promptChatMemoryAdvisor_buildsBeanSuccessfully` | Mock `DataSource`; call `new MyBot().promptChatMemoryAdvisor(mockDataSource)` → assert returned bean is not null and is of type `PromptChatMemoryAdvisor` |

> Note: This test requires a real or in-memory DataSource since `JdbcChatMemoryRepository` calls `dataSource.getConnection()`. Use H2 in-memory DataSource.

---

## 3. Prerequisite Refactor

**`MyTopicProducer`** — extract `KafkaProducer` construction into a protected factory method or accept it as a constructor parameter. This removes the direct `new KafkaProducer(props)` call that makes unit testing hard without real Kafka:

```java
// Option: package-private constructor for tests
MyTopicProducer(KafkaProducer<String, String> producer) {
    this.producer = producer;
}
```

---

## 4. Test Directory Structure

```
src/test/java/com/aydlabs/bot/
├── MyBotTest.java
├── adoption/
│   ├── DogAdoptionSchedulerTest.java
│   ├── controller/
│   │   └── MyAdoptionDogTest.java
│   └── database/
│       ├── DogTest.java
│       └── DogAdoptionSuggestionTest.java
└── producers/
    └── MyTopicProducerTest.java
```

---

## 5. Execution

```bash
mvn test                        # Run all unit tests
mvn test -pl . -Dtest=MyTopicProducerTest   # Run a single test class
```
