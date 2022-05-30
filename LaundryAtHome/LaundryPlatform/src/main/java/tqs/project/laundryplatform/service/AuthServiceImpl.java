package tqs.project.laundryplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthenticationService {
    @Autowired
    UserRepository userRepository;
    @Override
    public boolean register(String username, String fullName, String password, int phoneNumber, String email) {
        if(userRepository.existsByUsername(username))
            return false;

        User user = new User(username, fullName, password, email, phoneNumber);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean logIn(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user.getPassword().equals(password))
                return true;
        }
        return false;
    }
}
