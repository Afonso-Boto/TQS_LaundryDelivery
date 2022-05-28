package com.qourier.qourier_app.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

    private String email;
    private String password;

}
