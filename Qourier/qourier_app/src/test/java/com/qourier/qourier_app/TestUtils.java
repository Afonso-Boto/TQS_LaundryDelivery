package com.qourier.qourier_app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qourier.qourier_app.data.*;
import org.apache.commons.codec.digest.DigestUtils;

public class TestUtils {

    public static Gson gson = new GsonBuilder().create();

    public static class SampleAccountBuilder {

        private String email;
        private String password;
        private AccountState state;
        private AccountRole role;

        public SampleAccountBuilder(AccountRole role, String email) {
            this.role = role;
            this.email = email;
            password = "rider_passs";
            state = AccountState.SUSPENDED;
        }

        public SampleAccountBuilder password(String password) {
            this.password = password;
            return this;
        }

        public SampleAccountBuilder state(AccountState state) {
            this.state = state;
            return this;
        }

        public Rider buildRider() {
            if (!role.equals(AccountRole.RIDER))
                return null;

            return new Rider(buildAccount(), "123456789");
        }

        public Customer buildCustomer() {
            if (!role.equals(AccountRole.CUSTOMER))
                return null;

            return new Customer(buildAccount(), "Laundry");
        }

        public Admin buildAdmin() {
            if (!role.equals(AccountRole.ADMIN))
                return null;

            return new Admin(buildAccount());
        }

        private Account buildAccount() {
            Account account =
                    new Account("Sample Name", email, hashPassword(password));
            account.setRole(role);
            account.setState(state);
            return account;
        }

    }

    public static Rider createSampleRider(String riderAccountEmail, String riderAccountPassword) {
        Account riderAccount =
                new Account("riderz", riderAccountEmail, hashPassword(riderAccountPassword));
        riderAccount.setRole(AccountRole.RIDER);
        riderAccount.setState(AccountState.SUSPENDED);
        return new Rider(riderAccount, "123456789");
    }

    public static Rider createSampleRider(String riderAccountEmail) {
        return createSampleRider(riderAccountEmail, "rider_passs");
    }

    public static Customer createSampleCustomer(
            String customerAccountEmail, String customerAccountPassword) {
        Account customerAccount =
                new Account(
                        "customerss", customerAccountEmail, hashPassword(customerAccountPassword));
        customerAccount.setRole(AccountRole.CUSTOMER);
        customerAccount.setState(AccountState.ACTIVE);
        return new Customer(customerAccount, "Food");
    }

    public static Customer createSampleCustomer(String customerAccountEmail) {
        return createSampleCustomer(customerAccountEmail, "pass_custommer");
    }

    private static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}
