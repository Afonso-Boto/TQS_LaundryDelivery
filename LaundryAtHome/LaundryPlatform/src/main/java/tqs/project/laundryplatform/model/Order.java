package tqs.project.laundryplatform.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date")
    private Date date;

    @Column(name = "is_completed")
    private boolean isCompleted;

    @Column(name = "delivery_location")
    private String deliveryLocation;

    @OneToMany(mappedBy = "order")
    Set<Item> items;

    @ManyToOne
    @JoinColumn(name = "laundry_id")
    Laundry laundry;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "orderType_id")
    OrderType orderType;

    @OneToOne(mappedBy = "order")
    Complaint complaint;

    public Order(Date date, boolean isCompleted, String deliveryLocation) {
        this.date = date;
        this.isCompleted = isCompleted;
        this.deliveryLocation = deliveryLocation;
    }

    public Order(OrderType orderType, User user, Laundry laundry) {
        this.orderType = orderType;
        this.user = user;
        this.laundry = laundry;
    }

    public Order() {}
}
