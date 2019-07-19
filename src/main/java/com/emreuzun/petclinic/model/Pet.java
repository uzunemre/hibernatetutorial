package com.emreuzun.petclinic.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_pet")
public class Pet {
    @Id
    private Long id;

    //@Basic(optional=false) nullable ile aynı işlevi yapar
    @Column(name="pet_name",nullable=false)
    private String name;

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
}