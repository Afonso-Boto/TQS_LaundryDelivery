package com.qourier.qourier_app.controller;

import com.qourier.qourier_app.account.*;
import com.qourier.qourier_app.data.Account;
import com.qourier.qourier_app.data.Customer;
import com.qourier.qourier_app.data.Rider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
public class WebController {

    private final AccountManager accountManager;

    @Autowired
    public WebController(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest user) {
        Optional<Account> loggedInAccount = accountManager.login(user);
        if (loggedInAccount.isEmpty())
            return "login";

        Account account = loggedInAccount.get();

        // TODO redirection
        return switch (account.getRole()) {
            case ADMIN -> "index";
            case RIDER -> "index";
            case CUSTOMER -> "index";
        };
    }

    @PostMapping("register_customer")
    public String registerCustomer(@RequestBody CustomerRegisterRequest request) {
        Customer registeredCustomer;
        try {
            registeredCustomer = accountManager.registerCustomer(request);
        } catch (AccountAlreadyRegisteredException ex) {
            return "register_customer";
        }

        // TODO redirection
        return "index";
    }

    @PostMapping("register_rider")
    public String registerRider(@RequestBody RiderRegisterRequest request) {
        Rider registeredRider;
        try {
            registeredRider = accountManager.registerRider(request);
        } catch (AccountAlreadyRegisteredException ex) {
            return "register_rider";
        }

        // TODO redirection
        return "index";
    }

}
