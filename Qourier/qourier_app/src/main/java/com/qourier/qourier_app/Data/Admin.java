package com.qourier.qourier_app.Data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "admin")
public class Admin extends Person{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private AccountStates accountState;
    Admin(String name, String email, String password){
        super(name, email, password);
        this.accountState = AccountStates.ACTIVE;
    }

    public Admin() {}
}
