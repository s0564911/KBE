package de.htwb.ai.kbe.model;

import javax.persistence.*;

@Entity//(name = "user")
@Table(name="user")
public class User {

    @Override
    public String toString() {
        return "User [userId=" + userId + ", firstname=" + firstName + ", lastname=" + lastName + "]";
    }
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userId")
    private String userId;


    @Column(name="firstName") private String firstName;
    @Column(name="lastName") private String lastName;
    @Column(name="password") private String password;

    public User() {}

    private User(Builder builder) {
        this.userId = builder.userId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.password = builder.password;
    }

    public String getUserId() {
        return userId;
    }
    
    
    
    public void setUserId(String userid) {
        this.userId = userid;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastname) {
        this.lastName = lastname;
    }
    
    public String getPassword() { return password; }
    
 

    /**
     * Creates builder to build {@link User}.
     * @return created builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder to build {@link User}.
     */
    public static final class Builder {
//        private int id;
        private String userId;
        private String firstName;
        private String lastName;
        private String password;

        private Builder() {
        }

        public Builder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder withFirstname(String firstname) {
            this.firstName = firstname;
            return this;
        }

        public Builder withLastname(String lastname) {
            this.lastName = lastname;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
