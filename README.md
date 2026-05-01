# CareerCrack

CareerCrack is a full-stack web application for practicing and sharing coding interview problems. It includes a React + Vite frontend (in `client/`) and a Spring Boot backend (in `server/`). The project provides APIs for authentication, problem management, categories, and resources used by the web client.

## Features

- User authentication and authorization
- Create, update, and browse coding problems and categories
- REST API served by Spring Boot
- Modern frontend built with React and Vite

## Tech stack

- Frontend: React, Vite, JavaScript
- Backend: Java, Spring Boot, Maven
- Database: (project contains SQL schema under `server/db.sql`) — PostgreSQL recommended for local deployment

## Local development

Prerequisites:

- Java 17+ (or the version used by the project)
- Maven (or use the included `mvnw` wrapper)
- Node.js 16+ and npm or Yarn
- PostgreSQL (or another supported RDBMS if configured)

1. Start the database

- Create a PostgreSQL database and user for the application. Example:

```bash
createdb careercrack_dev
# or using psql:
# psql -c "CREATE DATABASE careercrack_dev;"
```

If the project provides `server/db.sql`, apply it to initialize schema:

```bash
psql -d careercrack_dev -f server/db.sql
```

2. Configure the backend

Copy or edit `server/src/main/resources/application.properties` to set your database URL, username, and password. Example properties to set:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/careercrack_dev
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
```

3. Run the backend

From the repository root (or `server/`):

```bash
cd server
./mvnw spring-boot:run
# or: mvn spring-boot:run
```

4. Run the frontend

Open a new terminal and start the client dev server:

```bash
cd client
npm install
npm run dev
```

By default, the frontend dev server (Vite) will proxy API calls to the backend if the client is configured that way. Visit the frontend URL printed by Vite (usually `http://localhost:5173`) to open the app.

## Build for production

Backend:

```bash
cd server
./mvnw package
# then run the produced jar from target/
java -jar target/*.jar
```

Frontend:

```bash
cd client
npm run build
# Deploy the generated `client/dist` (or `build`) assets to your static host or serve them behind the backend.
```

## Running tests

- Backend unit/integration tests: `cd server && ./mvnw test`
- Frontend tests (if present): `cd client && npm test`

## Contributing

Contributions and improvements are welcome. Please open issues or pull requests with changes and follow standard GitHub contribution practices.

## License

See the `LICENSE` file in this repository for license details.
