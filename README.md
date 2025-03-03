# FilmBase

## Introduction

**FilmBase** is a powerful and secure API built with **Spring Boot** for comprehensive movie management. This system is designed to provide a structured way to handle movies, ensuring strict access control where **only administrators** can perform modifications. The system features secure authentication, file management, and a detailed API documentation interface through **Swagger/OpenAPI**.

## Key Features 🚀

- **Admin-Only Management:** Only administrators can add, update, delete movies, and manage files.
- **File Upload Support:** Upload images and videos related to movies.
- **Advanced Security:** Secure API endpoints using **JWT & Spring Security**.
- **Efficient Data Handling:** Supports **pagination**, sorting, and multiple search options.
- **Comprehensive Documentation:** Fully documented API using **Swagger/OpenAPI**.

## Technologies Used 🛠️

- **Java 17** – Primary programming language.
- **Spring Boot 3.4.3** – The core framework of the application.
- **Spring Security & JWT** – Ensuring a high level of security.
- **MySQL** – Relational database for movie storage.
- **Spring Data JPA** – Simplifies database operations.
- **Spring Boot Actuator** – For monitoring and performance analysis.
- **Thymeleaf** – Template engine for email generation.
- **Swagger/OpenAPI** – For API documentation.
- **Lombok** – Reducing boilerplate code.

## Project Structure 📂

```
muhamedsaad112-filmbase/
├── src/
│   ├── main/
│   │   ├── java/com/filmbase/
│   │   │   ├── controller/       # REST API controllers
│   │   │   ├── dto/              # Data Transfer Objects (DTOs)
│   │   │   ├── entity/           # Database entity models
│   │   │   ├── exception/        # Custom exception handling
│   │   │   ├── repository/       # Data repositories
│   │   │   ├── security/         # Security configurations & JWT handling
│   │   │   ├── service/          # Business logic services
│   │   ├── resources/
│   │   │   ├── application.properties  # Application configurations
│   ├── test/java/com/filmbase/FilmBaseApplicationTests.java  # Tests
├── pom.xml  # Maven dependency management
```

## Prerequisites 📌

Before running the application, ensure you have:

- **Java 17**
- **Maven**
- **MySQL** or any compatible database

## Running the Application 🚀

### 1️⃣ Clone the Project
```bash
git clone https://github.com/MuhamedSaad112/Movie-Hub.git
cd muhamedsaad112-filmbase
```

### 2️⃣ Build the Project
```bash
./mvnw clean install
```

### 3️⃣ Start the Application
```bash
./mvnw spring-boot:run
```

## API Documentation 📖

Once the application is running, you can access the API documentation at:

```
http://localhost:8080/swagger-ui/index.html
```

## Key API Endpoints 🌐

### 🎬 Movie Management (Admin-Only)
| Method   | Endpoint                               | Description                       |
| -------- | -------------------------------------- | --------------------------------- |
| `POST`   | `/api/v1/admin/movie/add-movie`        | Add a new movie (Admin Only)      |
| `PUT`    | `/api/v1/admin/movie/update/{movieId}` | Update movie details (Admin Only) |
| `DELETE` | `/api/v1/admin/movie/delete/{movieId}` | Delete a movie (Admin Only)       |

### 🎥 Movie Browsing (User)
| Method | Endpoint                            | Description              |
| ------ | ----------------------------------- | ------------------------ |
| `GET`  | `/api/v1/movie/{movieId}`           | Get movie by ID          |
| `GET`  | `/api/v1/movie/all-list`            | Get all movies           |
| `GET`  | `/api/v1/movie/title/{title}`       | Search movie by title    |
| `GET`  | `/api/v1/movie/director/{director}` | Search movie by director |
| `GET`  | `/api/v1/movie/studio/{studio}`     | Search movie by studio   |
| `GET`  | `/api/v1/movie/allMoviesPage`       | Fetch paginated movies   |

### 📂 File Management (Admin-Only)
| Method   | Endpoint                    | Description                     |
| -------- | --------------------------- | ------------------------------- |
| `POST`   | `/file/upload`              | Upload a file (Admin Only)      |
| `GET`    | `/file/download/{fileName}` | Download a file (Admin Only)    |
| `DELETE` | `/file/{fileName}`          | Delete a file (Admin Only)      |
| `GET`    | `/file/list`                | List all uploaded files (Admin) |

## License 📜

This project is licensed under **Apache License 2.0**. For more details, check [LICENSE](http://www.apache.org/licenses/LICENSE-2.0).

## Developer 👨‍💻

- **Name:** Mohamed Saad  
- **Email:** [m.saad1122003@gmail.com](mailto:m.saad1122003@gmail.com)  
- **GitHub:** [MuhamedSaad112](https://github.com/MuhamedSaad112)

