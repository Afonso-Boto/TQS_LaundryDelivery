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

    private static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    public static String hasher(String str) {
        return DigestUtils.sha256Hex(str);
    }
}
