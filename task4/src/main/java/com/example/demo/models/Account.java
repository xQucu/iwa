package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@JsonIgnoreProperties(ignoreUnknown = true, value = { "student", "id" })
@Entity
public class Account {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne
    private String accountName;

    // @JsonIgnore
    @OneToOne(mappedBy = "account")
    private Student student;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

}
