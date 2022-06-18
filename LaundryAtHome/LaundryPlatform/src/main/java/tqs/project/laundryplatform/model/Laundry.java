package tqs.project.laundryplatform.model;

import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Laundry laundry = (Laundry) o;

        if (id != null ? !id.equals(laundry.id) : laundry.id != null) return false;
        if (name != null ? !name.equals(laundry.name) : laundry.name != null) return false;
        return location != null ? location.equals(laundry.location) : laundry.location == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (orders != null ? orders.hashCode() : 0);
        return result;
    }
}
