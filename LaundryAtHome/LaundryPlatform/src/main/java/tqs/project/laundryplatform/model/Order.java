package tqs.project.laundryplatform.model;

import java.sql.Date;
import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    public Order(Long id, Date date, double totalPrice) {
        this.id = id;
        this.date = date;
        this.totalPrice = totalPrice;
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
        if (user != null ? !user.equals(order.user) : order.user != null) return false;
        if (orderType != null ? !orderType.equals(order.orderType) : order.orderType != null)
            return false;
        return complaint != null ? complaint.equals(order.complaint) : order.complaint == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (isCompleted ? 1 : 0);
        result = 31 * result + (deliveryLocation != null ? deliveryLocation.hashCode() : 0);
        result = 31 * result + (deliveryDate != null ? deliveryDate.hashCode() : 0);
        temp = Double.doubleToLongBits(totalPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (laundry != null ? laundry.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (orderType != null ? orderType.hashCode() : 0);
        result = 31 * result + (complaint != null ? complaint.hashCode() : 0);
        return result;
    }
}
