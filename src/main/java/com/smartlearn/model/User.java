package com.smartlearn.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Holds details shared by every person who uses SmartLearn.
 * This abstract class demonstrates abstraction and inheritance: it shares common state
 * while requiring each concrete user type to identify its own role. As a JPA mapped superclass,
 * its identifier and shared columns are stored in each concrete user entity's table.
 */
@MappedSuperclass
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    protected User() {
        // JPA needs a no-argument constructor to create entity instances from database rows.
    }

    protected User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public abstract Role getRole();
}
