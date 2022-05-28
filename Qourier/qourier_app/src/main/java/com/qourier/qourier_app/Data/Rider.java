package com.qourier.qourier_app.Data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "rider")
public class Rider extends Person{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String CID;
    private AccountStates accountState;
    Rider(String name, String email, String password, String cid){
        super(name, email, password);
        this.CID = cid;
        this.accountState = AccountStates.ACTIVE;
    }

    public Rider() {}
}
