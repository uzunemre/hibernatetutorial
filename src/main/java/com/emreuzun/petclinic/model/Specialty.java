package com.emreuzun.petclinic.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="t_specialty")
public class Specialty extends BaseEntity {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}