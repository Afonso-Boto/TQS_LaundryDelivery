package tqs.project.laundryplatform.model;

import java.util.Set;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "order_type")
public class OrderType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "base_price")
    private double basePrice;

    @OneToMany(mappedBy = "order_type")
    Set<Order> orders;

    public OrderType(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    public OrderType() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderType type = (OrderType) o;

        if (Double.compare(type.basePrice, basePrice) != 0) return false;
        if (id != null ? !id.equals(type.id) : type.id != null) return false;
        return name != null ? name.equals(type.name) : type.name == null;
    }
}
