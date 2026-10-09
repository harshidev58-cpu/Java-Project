package com.smartlearn.model;

/**
 * Represents a person who teaches courses in SmartLearn.
 * This class demonstrates inheritance by specializing the common {@link User} type.
 */
public class Instructor extends User {

    public Instructor(Long id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public Role getRole() {
        return Role.INSTRUCTOR;
    }
}
