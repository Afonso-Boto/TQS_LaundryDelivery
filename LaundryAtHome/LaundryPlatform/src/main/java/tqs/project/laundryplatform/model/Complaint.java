package tqs.project.laundryplatform.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "complaint")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    Order order;

    public Complaint(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Complaint() {}

    public Complaint(String title, String description, Order order) {
        this.title = title;
        this.description = description;
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Complaint complaint = (Complaint) o;

        if (id != null ? !id.equals(complaint.id) : complaint.id != null) return false;
        if (title != null ? !title.equals(complaint.title) : complaint.title != null) return false;
        if (description != null
                ? !description.equals(complaint.description)
                : complaint.description != null) return false;
        return order != null ? order.equals(complaint.order) : complaint.order == null;
    }
}
