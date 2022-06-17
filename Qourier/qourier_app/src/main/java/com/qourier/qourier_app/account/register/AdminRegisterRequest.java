package com.qourier.qourier_app.account.register;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AdminRegisterRequest extends RegisterRequest {

    public AdminRegisterRequest(String email, String password, String name) {
        super(email, password, name);
    }
}
