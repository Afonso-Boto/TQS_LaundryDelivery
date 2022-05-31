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
import static tqs.project.laundryplatform.controller.AuthController.verifyCookie;

@Controller
public class MainController {

    private static final String REDIRECT_REGISTER = "register_form";
    private static final String REDIRECT_LOGIN = "login_form";
    private static final String REDIRECT_INDEX = "index";

    @GetMapping("/")
    public String mainPage() {
        return REDIRECT_INDEX;
    }

    @GetMapping("/index")
    public String showIndex(Model model, HttpServletRequest request) {
        System.err.println("index");

        if(verifyCookie(request))
            return REDIRECT_LOGIN;

        return REDIRECT_INDEX;
    }


    @GetMapping("/login")
    public String showLoginForm(Model model, HttpServletRequest request) {
        System.err.println("get login");

        if (getIdFromCookie(request) != null)
            return REDIRECT_INDEX;


        model.addAttribute("loginRequest", new LoginRequest());
        return REDIRECT_LOGIN;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpServletRequest request) {
        System.err.println("register");

        model.addAttribute("registerRequest", new RegisterRequest());
        return REDIRECT_REGISTER;
    }
}
