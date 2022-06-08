package tqs.project.laundryplatform.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "OrderType")
public class OrderType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "base_price")
    private double basePrice;

    @OneToMany(mappedBy = "orderType")
    Set<Order> orders;

    public OrderType(String name, double basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    public OrderType() {}
}
