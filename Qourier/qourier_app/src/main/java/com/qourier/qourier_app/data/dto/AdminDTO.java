package com.qourier.qourier_app.data.dto;

import com.qourier.qourier_app.data.Admin;
import lombok.Data;

@Data
public class AdminDTO {

    private AccountDTO account;

    public static AdminDTO fromEntity(Admin admin) {
        AdminDTO dto = new AdminDTO();
        dto.setAccount(AccountDTO.fromEntity(admin.getAccount()));
        return dto;
    }
}
