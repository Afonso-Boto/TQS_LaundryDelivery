package com.qourier.qourier_app.cucumber;

import com.qourier.qourier_app.data.AccountRole;
import com.qourier.qourier_app.data.AccountState;
import io.cucumber.java.ParameterType;

public class ParameterTypes {

    @ParameterType("\\w+")
    public String section(String section) {
        return section;
    }

    @ParameterType("not |")
    public boolean not(String not) {
        return !not.isEmpty();
    }

    @ParameterType("active|suspended")
    public AccountState accountsFilterType(String stateStr) {
        return AccountState.valueOf(stateStr.toUpperCase());
    }

    @ParameterType("pending|refused")
    public AccountState applicationsFilterType(String stateStr) {
        return AccountState.valueOf(stateStr.toUpperCase());
    }

    @ParameterType("activate|suspend")
    public String accountAction(String action) {
        return action;
    }

    @ParameterType("accept|refuse|reconsider")
    public String applicationAction(String action) {
        return action;
    }

    @ParameterType("Login|Main")
    public String page(String pageName) {
        return (pageName.equals("Main")) ? "" : "login";
    }

    @ParameterType("(pending|refused|active|suspended)")
    public AccountState accountState(String accountStateStr) {
        return AccountState.valueOf(accountStateStr.toUpperCase());
    }

    @ParameterType("Rider|Customer")
    public AccountRole accountRole(String accountRoleStr) {
        return AccountRole.valueOf(accountRoleStr.toUpperCase());
    }

    @ParameterType("\\w+")
    public String endpoint(String endpoint) {
        return endpoint;
    }
}
