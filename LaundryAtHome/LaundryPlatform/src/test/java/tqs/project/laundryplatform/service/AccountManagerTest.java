package tqs.project.laundryplatform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tqs.project.laundryplatform.account.AccountManager;
import tqs.project.laundryplatform.account.LoginRequest;
import tqs.project.laundryplatform.account.LoginResult;
import tqs.project.laundryplatform.account.RegisterRequest;
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AccountManagerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountManager accountManager;

    @BeforeEach
    public void setUp() {
    }

    @Test
    void whenLoginExistent_thenEmptyResult() {
        String loginUsername = "test";
        String loginEmail = "test@ua.pt";
        String loginPassword = "123";
        LoginRequest loginRequest = new LoginRequest(loginUsername, loginPassword);
        User user = new User(loginUsername, loginEmail, loginPassword, "test", 123);

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(loginUsername)).thenReturn(Optional.of(user));

        LoginResult result = accountManager.login(loginRequest);
        assertThat(result).isEqualTo(LoginResult.LOGGED_IN);
    }

    @Test
    void whenLoginNonExistent_thenEmptyResult() {
        String loginUsername = "test";
        String loginEmail = "test@ua.pt";
        String loginPassword = "123";
        LoginRequest loginRequest = new LoginRequest(loginUsername, loginPassword);

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        LoginResult result = accountManager.login(loginRequest);
        assertThat(result).isEqualTo(LoginResult.NON_EXISTENT_ACCOUNT);
    }

    @Test
    void whenLoginWrongPassword_thenEmptyResult() {
        String loginUsername = "test";
        String loginEmail = "test@ua.pt";
        String loginPassword = "123";
        LoginRequest loginRequest = new LoginRequest(loginUsername, loginPassword);
        User user = new User(loginUsername, loginEmail, "1234", "test", 123);

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(loginUsername)).thenReturn(Optional.of(user));

        LoginResult result = accountManager.login(loginRequest);
        assertThat(result).isEqualTo(LoginResult.WRONG_CREDENTIALS);
    }

    @Test
    void whenRegisterWithoutConflict_thenSuccess() {
        String loginUsername = "test";
        String loginEmail = "test@ua.pt";
        String loginPassword = "123";
        RegisterRequest registerRequest =
                new RegisterRequest(loginUsername, loginEmail, loginPassword, "test", 123);

        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);

        assertThat(accountManager.register(registerRequest)).isTrue();
    }

    @Test
    void whenRegisterWithConflict_thenFail() {
        String loginUsername = "test";
        String loginEmail = "test@ua.pt";
        String loginPassword = "123";
        RegisterRequest registerRequest =
                new RegisterRequest(loginUsername, loginEmail, loginPassword, "test", 123);

        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThat(accountManager.register(registerRequest)).isFalse();
    }
}
