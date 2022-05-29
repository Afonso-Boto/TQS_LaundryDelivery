package com.qourier.qourier_app.account;

import lombok.Getter;

@Getter
public class NullLoginToken extends LoginToken {

    public NullLoginToken(LoginResult result) {
        super(null, null, result);
    }

    @Override
    public String generateCookie() {
        return "";
    }

}
