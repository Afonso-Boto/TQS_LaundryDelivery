package com.qourier.qourier_app.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {

    @Id private String email;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_email")
    private Account account;

    private String servType;

    public Customer(Account account, String servType) {
        account.setState(AccountState.PENDING);
        account.setRole(AccountRole.CUSTOMER);
        this.account = account;
        this.servType = servType;
    }

    public Customer() {}
}
