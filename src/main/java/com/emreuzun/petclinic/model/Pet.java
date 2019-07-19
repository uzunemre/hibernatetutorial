package com.emreuzun.petclinic.model;

import javax.persistence.*;

@Entity
@Table(name="t_pet")
@SequenceGenerator(name="seqGen",sequenceName="pet_seq")
public class Pet extends BaseEntity {

    //@Basic(optional=false) nullable ile aynı işlevi yapar
    @Column(name="pet_name",nullable=false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}