package com.emreuzun.petclinic.model;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;


@TypeDef(name="ratingType",typeClass=RatingUserType.class)
@SecondaryTable(name="t_address",pkJoinColumns=@PrimaryKeyJoinColumn(name="owner_id"))
@Entity
@Table(name="t_owner")
public class Owner extends Person {


    @Type(type="ratingType")
    //@Convert(converter=RatingAttributeConverter.class)
    //@Enumerated(EnumType.ORDINAL)
    private Rating rating;

    @OneToMany(mappedBy="owner")
    private Set<Pet> pets = new HashSet<>();

    @Embedded
    private Address address;

    public Set<Pet> getPets() {
        return pets;
    }

    public void setPets(Set<Pet> pets) {
        this.pets = pets;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }


}