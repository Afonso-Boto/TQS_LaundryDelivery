package tqs.project.laundryplatform.model;

import javax.persistence.*;

@Entity
@Table(name = "Item")
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
    ItemType itemType;

    public Item(int number, boolean isDark) {
        this.number = number;
        this.isDark = isDark;
    }

    public Item(int number, boolean isDark, Order order, ItemType itemType) {
        this.number = number;
        this.isDark = isDark;
        this.order = order;
        this.itemType = itemType;
    }

    public Item() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isDark() {
        return isDark;
    }

    public void setDark(boolean dark) {
        isDark = dark;
    }
}
