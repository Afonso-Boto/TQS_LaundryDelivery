package tqs.project.laundryplatform.service;

import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthenticationService {
    @Override
    public boolean register(String username, String fullName, String password, int phoneNumber, String email) {
        return false;
    }

    @Override
    public boolean logIn(String username, String password) {
        return false;
    }
}
