package com.qourier.qourier_app.account;

import com.qourier.qourier_app.data.*;
import com.qourier.qourier_app.repository.AccountRepository;
import com.qourier.qourier_app.repository.AdminRepository;
import com.qourier.qourier_app.repository.CustomerRepository;
import com.qourier.qourier_app.repository.RiderRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
;import java.util.Optional;

@Component
public class AccountManager {

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;
    private final RiderRepository riderRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public AccountManager(AccountRepository accountRepository, AdminRepository adminRepository, RiderRepository riderRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.adminRepository = adminRepository;
        this.riderRepository = riderRepository;
        this.customerRepository = customerRepository;
    }

    public Optional<Account> login(LoginRequest request) {
        Optional<Account> account = accountRepository.findById(request.getEmail());

        return account.isPresent() && account.get().getPassword().equals(hashPassword(request.getPassword()))
                ? account
                : Optional.empty();
    }

    public boolean registerAdmin(AdminRegisterRequest request) {
        if (accountRepository.existsById(request.getEmail()))
            return false;

        Account account = generateAccount(request);
        Admin admin = new Admin(account);

        adminRepository.save(admin);

        return true;
    }

    public boolean registerRider(RiderRegisterRequest request) {
        if (accountRepository.existsById(request.getEmail()))
            return false;

        Account account = generateAccount(request);
        Rider rider = new Rider(account, request.getCitizenId());

        riderRepository.save(rider);

        return true;
    }

    public boolean registerCustomer(CustomerRegisterRequest request) {
        if (accountRepository.existsById(request.getEmail()))
            return false;

        Account account = generateAccount(request);
        Customer customer = new Customer(account, request.getServType());

        customerRepository.save(customer);

        return true;
    }

    private Account generateAccount(RegisterRequest registerRequest) {
        return new Account(registerRequest.getEmail(), hashPassword(registerRequest.getPassword()), registerRequest.getName());
    }

    private String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

}
