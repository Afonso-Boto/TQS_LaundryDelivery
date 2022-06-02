package com.qourier.qourier_app.config;

import com.qourier.qourier_app.data.AccountRole;
import org.springframework.core.convert.converter.Converter;

public class StringToAccountRoleConverter implements Converter<String, AccountRole> {

    @Override
    public AccountRole convert(String source) {
        return AccountRole.valueOf(source.toUpperCase());
    }
}
