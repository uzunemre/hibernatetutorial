package com.emreuzun.petclinic.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="t_person")
@Inheritance(strategy=InheritanceType.JOINED)
// TABLE_PER_CLASS ve JOINED(ayrı tablolar oluşur performans düşürür) inheritance tipleri de var
public abstract class Person extends BaseEntity {

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @ElementCollection
    @CollectionTable(name="t_person_email",joinColumns=@JoinColumn(name="person_id"))
    private Set<Email> emails = new HashSet<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Email> getEmails() {
        return emails;
    }

    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }



}