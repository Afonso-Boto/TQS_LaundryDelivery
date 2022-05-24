package tqs.project.laundryplatform.service;

import org.springframework.stereotype.Service;

public interface AuthenticationService {
    boolean register(String username, String fullName, String password, int phoneNumber, String email);
    boolean logIn(String username, String password);
}
