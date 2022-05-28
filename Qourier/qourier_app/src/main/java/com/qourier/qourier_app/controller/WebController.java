package com.qourier.qourier_app.controller;

import com.qourier.qourier_app.account.AccountManager;
import com.qourier.qourier_app.account.LoginRequest;
import com.qourier.qourier_app.data.Account;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Controller
public class WebController {

    private AccountManager accountManager;

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


}
