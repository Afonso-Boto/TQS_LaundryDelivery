package com.qourier.qourier_app.Data;

public class PersonDTO {

    private String name;
    private String email;

    private String password;

    private Roles role;

    private String roleInf;

    private String CID;

    private String servType;

    public static PersonDTO fromPersonEntity(Person Person){
        String roleInf = (Person.getRole() == Roles.Rider) ? Person.getCID() : Person.getServType();
        return new PersonDTO(Person.getName(), Person.getEmail(), Person.getPassword(), Person.getRole(), roleInf);
    }

    public Person toPersonEntity(){
        return new Person(getName(), getEmail(), getPassword(), getRole(), getRoleInf());
    }

    public PersonDTO() {
    }

    public PersonDTO(String name, String email, String password, Roles role, String roleInf) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;

        // Set role info
        if (this.role == Roles.Rider){
            this.CID = roleInf;
        }else{
            this.servType = roleInf;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Roles getRole() { return role; }

    public void setRole(Roles role) { this.role = role; }

    public String getRoleInf() { return roleInf; }

    public void setRoleInf(String roleInf) { this.roleInf = roleInf; }

    public String getCID() { return CID; }

    public void setCID(String CID) { this.CID = CID; }

    public String getServType() { return servType; }

    public void setServType(String servType) { this.servType = servType; }
}
