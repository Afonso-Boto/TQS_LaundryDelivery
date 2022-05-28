package tqs.project.laundryplatform.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "Order")
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

    public Order(Date date, boolean isCompleted, String deliveryLocation) {
        this.date = date;
        this.isCompleted = isCompleted;
        this.deliveryLocation = deliveryLocation;
    }

    public Order() {

    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean is_completed) {
        this.isCompleted = is_completed;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String delivery_location) {
        this.deliveryLocation = delivery_location;
    }
}
