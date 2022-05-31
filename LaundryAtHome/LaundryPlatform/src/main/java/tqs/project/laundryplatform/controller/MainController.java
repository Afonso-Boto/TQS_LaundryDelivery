package tqs.project.laundryplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import tqs.project.laundryplatform.model.User;

@RestController
public class MainController {

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping("/login")
    public ModelAndView showLoginForm(User user) {
        System.err.println("get login");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login_form");
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView showRegisterForm(User user) {
        System.err.println("register");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register_form");
        return modelAndView;
    }
}
