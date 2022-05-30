package com.qourier.qourier_app.data.dto;

import com.qourier.qourier_app.data.Customer;
import lombok.Data;

@Data
public class CustomerDTO {

    private AccountDTO account;
    private String servType;

    public static CustomerDTO fromEntity(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setAccount(AccountDTO.fromEntity(customer.getAccount()));
        dto.setServType(customer.getServType());
        return dto;
    }

}
