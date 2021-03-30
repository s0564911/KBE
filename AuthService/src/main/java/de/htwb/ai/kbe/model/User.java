package de.htwb.ai.kbe.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="userId")
    private String userId;

    @Column(name="firstName")
    private String firstName;

    @Column(name="lastName")
    private String lastName;

    @Column(name="password")
    private String password;

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() { return password; }
 
    public void setPassword(String email) {
        this.password = email;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName
                + ", password=" + password + "]";
    }
}
