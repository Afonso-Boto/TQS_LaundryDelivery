package com.qourier.qourier_app.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RiderRegisterRequest extends RegisterRequest {

    private String citizenId;

}
