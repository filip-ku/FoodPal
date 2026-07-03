# FoodPal

FoodPal is a multilingual desktop recipe manager built by an eight-person team during an eight-week Collaborative Software Engineering project at TU Delft. It combines a JavaFX client with a Spring Boot REST API and real-time WebSocket updates.

## Features

- Create, edit, browse, and delete recipes and ingredients
- Search recipes by title, preparation step, or ingredient
- Include or exclude ingredients from searches, helping with dietary preferences and allergies
- Scale recipe quantities and convert between common units
- Save favourite recipes locally
- Export recipes with their currently selected serving scale
- Switch live between English, Dutch, Spanish, and French
- Synchronise ingredient and recipe changes between connected clients using WebSockets

## Technology

- Java and JavaFX
- Spring Boot, Spring Data JPA, and REST
- STOMP over WebSockets
- H2 in-memory database
- Maven multi-module architecture
- JUnit, Mockito, and TestFX

## Architecture

The project is divided into three Maven modules:

- `client` — JavaFX interface, REST client, localisation, and WebSocket subscriptions
- `server` — Spring Boot API, persistence layer, services, and WebSocket publishing
- `commons` — domain models and shared WebSocket messages

## Running the application

### Prerequisites

- JDK 25
- No separate Maven installation is required; the Maven wrapper is included

Start the server from the repository root:

```bash
./mvnw -pl server -am spring-boot:run
```

In a second terminal, start the client:

```bash
./mvnw -pl client -am javafx:run
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Testing

Run the automated test suite from the repository root:

```bash
./mvnw test
```

## Project context

FoodPal was developed collaboratively for TU Delft's Collaborative Software Engineering Project. The complete Git history is retained to represent the team's original development process and individual contributions.

## License

This project is available under the [Apache License 2.0](LICENSE.txt).
