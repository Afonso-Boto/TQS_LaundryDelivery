package tqs.project.laundryplatform.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "ItemType")
public class ItemType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;

    @OneToMany(mappedBy = "itemType")
    Set<Item> items;

    public ItemType(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public ItemType() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
