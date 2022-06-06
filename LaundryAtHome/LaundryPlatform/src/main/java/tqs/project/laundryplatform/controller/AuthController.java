package tqs.project.laundryplatform.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import tqs.project.laundryplatform.account.AccountManager;
import tqs.project.laundryplatform.account.LoginRequest;
import tqs.project.laundryplatform.account.LoginResult;
import tqs.project.laundryplatform.account.RegisterRequest;
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.service.AuthenticationService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/auth")
@Log4j2
public class AuthController {

    private final AuthenticationService service;
    private final AccountManager accountManager;

    @Autowired
    public AuthController(AuthenticationService service, AccountManager accountManager) {
        this.service = service;
        this.accountManager = accountManager;
    }

    // setCookie to user
    public void setCookie(HttpServletResponse response, String username) {
        // Create cookie
        Cookie jwtTokenCookie = new Cookie("id", username);

        jwtTokenCookie.setMaxAge(86400);
        jwtTokenCookie.setSecure(false);
        jwtTokenCookie.setHttpOnly(true);
        jwtTokenCookie.setPath("/");

        // Set cookie onto person
        response.addCookie(jwtTokenCookie);
        System.err.println("Cookie set");
        System.err.println(jwtTokenCookie.getValue());
    }

    // Verify cookie presence
    public static boolean hasCookie(HttpServletRequest request) {
        System.err.println();
        System.err.println(request.getCookies());

        Cookie[] cookies = request.getCookies();
        if (cookies == null) return false;
        for (Cookie cookie : cookies)
            if (cookie.getName().equals("id") && !cookie.getValue().isEmpty()) return true;
        return false;
    }

    // Remove cookie
    public static void removeCookie(HttpServletResponse response) {
        Cookie jwtTokenCookie = new Cookie("id", "null");

        jwtTokenCookie.setMaxAge(0);
        jwtTokenCookie.setSecure(false);
        jwtTokenCookie.setHttpOnly(true);

        // Set cookie onto user
        response.addCookie(jwtTokenCookie);
    }

    // Get ID
    public static String getIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies)
            if (cookie.getName().equals("id")) {
                return cookie.getValue();
            }
        return null;
    }

    @PostMapping("/register")
    public String signUp(RegisterRequest request, HttpServletResponse response) {
        if (!accountManager.register(request)) return "redirect:/auth/register";

        // Set cookie for customer
        setCookie(response, request.getUsername());

        return "redirect:/index";
    }

    @PostMapping("/login")
    public String loginPost(LoginRequest user, HttpServletResponse response) {
        LoginResult result = accountManager.login(user);
        System.err.println("Login result: " + result);

        if (result.equals(LoginResult.WRONG_CREDENTIALS)
                || result.equals(LoginResult.NON_EXISTENT_ACCOUNT)) {
            return "login_form";
        }

        // Set cookie for customer
        setCookie(response, user.getUsername());

        return "redirect:/index";
    }
}
