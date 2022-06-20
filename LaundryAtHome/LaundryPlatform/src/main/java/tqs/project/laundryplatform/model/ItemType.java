package tqs.project.laundryplatform.model;

import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "item_type")
public class ItemType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;

    @OneToMany(mappedBy = "item_type")
    Set<Item> items;

    public ItemType(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public ItemType() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemType itemType = (ItemType) o;

        if (Double.compare(itemType.price, price) != 0) return false;
        if (id != null ? !id.equals(itemType.id) : itemType.id != null) return false;
        return name != null ? name.equals(itemType.name) : itemType.name == null;
    }
}
