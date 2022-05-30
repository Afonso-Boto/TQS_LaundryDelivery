package com.qourier.qourier_app.account;

import com.qourier.qourier_app.data.AccountRole;

public class AccountDoesNotExistException extends RuntimeException {

    public AccountDoesNotExistException(String email) {
        super(String.format("Account with email %s not found", email));
    }

    public AccountDoesNotExistException(String email, AccountRole role) {
        super(String.format("Account with email %s not found for role %s", email, role.name()));
    }
}
