package com.qourier.qourier_app.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {
    Rider rider;
    Customer customer;
    Admin admin;

    @BeforeEach
    public void setUp() {
        rider = new Rider(new Account("Name0", "email0@mail.com", "Password0"), "0123456789");
        customer =
                new Customer(new Account("Name1", "email1@mail.com", "Password1"), "Laundry stuff");
        admin = new Admin(new Account("Name2", "email2@mail.com", "Password2"));
    }

    @Test
    void whenRegisteringUserCreatesRightRoleSpecificInfo() {
        // Test that role specific info was rightly assigned
        // Rider
        assertThat(rider.getCitizenId()).isEqualTo("0123456789");

        // Customer
        assertThat(customer.getServType()).isEqualTo("Laundry stuff");
    }

    @Test
    void whenRegisteringUserAssignsRightAccountState() {
        // Test that roles where rightly assigned
        assertThat(rider.getAccount().getState()).isEqualTo(AccountState.PENDING);
        assertThat(customer.getAccount().getState()).isEqualTo(AccountState.PENDING);
        assertThat(admin.getAccount().getState()).isEqualTo(AccountState.ACTIVE);
    }
}
