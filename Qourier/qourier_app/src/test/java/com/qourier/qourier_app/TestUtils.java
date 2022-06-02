package com.qourier.qourier_app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qourier.qourier_app.data.*;
import org.apache.commons.codec.digest.DigestUtils;

public class TestUtils {

    public static Gson gson = new GsonBuilder()
            .create();

    public static Rider createSampleRider(String riderAccountEmail, String riderAccountPassword) {
        Account riderAccount = new Account("riderz", riderAccountEmail, hashPassword(riderAccountPassword));
        riderAccount.setRole(AccountRole.RIDER);
        riderAccount.setState(AccountState.SUSPENDED);
        return new Rider(riderAccount, "123456789");
    }

    public static Rider createSampleRider(String riderAccountEmail) {
        return createSampleRider(riderAccountEmail, "rider_passs");
    }

    public static Customer createSampleCustomer(String customerAccountEmail, String customerAccountPassword) {
        Account customerAccount = new Account("customerss", customerAccountEmail, hashPassword(customerAccountPassword));
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
