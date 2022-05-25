package tqs.project.laundryplatform.model;

import javax.persistence.*;

@Entity
@Table(name = "Laundry")
public class Laundry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    public Laundry(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Laundry(){

    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
