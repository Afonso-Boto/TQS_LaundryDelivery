package com.qourier.qourier_app;

import com.qourier.qourier_app.data.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class TestUtils {

    private TestUtils() {}

    public static class SampleAccountBuilder {

        private String email;
        private String password;
        private AccountState state;

        public SampleAccountBuilder(String email) {
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
            return new Rider(buildAccount(AccountRole.RIDER), "123456789");
        }

        public Customer buildCustomer() {
            return new Customer(buildAccount(AccountRole.CUSTOMER), "Laundry");
        }

        public Admin buildAdmin() {
            return new Admin(buildAccount(AccountRole.ADMIN));
        }

        private Account buildAccount(AccountRole role) {
            Account account = new Account("Sample Name", email, hashPassword(password));
            account.setRole(role);
            account.setState(state);
            return account;
        }
    }

    public static String randomString() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String hasher(String str) {
        return DigestUtils.sha256Hex(str);
    }

    private static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
}
