package tqs.project.laundryplatform.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "number")
    private int number;

    @Column(name = "is_dark")
    private boolean isDark;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne
    @JoinColumn(name = "item_type_id")
    ItemType item_type;

    public Item(int number, boolean isDark, Order order, ItemType item_type) {
        this.number = number;
        this.isDark = isDark;
        this.order = order;
        this.item_type = item_type;
    }

    public Item() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (number != item.number) return false;
        if (isDark != item.isDark) return false;
        if (!id.equals(item.id)) return false;
        return item_type.equals(item.item_type);
    }
}
