package com.qourier.qourier_app.Data;

import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "qourier_person")
public class Person {

    static final int MAX_NAME_SIZE = 30;
    static final int MAX_PASSWORD_SIZE = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(min = 3, max = MAX_NAME_SIZE)
    private String name;

    @Size(min = 8, max = MAX_PASSWORD_SIZE)
    private String password;

    @NotNull
    @Email
    private String email;

    private Roles role;
    private String CID;
    private String servType;

    private AccountStates accountState;

    public Person(String name, String email, String password, Integer role, String roleInf) {
        this.name = name;
        this.email = email;
        this.password = DigestUtils.sha256Hex(password);
        /* Set roles
            0 - rider
            2 - customer
         */
        switch (role) {
            case 0 -> {
                this.role = Roles.RIDER;
                this.CID = roleInf;
            }
            case 1 -> {
                this.role = Roles.CUSTOMER;
                this.servType = roleInf;
            }
            default -> {
                // Throw error?
            }
        }
        this.accountState = AccountStates.PENDING;
    }

    public Person(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = DigestUtils.sha256Hex(password);
        this.role = Roles.ADMIN;
        this.accountState = AccountStates.ACTIVE;
    }

    public Person() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = DigestUtils.sha256Hex(password);
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getServType() {
        return servType;
    }

    public void setServType(String servType) {
        this.servType = servType;
    }

    public AccountStates getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountStates accountState) {
        this.accountState = accountState;
    }
}

