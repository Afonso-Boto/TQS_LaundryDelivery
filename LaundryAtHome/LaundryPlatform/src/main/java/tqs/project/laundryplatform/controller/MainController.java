package tqs.project.laundryplatform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import tqs.project.laundryplatform.account.LoginRequest;
import tqs.project.laundryplatform.account.RegisterRequest;

import javax.servlet.http.HttpServletRequest;

import static tqs.project.laundryplatform.controller.AuthController.getIdFromCookie;

@Controller
public class MainController {

    @GetMapping("/")
    public ModelAndView mainPage() {
        return new ModelAndView("login_form");
    }

    @GetMapping("/index")
    public ModelAndView showIndex() {
        System.err.println("index");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }


    @GetMapping("/login")
    public String showLoginForm(Model model, HttpServletRequest request) {
        System.err.println("get login");

        if (getIdFromCookie(request) != null)
            return "index";


        model.addAttribute("loginRequest", new LoginRequest());
        return "login_form";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpServletRequest request) {
        System.err.println("register");

        model.addAttribute("registerRequest", new RegisterRequest());
        return "register_form";
    }
}
