package com.qourier.qourier_app.account;

import com.qourier.qourier_app.account.login.LoginRequest;
import com.qourier.qourier_app.account.login.LoginResult;
import com.qourier.qourier_app.account.register.AdminRegisterRequest;
import com.qourier.qourier_app.account.register.CustomerRegisterRequest;
import com.qourier.qourier_app.account.register.RegisterRequest;
import com.qourier.qourier_app.account.register.RiderRegisterRequest;
import com.qourier.qourier_app.data.*;
import com.qourier.qourier_app.data.dto.AccountDTO;
import com.qourier.qourier_app.data.dto.CustomerDTO;
import com.qourier.qourier_app.data.dto.RiderDTO;
import com.qourier.qourier_app.repository.AccountRepository;
import com.qourier.qourier_app.repository.AdminRepository;
import com.qourier.qourier_app.repository.CustomerRepository;
import com.qourier.qourier_app.repository.RiderRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountManager {

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;
    private final RiderRepository riderRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public AccountManager(
            AccountRepository accountRepository,
            AdminRepository adminRepository,
            RiderRepository riderRepository,
            CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.adminRepository = adminRepository;
        this.riderRepository = riderRepository;
        this.customerRepository = customerRepository;
    }

    public LoginResult login(LoginRequest request) {
        Optional<Account> account = accountRepository.findById(request.getEmail());

        if (account.isEmpty()) return LoginResult.NON_EXISTENT_ACCOUNT;
        else if (!account.get().getPassword().equals(hashPassword(request.getPassword())))
            return LoginResult.WRONG_CREDENTIALS;
        return LoginResult.LOGGED_IN;
    }

    public boolean registerAdmin(AdminRegisterRequest request) {
        if (accountExists(request.getEmail())) return false;

        Account account = generateAccount(request);
        Admin admin = new Admin(account);

        adminRepository.save(admin);

        return true;
    }

    public boolean registerRider(RiderRegisterRequest request) {
        if (accountExists(request.getEmail())) return false;

        Account account = generateAccount(request);
        Rider rider = new Rider(account, request.getCitizenId());

        riderRepository.save(rider);

        return true;
    }

    public boolean registerCustomer(CustomerRegisterRequest request) {
        if (accountExists(request.getEmail())) return false;

        Account account = generateAccount(request);
        Customer customer = new Customer(account, request.getServType());

        customerRepository.save(customer);

        return true;
    }

    public boolean acceptApplication(String email) {
        return updateState(email, AccountState.PENDING, AccountState.ACTIVE);
    }

    public boolean refuseApplication(String email) {
        return updateState(email, AccountState.PENDING, AccountState.REFUSED);
    }

    public boolean reconsiderApplication(String email) {
        return updateState(email, AccountState.REFUSED, AccountState.PENDING);
    }

    public boolean suspendAccount(String email) {
        return updateState(email, AccountState.ACTIVE, AccountState.SUSPENDED);
    }

    public boolean activateAccount(String email) {
        return updateState(email, AccountState.SUSPENDED, AccountState.ACTIVE);
    }

    public RiderDTO getRiderAccount(String email) {
        return riderRepository
                .findById(email)
                .map(RiderDTO::fromEntity)
                .orElseThrow(() -> new AccountDoesNotExistException(email, AccountRole.RIDER));
    }

    public CustomerDTO getCustomerAccount(String email) {
        return customerRepository
                .findById(email)
                .map(CustomerDTO::fromEntity)
                .orElseThrow(() -> new AccountDoesNotExistException(email, AccountRole.CUSTOMER));
    }

    public AccountDTO getAccount(String email) {
        return accountRepository
                .findById(email)
                .map(AccountDTO::fromEntity)
                .orElseThrow(() -> new AccountDoesNotExistException(email));
    }

    public boolean accountExists(String email) {
        return accountRepository.existsById(email);
    }

    public void assignWork(String riderId, Long deliveryId) {
        Rider rider =
                riderRepository
                        .findById(riderId)
                        .orElseThrow(() -> new AccountDoesNotExistException(riderId));

        rider.setCurrentDelivery(deliveryId);
        riderRepository.save(rider);
    }

    public RiderDTOQueryResult queryRidersByState(
            Pageable pageable, Collection<AccountState> states) {
        RiderDTOQueryResult queryResult = new RiderDTOQueryResult();
        Page<RiderDTO> page =
                riderRepository.findByAccount_StateIn(states, pageable).map(RiderDTO::fromEntity);

        queryResult.setResult(page.toList());
        queryResult.setTotalPages(page.getTotalPages());

        return queryResult;
    }

    public CustomerDTOQueryResult queryCustomersByState(
            Pageable pageable, Collection<AccountState> states) {
        CustomerDTOQueryResult queryResult = new CustomerDTOQueryResult();
        Page<CustomerDTO> page =
                customerRepository
                        .findByAccount_StateIn(states, pageable)
                        .map(CustomerDTO::fromEntity);

        queryResult.setResult(page.toList());
        queryResult.setTotalPages(page.getTotalPages());

        return queryResult;
    }

    private boolean updateState(String email, AccountState startState, AccountState endState) {
        Account account =
                accountRepository
                        .findById(email)
                        .orElseThrow(() -> new AccountDoesNotExistException(email));

        if (!account.getState().equals(startState)) return false;

        account.setState(endState);
        accountRepository.save(account);

        return true;
    }

    private Account generateAccount(RegisterRequest registerRequest) {
        return new Account(
                registerRequest.getName(),
                registerRequest.getEmail(),
                hashPassword(registerRequest.getPassword()));
    }

    private String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    @Data
    public static class RiderDTOQueryResult {

        private List<RiderDTO> result;
        private int totalPages;
    }

    @Data
    public static class CustomerDTOQueryResult {

        private List<CustomerDTO> result;
        private int totalPages;
    }
}
