# profile-observability-poc

This project demonstrates how to leverage **Spring Boot Observability** powered by **Micrometer** and **OpenTelemetry** to instrument both built-in and **custom** business logic. It implements a simple **Profile Creation API**, then layers on **extra** metrics, tags, and spans in a distributed tracing environment. By capturing business-specific details (e.g., region, user type, tier) at runtime, we gain **richer** observability across our system.

---

## ✨ Features

- **Spring Boot + Micrometer + OpenTelemetry** integration  
- **Custom Observations** for the `createProfile` business logic  
- **Decorator Pattern** to keep business logic separate from observability concerns  
- Exports **traces** to Jaeger or Zipkin  
- Exports **metrics** to Prometheus  
- **Custom business metric**: `profile.created.eu` (counts EU profiles)  
- Automatic capture of **latency**, **traces**, and **tags** (user type, region, tier)

---

## Story & Rationale

### Existing vs. Custom Instrumentation

Many frameworks (like Spring Boot) provide **built-in instrumentation** for common aspects such as HTTP requests, database calls, and system metrics (CPU, memory, etc.). However, when you need **business-specific** visibility—like how many EU users were created, or which tier a user belongs to—you need to add **custom instrumentation**.  

By default, Micrometer and Spring can only tell you that “some request happened” or “a particular query executed.” But **to truly observe** your application in a distributed environment, you often want more contextual data—for example, is the user from the EU region? Are they a VIP tier user? Where do we see latencies creeping in?

### The Observation API

Spring Boot’s **Observation API** (introduced around the time of Spring Boot 3 / Micrometer 1.10+) enables you to create your **own Observations**. These Observations:
- Generate **spans** in distributed tracing, which you can view in Jaeger or Zipkin.  
- Emit **metrics** automatically (like timing / latency) to Micrometer’s exporters (Prometheus, etc.).  
- Let you define **tags** (low-cardinality key-value pairs) that appear in both your traces and metrics.  

In this project, we use a **decorator pattern** (`ObservedProfileService`) around the actual `ProfileServiceImp` to create an `Observation` whenever a new profile is created.

### Why is this Useful in a Distributed System?

In modern microservices, a single user action might pass through multiple services. **Traces** help you see the entire journey (across different services) and each **span** measures a segment of that journey. This is crucial to pinpoint **where** and **why** latency or errors occur.  

- **Trace**: A collection of spans that represent a single request or workflow across multiple services.  
- **Span**: A single unit of work (e.g., calling `ProfileService.create`) within the trace.  

Meanwhile, **metrics** give you aggregated data over time (e.g., how many EU profiles were created in the last 5 minutes? What is the average latency in profile creation?). By combining **traces** (transaction-level details) and **metrics** (time-series data), you get **full-stack observability**.

---

## How It Works

In the `ObservedProfileService` decorator, we have the following code inside `create(...)`:

```java
@Override
public ProfileResponse create(ProfileRequest request) {

    // Custom Business Counter (counts only EU region profiles)
    // Metric Name: profile.created.eu
    // Exported ONLY to Prometheus
    if ("EU".equalsIgnoreCase(request.region())) {
        meterRegistry.counter("profile.created.eu").increment();
    }

    // Create a new Observation (produces a trace span + a timer automatically)
    return Observation.createNotStarted(
            customConvention,              // Optional user-defined convention
            DEFAULT_CONVENTION,            // Fallback convention (sets name + tags)
            () -> new CreateProfileContext(
                    request.userType(),    // Will become both a span attribute and a metric tag
                    request.region(),
                    request.tier()
            ),
            registry                       // ObservationRegistry linked to OTEL
    )
    // Observe the actual business logic execution
    // => Once complete, the observation ends, capturing the total time (latency) and the trace data
    .observe(() -> delegate.create(request));
}
```

#### Breakdown and explanation of haert of the decorator class:

1. **Increment a Custom Counter**:  
   - We only increment `profile.created.eu` if the user’s region is “EU”.  
   - This metric appears in Prometheus and lets us see the count of EU-based profiles created.

2. **Create and Start an Observation**:  
   - `Observation.createNotStarted(...)` sets up a new observation with either a custom or default convention.  
   - The `CreateProfileContext` passes attributes (`userType`, `region`, `tier`) that become **tags** on both the trace and the generated Micrometer metrics.  
   - Linking to `registry` ensures this observation is sent to **OpenTelemetry** (for traces) and **Micrometer** (for metrics).

3. **Observe the Business Logic**:  
   - The final `.observe(() -> delegate.create(request))` calls the real service method and measures the entire duration as a single **span** in the trace.  
   - Once the business logic completes, the observation ends, and we’ve captured:  
       - **Latency** (time taken)  
       - **Trace** details (spans)  
       - **Metrics** (timer, plus any tags)  

---

## ⚙️ Setup

### Prerequisites
- Docker & Docker Compose
- Java 21+
- Maven

### Run the Observability Stack
```bash
docker-compose up
```
This starts:
- **Zipkin** at `http://localhost:9411`  
- **Jaeger** at `http://localhost:16686`  
- **Prometheus** at `http://localhost:9090`

### Run the Application
```bash
./mvnw spring-boot:run
```
---

## 📊 Observability Endpoints

- **Prometheus Metrics**: `http://localhost:8080/actuator/prometheus`
- **Health**: `http://localhost:8080/actuator/health`
- **Jaeger UI**: `http://localhost:16686`
- **Zipkin UI**: `http://localhost:9411`
- **Prometheus UI**: `http://localhost:9090`

---
