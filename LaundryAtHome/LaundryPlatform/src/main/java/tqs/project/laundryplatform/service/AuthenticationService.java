package tqs.project.laundryplatform.service;

import org.springframework.stereotype.Service;
import tqs.project.laundryplatform.model.User;

public interface AuthenticationService {
    boolean register(User user);
    boolean logIn(String username, String password);
}
