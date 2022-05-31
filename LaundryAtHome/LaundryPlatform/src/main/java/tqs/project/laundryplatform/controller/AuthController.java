package tqs.project.laundryplatform.controller;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
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
    public RedirectView signUp(User user) {
        if (service.register(user))
            return new RedirectView("/index");

        return new RedirectView("/register");
    }

    @PostMapping("/login")
    public RedirectView logIn(User user){
        if(service.logIn(user.getUsername(), user.getPassword()))
            return new RedirectView("/index");

        return new RedirectView("/login");
    }
}
