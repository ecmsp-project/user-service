package com.ecmsp.userservice.api.rest;


import com.ecmsp.userservice.user.domain.UserFacade;
import com.ecmsp.userservice.user.domain.UserToCreate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody UserCreateRequest request) {
        UserToCreate user = new UserToCreate(request.login(), request.password());
        userFacade.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
