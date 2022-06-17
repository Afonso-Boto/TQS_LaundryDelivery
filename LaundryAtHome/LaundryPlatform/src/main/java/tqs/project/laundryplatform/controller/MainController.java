package tqs.project.laundryplatform.controller;

import static tqs.project.laundryplatform.controller.AuthController.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tqs.project.laundryplatform.account.LoginRequest;
import tqs.project.laundryplatform.account.RegisterRequest;

@Controller
@RequestMapping("/")
@CrossOrigin(origins = "*")
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

    @GetMapping("/new_order")
    public String newOrder(Model model, HttpServletRequest request) {
        return "new_order";
    }

    @GetMapping("/orders")
    public String orders(Model model, HttpServletRequest request) {
        return "orders";
    }

    @GetMapping("/service")
    public String service(Model model, HttpServletRequest request) {
        return "service";
    }

    @GetMapping("/pricing")
    public String pricing(Model model, HttpServletRequest request) {
        return "pricing";
    }

    @GetMapping("/ok")
    public String ok(Model model, HttpServletRequest request) {
        System.out.println("ok");
        return "ok_page";
    }

    @GetMapping("/error")
    public String error(Model model, HttpServletRequest request) {
        return "error";
    }
}
