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

    // Verefy cookie presence
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
    public String registerCustomerPost(@RequestBody CustomerRegisterRequest request) {
        if (!accountManager.registerCustomer(request))
            return "register_customer";

        // TODO redirection
        return "index";
    }

    @PostMapping("/register_rider")
    public String registerRiderPost(@RequestBody RiderRegisterRequest request) {
        if (!accountManager.registerRider(request))
            return "register_rider";

        // TODO redirection
        return "index";
    }

    @GetMapping("/")
    public ModelAndView index() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "index";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        return modelAndView;
    }

    @GetMapping("/progress")
    public ModelAndView progress() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "progress";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        return modelAndView;
    }

    @GetMapping("/accounts")
    public ModelAndView accounts() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "accounts";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        return modelAndView;
    }

    @GetMapping("/applications")
    public ModelAndView applications() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "applications";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        return modelAndView;
    }

    @GetMapping("/monitor")
    public ModelAndView monitor() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "monitor";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView loginGet() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "login";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        modelAndView.addObject("loginRequest", new LoginRequest());
        return modelAndView;
    }

    @GetMapping("/deliveries")
    public ModelAndView deliveries() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "deliveries";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        return modelAndView;
    }

    @GetMapping("/profile")
    public ModelAndView profile() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "profile";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        return modelAndView;
    }

    @GetMapping("/delivery_management")
    public ModelAndView deliveryManagement() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "delivery_management";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        return modelAndView;
    }

    @GetMapping("/register_rider")
    public ModelAndView registerRiderGet() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "register_rider";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        modelAndView.addObject("riderRegisterRequest", new RiderRegisterRequest());
        return modelAndView;
    }

    @GetMapping("/register_customer")
    public ModelAndView registerCustomerGet() {
        AccountRole role;
        String viewName;

        // TODO redirection
        role = AccountRole.ADMIN;
        viewName = "register_customer";

        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("role", role);
        modelAndView.addObject("customerRegisterRequest", new CustomerRegisterRequest());
        return modelAndView;
    }

}
