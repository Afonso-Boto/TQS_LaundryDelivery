package com.qourier.qourier_app.account.register;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RiderRegisterRequest extends RegisterRequest {

    public RiderRegisterRequest(String email, String password, String name, String citizenId) {
        super(email, password, name);
        this.citizenId = citizenId;
    }

    private String citizenId;
}
