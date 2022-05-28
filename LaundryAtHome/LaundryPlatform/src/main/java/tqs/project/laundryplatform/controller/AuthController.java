package tqs.project.laundryplatform.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import tqs.project.laundryplatform.service.AuthenticationService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService service;

    public AuthController(AuthenticationService service){
        this.service = service;
    }

    @PostMapping("/register")
    public HttpStatus signUp(@RequestBody JSONObject userInfo) {

        if(service.register(userInfo.getString("username"),
                userInfo.getString("full_name"),
                userInfo.getString("password"),
                userInfo.getInt("phone_number"),
                userInfo.getString("email")))
            return HttpStatus.CREATED;

        return HttpStatus.PRECONDITION_FAILED;

    }

    @GetMapping
    public HttpStatus logIn(@RequestParam String username, @RequestParam String password){
        if(service.logIn(username, password))
            return HttpStatus.ACCEPTED;


        return HttpStatus.UNAUTHORIZED;
    }
}
