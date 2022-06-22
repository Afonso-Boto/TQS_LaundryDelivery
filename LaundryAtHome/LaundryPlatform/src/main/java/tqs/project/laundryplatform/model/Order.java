package tqs.project.laundryplatform.model;

import java.sql.Date;
import java.util.Set;
import javax.persistence.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
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

    @Column(name = "delivery_date")
    private Date deliveryDate;

    @Column(name = "total_price")
    private double totalPrice;

    @OneToMany(mappedBy = "order")
    Set<Item> items;

    @ManyToOne
    @JoinColumn(name = "laundry_id")
    Laundry laundry;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "ordertype_id")
    OrderType order_type;

    @OneToOne(mappedBy = "order")
    Complaint complaint;

    public Order(Date date, boolean isCompleted, String deliveryLocation) {
        this.date = date;
        this.isCompleted = isCompleted;
        this.deliveryLocation = deliveryLocation;
    }

    public Order(OrderType order_type, User user, Laundry laundry) {
        this.order_type = order_type;
        this.user = user;
        this.laundry = laundry;
    }

    public Order() {}

    public Order(Long id, Date date, double totalPrice) {
        this.id = id;
        this.date = date;
        this.totalPrice = totalPrice;
    }

    public Order(Long id, Date date, boolean isCompleted, double totalPrice, String deliveryLocation) {
        this.id = id;
        this.date = date;
        this.isCompleted = isCompleted;
        this.totalPrice = totalPrice;
        this.deliveryLocation = deliveryLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (isCompleted != order.isCompleted) return false;
        if (Double.compare(order.totalPrice, totalPrice) != 0) return false;
        if (id != null ? !id.equals(order.id) : order.id != null) return false;
        if (date != null ? !date.equals(order.date) : order.date != null) return false;
        if (deliveryLocation != null
                ? !deliveryLocation.equals(order.deliveryLocation)
                : order.deliveryLocation != null) return false;
        if (deliveryDate != null
                ? !deliveryDate.equals(order.deliveryDate)
                : order.deliveryDate != null) return false;
        if (laundry != null ? !laundry.equals(order.laundry) : order.laundry != null) return false;
        if (order_type != null ? !order_type.equals(order.order_type) : order.order_type != null)
            return false;
        return complaint != null ? complaint.equals(order.complaint) : order.complaint == null;
    }

    public Set<Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return id + "," + date + "," + isCompleted + "," + totalPrice + "," + deliveryLocation + ",";
    }
}
