package com.qourier.qourier_app.controller;

import com.qourier.qourier_app.account.*;
import com.qourier.qourier_app.data.Account;
import com.qourier.qourier_app.data.AccountRole;
import com.qourier.qourier_app.data.Customer;
import com.qourier.qourier_app.data.Rider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
public class WebController {

    // setCookie to user
    public void setCookie(HttpServletResponse response, AccountRole role) {
        // Create cookie
        Cookie jwtTokenCookie = new Cookie("role", role.toString());

        jwtTokenCookie.setMaxAge(86400);
        jwtTokenCookie.setSecure(false);
        jwtTokenCookie.setHttpOnly(true);

        // Set cookie onto person
        response.addCookie(jwtTokenCookie);
    }

    // Get cookies
    public Boolean VerifyCookie(HttpServletRequest request, AccountRole role){
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return false;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("role") && cookie.getValue().equals(role.toString())) return true;
        };
        return false;
    }

    // Verify cookie presence
    public Boolean HasCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return false;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("role") && !cookie.getValue().isEmpty()) return true;
        };
        return false;
    }

    private final AccountManager accountManager;

    @Autowired
    public WebController(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @PostMapping("/login")
    public String loginPost(@RequestBody LoginRequest user) {
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
        if(HasCookie(request)){
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
    public String registerCustomerPost(@RequestBody CustomerRegisterRequest request, HttpServletResponse response) {
        if (!accountManager.registerCustomer(request))
            return "register_customer";

        // Set cookie for customer
        setCookie(response, AccountRole.CUSTOMER);

        // TODO redirection
        return "redirect:/index";
    }

    @PostMapping("/register_rider")
    public String registerRiderPost(@RequestBody RiderRegisterRequest request, HttpServletResponse response) {
        if (!accountManager.registerRider(request))
            return "register_rider";

        // Set cookie for rider
        setCookie(response, AccountRole.RIDER);

        // TODO redirection
        return "redirect:/index";
    }

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        AccountRole role = AccountRole.ADMIN;

        // TODO redirection
        // Verify if cookie role is right or not
        if (!VerifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "index";
    }

    @GetMapping("/progress")
    public String progress(Model model, HttpServletRequest request) {
        AccountRole role = AccountRole.ADMIN;

        // TODO redirection
        // Verify if cookie role is right or not
        if (!VerifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "progress";
    }

    @GetMapping("/accounts")
    public String accounts(Model model, HttpServletRequest request) {
        AccountRole role = AccountRole.ADMIN;

        // TODO redirection
        // Verify if cookie role is right or not
        if (!VerifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "accounts";
    }

    @GetMapping("/applications")
    public String applications(Model model, HttpServletRequest request) {
        AccountRole role = AccountRole.ADMIN;

        // TODO redirection
        // Verify if cookie role is right or not
        if (!VerifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "applications";
    }

    @GetMapping("/monitor")
    public String monitor(Model model, HttpServletRequest request) {
        AccountRole role = AccountRole.ADMIN;

        // TODO redirection
        // Verify if cookie role is right or not
        if (!VerifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "monitor";
    }

    @GetMapping("/login")
    public String loginGet(Model model, HttpServletRequest request) {
        AccountRole role = AccountRole.ADMIN;

        // TODO redirection
        // Verify if logged in already
        if (VerifyCookie(request, role))
            return "redirect:/index";

        model.addAttribute("role", role);
        return "login";
    }

    @GetMapping("/deliveries")
    public String deliveries(Model model, HttpServletRequest request) {
        AccountRole role = AccountRole.ADMIN;

        // TODO redirection
        // Verify if cookie role is right or not
        if (!VerifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "deliveries";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        AccountRole role = AccountRole.ADMIN;

        // TODO redirection
        // Verify if cookie role is right or not
        if (!VerifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "profile";
    }

    @GetMapping("/delivery_management")
    public String deliveryManagement(Model model, HttpServletRequest request) {
        AccountRole role = AccountRole.ADMIN;

        // TODO redirection
        // Verify if cookie role is right or not
        if (!VerifyCookie(request, role))
            return "redirect:/login";

        model.addAttribute("role", role);
        return "delivery_management";
    }

}
