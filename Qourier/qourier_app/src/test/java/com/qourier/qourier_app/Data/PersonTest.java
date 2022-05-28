package com.qourier.qourier_app.Data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonTest {
    Rider rider;
    Customer customer;
    Admin admin;

    @BeforeEach
    public void setUp() {
        rider = new Rider("Name0", "email0@mail.com", "Password0", "0123456789");
        customer = new Customer("Name1", "email1@mail.com", "Password1", "Laundry stuff");
        admin = new Admin("Name2", "email2@mail.com", "Password2");
    }

    @Test
    void whenRegisteringUserCreatesRightRoleSpecificInfo() {
        // Test that role specific info was rightly assigned
        // Rider
        assertThat( rider.getCID() ).isEqualTo( "0123456789" );

        // Customer
        assertThat( customer.getServType()).isEqualTo( "Laundry stuff" );
    }

    @Test
    void whenRegisteringUserAssignsRightAccountState() {
        // Test that roles where rightly assigned
        assertThat( rider.getAccountState() ).isEqualTo( AccountStates.PENDING );
        assertThat( customer.getAccountState() ).isEqualTo( AccountStates.PENDING );
        assertThat( admin.getAccountState() ).isEqualTo( AccountStates.ACTIVE );
    }
}
