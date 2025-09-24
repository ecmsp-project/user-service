# User Service

## Installation

**Generate RSA keys:**
   ```bash
   cd src/main/resources/local/secrets/
   openssl genrsa > local.private.key
   openssl rsa -in local.private.key -pubout > local.public.key
   ```

## User Controller API

### Create User
```
POST http://localhost:8080/api/users
Content-Type: application/json

{
  "login": "newuser",
  "password": "password123"
}
```

### Authenticate User
```
POST http://localhost:8080/auth/authenticate
Content-Type: application/json

{
  "login": "testuser",
  "password": "testpassword"
}
```