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
}
