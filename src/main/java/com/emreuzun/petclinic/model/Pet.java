package com.emreuzun.petclinic.model;

import javax.persistence.*;

@Entity
@Table(name="t_pet")
public class Pet {

    @Id
    @GeneratedValue(generator="seqGen")
    @SequenceGenerator(name="seqGen",sequenceName="pet_seq")
    private Long id;
    // primary key generation

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