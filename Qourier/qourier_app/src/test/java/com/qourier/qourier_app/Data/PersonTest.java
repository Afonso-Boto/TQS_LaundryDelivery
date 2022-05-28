package com.qourier.qourier_app.Data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonTest {
    Person rider, customer, admin;

    @BeforeEach
    public void setUp() {
        rider = new Person("Name0", "email0@mail.com", "Password0", 0, "0123456789");
        customer = new Person("Name1", "email1@mail.com", "Password1", 1, "Laundry stuff");
        admin = new Person("Name2", "email2@mail.com", "Password2");
    }

    @Test
    void whenRegisteringUserCreatesRightRole() {
        // Test that roles where rightly assigned
        assertThat( rider.getRole() ).isEqualTo( Roles.RIDER );
        assertThat( customer.getRole() ).isEqualTo( Roles.CUSTOMER );
        assertThat( admin.getRole() ).isEqualTo( Roles.ADMIN );
    }

    @Test
    void whenRegisteringUserCreatesRightRoleSpecificInfo() {
        // Test that role specific info was rightly assigned
        // Rider
        assertThat( rider.getCID() ).isEqualTo( "0123456789" );
        assertThat( rider.getServType() == null ).isEqualTo(true);

        // Customer
        assertThat( customer.getServType() ).isEqualTo( "Laundry stuff" );
        assertThat( customer.getCID() == null ).isEqualTo(true);

        // Admin
        assertThat( admin.getCID() == null ).isEqualTo(true);
        assertThat( admin.getServType() == null ).isEqualTo(true);
    }
}
