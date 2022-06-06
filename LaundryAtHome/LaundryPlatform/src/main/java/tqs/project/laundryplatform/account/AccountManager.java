package tqs.project.laundryplatform.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.repository.UserRepository;

import java.util.Optional;

@Service
public class AccountManager {
    @Autowired private UserRepository userRepository;

    public LoginResult login(LoginRequest request) {
        Optional<User> user = userRepository.findByUsername(request.getUsername());

        if (user.isEmpty()) return LoginResult.NON_EXISTENT_ACCOUNT;
        else if (!user.get().getPassword().equals(request.getPassword())) {
            return LoginResult.WRONG_CREDENTIALS;
        }
        return LoginResult.LOGGED_IN;
    }

    public boolean register(RegisterRequest request) {
        if (accountExists(request.getUsername())) return false;

        User user = generateUser(request);
        userRepository.save(user);

        return true;
    }

    public boolean accountExists(String username) {
        return userRepository.existsByUsername(username);
    }

    private User generateUser(RegisterRequest registerRequest) {
        return new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getFullName(),
                registerRequest.getPhoneNumber());
    }
}
