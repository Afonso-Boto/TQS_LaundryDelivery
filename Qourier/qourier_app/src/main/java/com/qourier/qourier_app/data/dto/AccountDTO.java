package com.qourier.qourier_app.data.dto;

import com.qourier.qourier_app.data.Account;
import com.qourier.qourier_app.data.AccountRole;
import com.qourier.qourier_app.data.AccountState;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AccountDTO {

    private String email;
    private String password;
    private String name;
    private AccountState state;
    private AccountRole role;
    private LocalDateTime registrationTime;

    public static AccountDTO fromEntity(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setEmail(account.getEmail());
        dto.setPassword(account.getPassword());
        dto.setName(account.getName());
        dto.setState(account.getState());
        dto.setRole(account.getRole());
        dto.setRegistrationTime(account.getRegistrationTime());
        return dto;
    }
}
