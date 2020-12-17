package de.htwb.ai.kbe.model;

import javax.persistence.*;

@Entity//(name = "user")
@Table(name="user") //(name="users") //users for working Postgres
public class User {

    @Override
    public String toString() {
        return "User [userId=" + userid + ", firstname=" + firstname + ", lastname=" + lastname + "]";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userid")
    private String userid;


    @Column(name="firstname") private String firstname;
    @Column(name="lastname") private String lastname;
    @Column(name="password") private String password;

    public User() {}

    private User(Builder builder) {
        this.userid = builder.userid;
        this.firstname = builder.firstname;
        this.lastname = builder.lastname;
        this.password = builder.password;
    }

    public String getUserid() {
        return userid;
    }
    
    public void setUserid(String userid) {
        this.userid = userid;
    }
    
    public String getFirstname() {
        return firstname;
    }
    
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    public String getLastname() {
        return lastname;
    }
    
    public void setLastname(String lastname) {
        this.lastname = lastname;
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
        private String userid;
        private String firstname;
        private String lastname;
        private String password;

        private Builder() {
        }

        public Builder withUserId(String userId) {
            this.userid = userId;
            return this;
        }

        public Builder withFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public Builder withLastname(String lastname) {
            this.lastname = lastname;
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
