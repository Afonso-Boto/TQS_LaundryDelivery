package com.qourier.qourier_app.account;

import com.qourier.qourier_app.data.Account;
import com.qourier.qourier_app.data.AccountRole;
import com.qourier.qourier_app.repository.AccountRepository;
import com.qourier.qourier_app.repository.AdminRepository;
import com.qourier.qourier_app.repository.CustomerRepository;
import com.qourier.qourier_app.repository.RiderRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountManagerTest {

    @Mock private AccountRepository accountRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private RiderRepository riderRepository;
    @Mock private CustomerRepository customerRepository;

    @InjectMocks
    private AccountManager accountManager;

    @BeforeEach
    public void setUp() {}

    /* TODO FIX
    @Test
    void whenLoginExistentThenEmptyResult() {
        String loginEmail = "kos@dmail.com";
        String loginPassword = "root";
        LoginRequest loginRequest = new LoginRequest(loginEmail, loginPassword);
        Account account = new Account("The Name", loginEmail, hashPassword(loginPassword));
        account.setRole(AccountRole.ADMIN);

        when(accountRepository.findById(any())).thenReturn(Optional.empty());
        when(accountRepository.findById(loginEmail)).thenReturn(Optional.of(account));

        LoginToken token = accountManager.login(loginRequest);
        assertThat(token.getLoginResult()).isEqualTo(LoginResult.LOGGED_IN);
        assertThat(token.getEmail()).isEqualTo(loginEmail);
        assertThat(token.getRole()).isEqualTo(account.getRole());
    }

    @Test
    void whenLoginNonExistentThenEmptyResult() {
        String loginEmail = "kos@dmail.com";
        String loginPassword = "root";
        LoginRequest loginRequest = new LoginRequest(loginEmail, loginPassword);

        when(accountRepository.findById(any())).thenReturn(Optional.empty());

        LoginToken token = accountManager.login(loginRequest);
        assertThat(token.getLoginResult()).isEqualTo(LoginResult.NON_EXISTENT_ACCOUNT);
        assertThat(token.getEmail()).isNull();
        assertThat(token.getRole()).isNull();
    }

    @Test
    void whenLoginIncorrectThenEmptyResult() {
        String loginEmail = "kos@dmail.com";
        String loginPassword = "root";
        LoginRequest loginRequest = new LoginRequest(loginEmail, loginPassword);
        Account account = new Account("The Name", loginEmail, hashPassword("not-root"));

        when(accountRepository.findById(any())).thenReturn(Optional.empty());
        when(accountRepository.findById(loginEmail)).thenReturn(Optional.of(account));

        LoginToken token = accountManager.login(loginRequest);
        assertThat(token.getLoginResult()).isEqualTo(LoginResult.WRONG_CREDENTIALS);
        assertThat(token.getEmail()).isNull();
        assertThat(token.getRole()).isNull();
    }

    private String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

     */

}
