package com.qourier.qourier_app.controller;

import com.qourier.qourier_app.account.*;
import com.qourier.qourier_app.data.AccountRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Locale;

import static com.qourier.qourier_app.data.AccountRole.*;

@Controller
public class WebController {

    // setCookie to user
    public void setCookie(HttpServletResponse response, String email) {
        // Create cookie
        Cookie jwtTokenCookie = new Cookie("id", email);

        jwtTokenCookie.setMaxAge(86400);
        jwtTokenCookie.setSecure(false);
        jwtTokenCookie.setHttpOnly(true);

        // Set cookie onto person
        response.addCookie(jwtTokenCookie);
    }

    // Get cookies
    public boolean verifyCookie(HttpServletRequest request, AccountRole role){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return false;
        for (Cookie cookie : cookies)
            if (cookie.getName().equals("id") && cookie.getValue().equals(role.toString()))
                return true;
        return false;
    }

    // Verify cookie presence
    public boolean hasCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return false;
        for (Cookie cookie : cookies)
            if (cookie.getName().equals("id") && !cookie.getValue().isEmpty())
                return true;
        return false;
    }

    // Get role
    public AccountRole getRoleFromCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies)
            if (cookie.getName().equals("id")){
                // TODO add method to get role from email
                String email = "";
                switch (email) {
                    case "ADMIN":
                        return ADMIN;
                    case "RIDER":
                        return RIDER;
                    case "CUSTOMER":
                        return CUSTOMER;
                }
            }
        return null;
    }

    private final AccountManager accountManager;

    @Autowired
    public WebController(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @PostMapping("/login")
    public String loginPost(LoginRequest user) {
        LoginToken token = accountManager.login(user);
        if (token.getLoginResult().equals(LoginResult.WRONG_CREDENTIALS) || token.getLoginResult().equals(LoginResult.NON_EXISTENT_ACCOUNT))
            return "login";

        // TODO redirection
        return switch (token.getRole()) {
            case ADMIN -> "index";
            case RIDER -> "index";
            case CUSTOMER -> "index";
        };
    }

    @GetMapping(value="/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // See if we are logged in or not
        if(hasCookie(request)){
            Cookie jwtTokenCookie = new Cookie("role", "null");

            jwtTokenCookie.setMaxAge(0);
            jwtTokenCookie.setSecure(false);
            jwtTokenCookie.setHttpOnly(true);

            // Set cookie onto user
            response.addCookie(jwtTokenCookie);
            return "redirect:/login";
        }else{
            return "error";
        }
    }

    @PostMapping("/register_customer")
    public String registerCustomerPost(CustomerRegisterRequest request, HttpServletResponse response) {
        if (!accountManager.registerCustomer(request))
            return "register_customer";

        // Set cookie for customer
        setCookie(response, request.getEmail());

        return "redirect:/index";
    }

    @PostMapping("/register_rider")
    public String registerRiderPost(RiderRegisterRequest request, HttpServletResponse response) {
        if (!accountManager.registerRider(request))
            return "register_rider";

        // Set cookie for rider
        setCookie(response, request.getEmail());

        return "redirect:/index";
    }

    @GetMapping("/")
    public String indexToRoot() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model, HttpServletRequest request) {

        // Verify if cookie role is right or not
        if (!verifyCookie(request, ADMIN) && !verifyCookie(request, CUSTOMER) && !verifyCookie(request, RIDER))
            return "redirect:/login";

        model.addAttribute("role", getRoleFromCookie(request));
        return "index";
    }

    @GetMapping("/progress")
    public String progress(Model model, HttpServletRequest request) {
        AccountRole role = ADMIN;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "progress";
    }

    @GetMapping("/accounts")
    public String accounts(Model model, HttpServletRequest request) {
        AccountRole role = ADMIN;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "accounts";
    }

    @GetMapping("/applications")
    public String applications(Model model, HttpServletRequest request) {
        AccountRole role = ADMIN;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "applications";
    }

    @GetMapping("/monitor")
    public String monitor(Model model, HttpServletRequest request) {
        AccountRole role = ADMIN;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "monitor";
    }

    @GetMapping("/login")
    public String loginGet(Model model, HttpServletRequest request) {

        // Verify if logged in already
        if (getRoleFromCookie(request) != null)
            return "redirect:/index";

        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping("/deliveries")
    public String deliveries(Model model, HttpServletRequest request) {
        AccountRole role = RIDER;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "deliveries";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {

        // Verify if cookie role is right or not
        if (!verifyCookie(request, ADMIN) && !verifyCookie(request, CUSTOMER) && !verifyCookie(request, RIDER))
            return "redirect:/login";

        return (getRoleFromCookie(request) == RIDER) ? "profile_rider" : "profile_customer";
    }

    @GetMapping("/delivery_management")
    public String deliveryManagement(Model model, HttpServletRequest request) {
        AccountRole role = CUSTOMER;

        // Verify if cookie role is right or not
        if (!verifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "delivery_management";
    }

    @GetMapping("/register_rider")
    public String registerRiderGet(Model model, HttpServletRequest request) {
        model.addAttribute("riderRegisterRequest", new RiderRegisterRequest());
        return "register_rider";
    }

    @GetMapping("/register_customer")
    public String registerCustomerGet(Model model, HttpServletRequest request) {
        model.addAttribute("customerRegisterRequest", new CustomerRegisterRequest());
        return "register_customer";
    }

}
