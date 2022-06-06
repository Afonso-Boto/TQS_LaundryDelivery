package tqs.project.laundryplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tqs.project.laundryplatform.model.User;
import tqs.project.laundryplatform.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthenticationService {
    @Autowired UserRepository userRepository;

    @Override
    public boolean register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) return false;

        userRepository.save(user);
        return true;
    }

    @Override
    public boolean logIn(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user.getPassword().equals(password)) return true;
        }
        return false;
    }
}
