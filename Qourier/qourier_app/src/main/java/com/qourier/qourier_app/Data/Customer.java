package com.qourier.qourier_app.Data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "customer")
public class Customer extends Person{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String servType;
    private AccountStates accountState;
    Customer(String name, String email, String password, String servType){
        super(name, email, password);
        this.servType = servType;
        this.accountState = AccountStates.ACTIVE;
    }

    public Customer() {}
}
