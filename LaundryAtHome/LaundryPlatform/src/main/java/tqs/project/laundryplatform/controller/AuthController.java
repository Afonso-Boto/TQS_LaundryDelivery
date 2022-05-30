package tqs.project.laundryplatform.controller;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.service.AuthenticationService;


@RestController
@RequestMapping("/auth")
@Log4j2
public class AuthController {

    private final AuthenticationService service;

    public AuthController(AuthenticationService service){
        this.service = service;
    }

    @PostMapping("/register")
    public HttpStatus signUp(User user) {

        if (service.register(user)){
            return HttpStatus.OK;
        } else {
            return HttpStatus.CONFLICT;
        }
    }

    @PostMapping("/login")
    public HttpStatus logIn(User user){
        if(service.logIn(user.getUsername(), user.getPassword()))
            return HttpStatus.OK;


        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
