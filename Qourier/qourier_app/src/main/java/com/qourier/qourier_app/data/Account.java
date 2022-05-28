package com.qourier.qourier_app.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@Table(name = "account")
public class Account {

    static final int MAX_NAME_SIZE = 30;
    static final int MAX_PASSWORD_SIZE = 100;

    @Id
    @Email
    private String email;

    @Size(min = 8, max = MAX_PASSWORD_SIZE)
    private String password;
    @Size(min = 3, max = MAX_NAME_SIZE)
    private String name;

    @NotNull
    private AccountState state;
    @NotNull
    private AccountRole role;

    public Account(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Account() {}

}
