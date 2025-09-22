You can test the login endpoint using the following  command. Just copy & paste it into HTTP request or any other REST client.
Added user is hardcoded in inMemoryRepository.
```
POST http://localhost:8080/auth/authenticate
Content-Type: application/json

{
  "login": "testuser",
  "password": "testpassword"
}
```