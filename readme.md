# User Service

## Installation

**Generate RSA keys:**
   ```bash
   cd src/main/resources/local/secrets/
   openssl genrsa > local.private.key
   openssl rsa -in local.private.key -pubout > local.public.key
   ```
**For macOS (maybe windows) users run 1 more command:**
   ```bash
   openssl pkcs8 -topk8 -inform PEM -outform PEM -in local.private.key -out local.private.pkcs8.key -nocrypt
   ```
then change `secret-key-file` in application.yml to ```secret-key-file: classpath:local/secrets/local.private.pkcs8.key```
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