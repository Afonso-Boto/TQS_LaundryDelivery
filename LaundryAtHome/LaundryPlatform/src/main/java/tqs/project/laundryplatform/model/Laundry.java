package tqs.project.laundryplatform.model;

<<<<<<< HEAD
=======
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
>>>>>>> laundryathome_tqs-89_make-order
import java.util.Set;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "Laundry")
public class Laundry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @OneToMany(mappedBy = "laundry")
    Set<Order> orders;

    public Laundry(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Laundry() {}
}
