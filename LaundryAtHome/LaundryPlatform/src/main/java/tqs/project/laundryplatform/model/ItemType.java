package tqs.project.laundryplatform.model;

import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "itemType")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemType itemType = (ItemType) o;

        if (Double.compare(itemType.price, price) != 0) return false;
        if (id != null ? !id.equals(itemType.id) : itemType.id != null) return false;
        return name != null ? name.equals(itemType.name) : itemType.name == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
