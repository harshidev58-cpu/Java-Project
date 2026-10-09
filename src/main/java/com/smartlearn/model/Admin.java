package com.smartlearn.model;

/**
 * Represents a person who administers SmartLearn.
 * This class demonstrates inheritance by specializing the common {@link User} type.
 */
public class Admin extends User {

    public Admin(Long id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
