# Credit Card Application Microservice
API Documentation
Swagger documentation is available at http://localhost:8080/swagger-ui.html.

## Overview

The Credit Card Application Microservice is part of a larger ecosystem responsible for credit card application request.

## Technologies

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **H2 Database (In-Memory)**
- **SpringDoc OpenAPI (Swagger)**
- **JUnit 5 (JUnit Jupiter)**
- **Mockito**

## Setup

### Prerequisites

- Java 17
- Maven (for building the project)

### Clone the Repository

git clone https://github.com/sgshivi08/credit-card-ms.git
cd credit-card-ms

Build the Project mvn clean install

Run the Application mvn spring-boot:run

The application will start on port 8080 by default.
Testing
To run unit tests, use:

mvn test

Configuration
Application Properties: Configure application settings in src/main/resources/application.properties.
