# Auth Manager Service Application

This project is a Ktor-based application that provides user authentication and protected routes. The application allows users to sign up, sign in, and access a protected endpoint.

## Table of Contents

- [Getting Started](#getting-started)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Sign Up](#1-sign-up)
- [Sign In](#2-sign-in)
- [Protected Route](#3-protected-route)
- [Configuration](#configuration)
- [Built With](#built-with)
- [License](#license)

## Getting Started

These instructions will help you set up and run the application on your local machine for development and testing purposes.

### Prerequisites

- [JDK 8+](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Gradle](https://gradle.org/install/)
- [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) account (for storing user data)

### Installing

1. Clone the repository:

  ```bash
  git clone https://github.com/your-username/ktor-application.git
  cd ktor-application
  ```

2. Configure your environment variables:

  Create a `.env` file in the root directory and add the following environment variables:

  ```plaintext
  JWT_ISSUER=https://your-domain.com/
  JWT_AUDIENCE=your-audience
  JWT_REALM=your-realm
  JWT_SECRET=your-secret
  MONGO_PW=your-mongodb-password
  ```

3. Build the project:

  ```bash
  ./gradlew build
  ```

## Running the Application

To run the application, use the following command:

```bash
./gradlew run
```

The server will start on `http://localhost:5500`.

## API Endpoints

### 1. Sign Up

**URL**: `http://localhost:5500/signup`
**Method**: `POST`
**Body**:

```json
{
"name": "shiv",
"email": "shiv@test.com",
"password": "test1234"
}
```

**Response**:

- `200 OK`: User successfully registered and returns a JWT token.
- `400 Bad Request`: Invalid request data.

### 2. Sign In

**URL**: `http://localhost:5500/signin`
**Method**: `POST`
**Body**:

```json
{
"name": "",
"email": "shiv@test.com",
"password": "test1234"
}
```

**Response**:

- `200 OK`: Returns a JWT token for authentication.
- `401 Unauthorized`: Invalid credentials.

### 3. Protected Route

**URL**: `http://localhost:5500/secret`
**Method**: `GET`
**Headers**:

```plaintext
Authorization: Bearer <JWT Token>
```

**Response**:

- `200 OK`: Access to the protected resource.
- `401 Unauthorized`: Invalid or missing JWT token.

## Configuration

The application is configured using the `application.conf` file located in the `resources` directory. Ensure that the following properties are correctly set:

```hocon
ktor {
  deployment {
    port = 5500
  }
  application {
    modules = [ online.nsandroid.ApplicationKt.module ]
  }
  security {
    jwt {
        issuer = ${?JWT_ISSUER}
        audience = ${?JWT_AUDIENCE}
        realm = ${?JWT_REALM}
        secret = ${?JWT_SECRET}
    }
  }
}
```

### TODO Items from the Code

The `Application.kt` file includes TODO items to enhance the functionality and security of the application. Ensure to address the following:

- **TODO**: Pass database name `val dbName = ""`
- **TODO**: Configure before testing it: `connectionString = "mongodb+srv://<username>:<password>@<cluster-url>/<dbname>?retryWrites=true&w=majority"`
- **TODO**: Before running pass JWT secret here: `const val JWT_SECRET = ""`
- **TODO**: Add MongoDB password here: `const val MONGO_PW = ""`

## Built With

- [Ktor](https://ktor.io/) - Framework for building asynchronous servers and clients in connected systems.
- [KMongo](https://litote.org/kmongo/) - Kotlin toolkit for MongoDB.
- [Gradle](https://gradle.org/) - Build tool.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
