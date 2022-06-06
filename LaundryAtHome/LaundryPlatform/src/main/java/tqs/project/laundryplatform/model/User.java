package tqs.project.laundryplatform.model;

<<<<<<< HEAD
=======
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
>>>>>>> laundryathome_tqs-89_make-order
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private int phoneNumber;

    @OneToMany(mappedBy = "user")
    Set<Order> orders;

    public User(String username, String email, String password, String fullName, int phoneNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return phoneNumber == user.phoneNumber
                && id.equals(user.id)
                && username.equals(user.username)
                && email.equals(user.email)
                && password.equals(user.password)
                && fullName.equals(user.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, fullName, phoneNumber);
    }
}
