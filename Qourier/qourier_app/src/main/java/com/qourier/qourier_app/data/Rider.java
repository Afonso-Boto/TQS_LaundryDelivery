package com.qourier.qourier_app.data;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "rider")
public class Rider {

    @Id private String email;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_email")
    private Account account;

    private String citizenId;

    private Long currentDelivery;

    public Rider(Account account, String citizenId) {
        account.setState(AccountState.PENDING);
        account.setRole(AccountRole.RIDER);
        this.account = account;
        this.citizenId = citizenId;
        this.currentDelivery = null;
    }

    public Rider() {}
}
