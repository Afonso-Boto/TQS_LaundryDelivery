package com.qourier.qourier_app.data;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "customer")
public class Customer {

    @Id private String email;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_email")
    private Account account;

    private String servType;

    public Customer(Account account, String servType) {
        account.setRole(AccountRole.CUSTOMER);
        this.account = account;
        this.servType = servType;
    }

    public Customer() {}
}
