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
POST http://localhost:8500/api/users
Content-Type: application/json

{
  "login": "testuser",
  "password": "testpassword"
}
```

### Authenticate User
```
POST http://localhost:8500/api/auth/authenticate
Content-Type: application/json

{
  "login": "testuser",
  "password": "testpassword"
}
```