package com.qourier.qourier_app.data;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "admin")
public class Admin {

    @Id private String email;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_email")
    private Account account;

    public Admin(Account account) {
        account.setState(AccountState.ACTIVE);
        account.setRole(AccountRole.ADMIN);
        this.account = account;
    }

    public Admin() {}
}
