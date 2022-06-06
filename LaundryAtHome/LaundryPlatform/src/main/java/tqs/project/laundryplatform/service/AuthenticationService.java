package tqs.project.laundryplatform.service;

import tqs.project.laundryplatform.model.User;

public interface AuthenticationService {
    boolean register(User user);

    boolean logIn(String username, String password);
}
