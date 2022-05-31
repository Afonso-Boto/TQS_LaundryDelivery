package tqs.project.laundryplatform.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String fullName;
    private String username;
    private int phoneNumber;
    private String email;
    private String password;
}
