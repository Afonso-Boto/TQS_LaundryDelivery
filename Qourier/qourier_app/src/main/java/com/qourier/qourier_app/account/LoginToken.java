package com.qourier.qourier_app.account;

import com.qourier.qourier_app.data.AccountRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginToken {

    private final String email;
    private final AccountRole role;
    private final LoginResult loginResult;

    public String generateCookie() {
        // TODO
        return "";
    }

}
