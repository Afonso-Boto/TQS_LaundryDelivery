package tqs.project.laundryplatform.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import tqs.project.laundryplatform.account.LoginRequest;
import tqs.project.laundryplatform.account.RegisterRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static tqs.project.laundryplatform.controller.AuthController.*;

@Controller
@Log4j2
public class MainController {

    private static final String REDIRECT_REGISTER = "redirect:/register";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_INDEX = "redirect:/index";

    @GetMapping("/")
    public String mainPage() {
        return REDIRECT_INDEX;
    }

    @GetMapping("/index")
    public String showIndex(Model model, HttpServletRequest request) {
        System.err.println("index");

        if (!hasCookie(request)) {
            System.err.println("cookie not verified");
            return REDIRECT_LOGIN;
        }

        System.err.println("cookie verified");
        System.err.println(getIdFromCookie(request));
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpServletRequest request) {
        System.err.println("get login");

        if (getIdFromCookie(request) != null) return REDIRECT_INDEX;

        model.addAttribute("loginRequest", new LoginRequest());
        return "login_form";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, HttpServletRequest request) {
        System.err.println("register");

        model.addAttribute("registerRequest", new RegisterRequest());
        return "register_form";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        System.err.println("logout");

        if (hasCookie(request)) {
            removeCookie(response);
            return REDIRECT_LOGIN;
        } else {
            return "error";
        }
    }

    @GetMapping("/orders")
    public String orders(Model model, HttpServletRequest request){
        return "orders";
    }

    @GetMapping("/service")
    public String service(Model model, HttpServletRequest request){
        return "service";
    }

    @GetMapping("/pricing")
    public String pricing(Model model, HttpServletRequest request){
        return "pricing";
    }

    @GetMapping("/new_order")
    public String newOrder(Model model, HttpServletRequest request){
        return "new_order";
    }

}
