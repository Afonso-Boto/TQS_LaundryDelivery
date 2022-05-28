package com.qourier.qourier_app.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "rider")
public class Rider {

    @Id
    private String email;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_email")
    private Account account;

    private String citizenId;

    public Rider(Account account, String citizenId){
        this.account = account;
        this.citizenId = citizenId;
    }

    public Rider() {}

}
