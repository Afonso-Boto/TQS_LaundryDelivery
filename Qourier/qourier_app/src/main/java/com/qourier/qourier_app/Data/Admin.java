package com.qourier.qourier_app.Data;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@Table(name = "admin")
public class Admin implements Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    static final int MAX_NAME_SIZE = 30;
    static final int MAX_PASSWORD_SIZE = 100;

    @Size(min = 3, max = MAX_NAME_SIZE)
    private String name;

    @NotNull
    @Email
    private String email;
    @Size(min = 8, max = MAX_PASSWORD_SIZE)
    private String password;
    private AccountStates accountState;
    Admin(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = DigestUtils.sha256Hex(password);
        this.accountState = AccountStates.ACTIVE;
    }

    public Admin() {}
}
