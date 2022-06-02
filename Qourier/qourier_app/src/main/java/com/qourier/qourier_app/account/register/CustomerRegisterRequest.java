package com.qourier.qourier_app.account.register;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerRegisterRequest extends RegisterRequest {

    public CustomerRegisterRequest(String email, String password, String name, String servType) {
        super(email, password, name);
        this.servType = servType;
    }

    private String servType;
}
