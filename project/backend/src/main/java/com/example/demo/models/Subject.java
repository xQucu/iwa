package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @jakarta.persistence.ManyToMany(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinTable(name = "subject_users", joinColumns = @jakarta.persistence.JoinColumn(name = "subject_id"), inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "user_id"))
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "password", "roles" })
    private java.util.Set<User> enrolledUsers = new java.util.HashSet<>();

    public Subject() {
    }

    public Subject(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.Set<User> getEnrolledUsers() {
        return enrolledUsers;
    }

    public void setEnrolledUsers(java.util.Set<User> enrolledUsers) {
        this.enrolledUsers = enrolledUsers;
    }
}
