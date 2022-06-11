package com.qourier.qourier_app.account;

import static com.qourier.qourier_app.TestUtils.SampleAccountBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountManagerTest {

    @Mock private AccountRepository accountRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private RiderRepository riderRepository;
    @Mock private CustomerRepository customerRepository;

    @InjectMocks private AccountManager accountManager;

    @BeforeEach
    public void setUp() {}

    @Test
    void whenLoginExistent_thenEmptyResult() {
        String loginEmail = "kos@dmail.com";
        String loginPassword = "root";
        LoginRequest loginRequest = new LoginRequest(loginEmail, loginPassword);
        Account account = new Account("The Name", loginEmail, hashPassword(loginPassword));
        account.setRole(AccountRole.ADMIN);

        when(accountRepository.findById(any())).thenReturn(Optional.empty());
        when(accountRepository.findById(loginEmail)).thenReturn(Optional.of(account));

        LoginResult result = accountManager.login(loginRequest);
        assertThat(result).isEqualTo(LoginResult.LOGGED_IN);
    }

    @Test
    void whenLoginNonExistent_thenEmptyResult() {
        String loginEmail = "kos@dmail.com";
        String loginPassword = "root";
        LoginRequest loginRequest = new LoginRequest(loginEmail, loginPassword);

        when(accountRepository.findById(any())).thenReturn(Optional.empty());

        LoginResult result = accountManager.login(loginRequest);
        assertThat(result).isEqualTo(LoginResult.NON_EXISTENT_ACCOUNT);
    }

    @Test
    void whenLoginIncorrect_thenEmptyResult() {
        String loginEmail = "kos@dmail.com";
        String loginPassword = "root";
        LoginRequest loginRequest = new LoginRequest(loginEmail, loginPassword);
        Account account = new Account("The Name", loginEmail, hashPassword("not-root"));

        when(accountRepository.findById(any())).thenReturn(Optional.empty());
        when(accountRepository.findById(loginEmail)).thenReturn(Optional.of(account));

        LoginResult result = accountManager.login(loginRequest);
        assertThat(result).isEqualTo(LoginResult.WRONG_CREDENTIALS);
    }

    @Test
    void whenRegisterAccountWithoutConflict_thenReturnTrue() {
        AdminRegisterRequest adminRequest =
                new AdminRegisterRequest("gsaog@ngoas.com", "1234", "A Name");
        RiderRegisterRequest riderRequest =
                new RiderRegisterRequest(
                        "gnsioa@moab.com", "67843smA", "Another Name", "123456789");
        CustomerRegisterRequest customerRequest =
                new CustomerRegisterRequest(
                        "hashc@moab.com", "base003", "Yet Another Name", "Laundry");
        List<RegisterRequest> requests = List.of(adminRequest, riderRequest, customerRequest);

        for (RegisterRequest request : requests)
            when(accountRepository.existsById(request.getEmail())).thenReturn(false);

        assertThat(accountManager.registerAdmin(adminRequest)).isTrue();
        assertThat(accountManager.registerRider(riderRequest)).isTrue();
        assertThat(accountManager.registerCustomer(customerRequest)).isTrue();

        verify(adminRepository, times(1)).save(any());
        verify(riderRepository, times(1)).save(any());
        verify(customerRepository, times(1)).save(any());
    }

    @Test
    void whenRegisterAccountWithConflict_thenReturnFalse() {
        AdminRegisterRequest adminRequest =
                new AdminRegisterRequest("gsaog@ngoas.com", "1234", "A Name");
        RiderRegisterRequest riderRequest =
                new RiderRegisterRequest(
                        "gnsioa@moab.com", "67843smA", "Another Name", "123456789");
        CustomerRegisterRequest customerRequest =
                new CustomerRegisterRequest(
                        "hashc@moab.com", "base003", "Yet Another Name", "Laundry");
        List<RegisterRequest> requests = List.of(adminRequest, riderRequest, customerRequest);

        for (RegisterRequest request : requests)
            when(accountRepository.existsById(request.getEmail())).thenReturn(true);

        assertThat(accountManager.registerAdmin(adminRequest)).isFalse();
        assertThat(accountManager.registerRider(riderRequest)).isFalse();
        assertThat(accountManager.registerCustomer(customerRequest)).isFalse();

        verify(adminRepository, never()).save(any());
        verify(riderRepository, never()).save(any());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void whenAcceptApplication_thenReturnTrueAndAccountIsActive() {
        assertCorrectStateFlow(
                AccountState.PENDING, AccountState.ACTIVE, accountManager::acceptApplication);
    }

    @Test
    void whenAcceptApplicationInNonPending_thenReturnFalseAndNoOp() {
        assertIncorrectStateFlow(
                AccountState.PENDING, AccountState.ACTIVE, accountManager::acceptApplication);
    }

    @Test
    void whenRefuseApplication_thenReturnTrueAndAccountIsRefused() {
        assertCorrectStateFlow(
                AccountState.PENDING, AccountState.REFUSED, accountManager::refuseApplication);
    }

    @Test
    void whenRefuseApplicationInNonPending_thenReturnFalseAndNoOp() {
        assertIncorrectStateFlow(
                AccountState.PENDING, AccountState.REFUSED, accountManager::refuseApplication);
    }

    @Test
    void whenReconsiderApplication_thenReturnTrueAndAccountIsPending() {
        assertCorrectStateFlow(
                AccountState.REFUSED, AccountState.PENDING, accountManager::reconsiderApplication);
    }

    @Test
    void whenReconsiderApplicationInNonRefused_thenReturnFalseAndNoOp() {
        assertIncorrectStateFlow(
                AccountState.REFUSED, AccountState.PENDING, accountManager::reconsiderApplication);
    }

    @Test
    void whenSuspendAccount_thenReturnTrueAndAccountIsSuspended() {
        assertCorrectStateFlow(
                AccountState.ACTIVE, AccountState.SUSPENDED, accountManager::suspendAccount);
    }

    @Test
    void whenSuspendAccountInNonActive_thenReturnFalseAndNoOp() {
        assertIncorrectStateFlow(
                AccountState.ACTIVE, AccountState.SUSPENDED, accountManager::suspendAccount);
    }

    @Test
    void whenActivateAccount_thenReturnTrueAndAccountIsActive() {
        assertCorrectStateFlow(
                AccountState.SUSPENDED, AccountState.ACTIVE, accountManager::activateAccount);
    }

    @Test
    void whenActivateAccountInNonSuspended_thenReturnFalseAndNoOp() {
        assertIncorrectStateFlow(
                AccountState.SUSPENDED, AccountState.ACTIVE, accountManager::activateAccount);
    }

    @Test
    void givenAccountDoesNotExist_whenChangeState_thenThrow() {
        String accountEmail = "the.email@mail.com";

        when(accountRepository.findById(accountEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountManager.acceptApplication(accountEmail))
                .isInstanceOf(AccountDoesNotExistException.class);
        assertThatThrownBy(() -> accountManager.refuseApplication(accountEmail))
                .isInstanceOf(AccountDoesNotExistException.class);
        assertThatThrownBy(() -> accountManager.reconsiderApplication(accountEmail))
                .isInstanceOf(AccountDoesNotExistException.class);
        assertThatThrownBy(() -> accountManager.activateAccount(accountEmail))
                .isInstanceOf(AccountDoesNotExistException.class);
        assertThatThrownBy(() -> accountManager.suspendAccount(accountEmail))
                .isInstanceOf(AccountDoesNotExistException.class);
    }

    @Test
    void whenGetAccountDetails_thenGetCorrectDetails() {
        String riderAccountEmail = "the.email@mail.com";
        Rider rider = new SampleAccountBuilder(riderAccountEmail).buildRider();
        String customerAccountEmail = "email@email.com";
        Customer customer = new SampleAccountBuilder(customerAccountEmail).buildCustomer();

        when(accountRepository.findById(riderAccountEmail))
                .thenReturn(Optional.of(rider.getAccount()));
        when(accountRepository.findById(customerAccountEmail))
                .thenReturn(Optional.of(customer.getAccount()));
        when(riderRepository.findById(riderAccountEmail)).thenReturn(Optional.of(rider));
        when(customerRepository.findById(customerAccountEmail)).thenReturn(Optional.of(customer));

        assertThat(accountManager.getAccount(riderAccountEmail))
                .isEqualTo(AccountDTO.fromEntity(rider.getAccount()));
        assertThat(accountManager.getAccount(customerAccountEmail))
                .isEqualTo(AccountDTO.fromEntity(customer.getAccount()));
        assertThat(accountManager.getRiderAccount(riderAccountEmail))
                .isEqualTo(RiderDTO.fromEntity(rider));
        assertThat(accountManager.getCustomerAccount(customerAccountEmail))
                .isEqualTo(CustomerDTO.fromEntity(customer));
    }

    @Test
    void givenAccountDoesNotExist_whenGetAccountDetails_thenThrow() {
        String accountEmail = "the.email@mail.com";

        when(accountRepository.findById(accountEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountManager.getAccount(accountEmail))
                .isInstanceOf(AccountDoesNotExistException.class);
    }

    @Test
    void givenRiderAndCustomerExist_whenGetWrongAccountEmail_thenThrow() {
        String riderAccountEmail = "ngoisa@gioa.com";
        Rider rider = new SampleAccountBuilder(riderAccountEmail).buildRider();
        String customerAccountEmail = "h9agija@hjd90bs.org";
        Customer customer = new SampleAccountBuilder(customerAccountEmail).buildCustomer();

        when(riderRepository.findById(customer.getAccount().getEmail()))
                .thenReturn(Optional.empty());
        when(customerRepository.findById(rider.getAccount().getEmail()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountManager.getRiderAccount(customerAccountEmail))
                .isInstanceOf(AccountDoesNotExistException.class);
        assertThatThrownBy(() -> accountManager.getCustomerAccount(riderAccountEmail))
                .isInstanceOf(AccountDoesNotExistException.class);
    }

    private String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    private void assertCorrectStateFlow(
            AccountState startState,
            AccountState endState,
            Function<String, Boolean> accountOperation) {
        String accountEmail = "email@email.com";
        Account account = new Account("name", accountEmail, "password");
        account.setState(startState);
        account = Mockito.spy(account);

        when(accountRepository.findById(accountEmail)).thenReturn(Optional.of(account));

        assertThat(accountOperation.apply(accountEmail)).isTrue();
        verify(account, times(1)).setState(any());
        verify(account, times(1)).setState(endState);
        verify(accountRepository, times(1)).save(account);
    }

    private void assertIncorrectStateFlow(
            AccountState startState,
            AccountState endState,
            Function<String, Boolean> accountOperation) {
        List<AccountState> prohibitedStates =
                Arrays.stream(AccountState.values())
                        .filter(accountState -> !accountState.equals(startState))
                        .toList();

        List<Account> accounts = new ArrayList<>();
        for (AccountState state : prohibitedStates) {
            Account account = new Account("name", "email@email.com" + state.name(), "password");
            account.setState(state);
            account = Mockito.spy(account);
            accounts.add(account);
        }

        for (Account account : accounts)
            when(accountRepository.findById(account.getEmail())).thenReturn(Optional.of(account));

        for (Account account : accounts) {
            assertThat(accountOperation.apply(account.getEmail())).isFalse();
            verify(account, never()).setState(endState);
            verify(accountRepository, never()).save(account);
        }
    }
}
