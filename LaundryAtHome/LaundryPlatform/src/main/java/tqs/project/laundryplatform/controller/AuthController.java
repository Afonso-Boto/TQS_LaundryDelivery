package tqs.project.laundryplatform.controller;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
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
    public HttpStatus signUp(@RequestBody String userData) {
        JSONObject userInfo = new JSONObject(userData);
        String username;
        String fullName;
        String password;
        int phoneNumber;
        String email;

        System.err.println(userInfo);

        try {
            username = userInfo.getString("username");
            fullName = userInfo.getString("full_name");
            password = userInfo.getString("password");
            phoneNumber = userInfo.getInt("phone_number");
            email = userInfo.getString("email");
        } catch (Exception e) {
            log.error("Error parsing JSON: " + e.getMessage());
            return HttpStatus.BAD_REQUEST;
        }

        if (service.register(username, fullName, password, phoneNumber, email)){
            return HttpStatus.OK;
        } else {
            return HttpStatus.CONFLICT;
        }
    }

    @PostMapping("/login")
    public HttpStatus logIn(@RequestParam("username") String username, @RequestParam("password") String password){
        if(service.logIn(username, password))
            return HttpStatus.OK;


        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
