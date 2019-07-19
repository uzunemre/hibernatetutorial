package com.emreuzun.petclinic.model;


import javax.persistence.*;

@SecondaryTable(name="t_address",pkJoinColumns=@PrimaryKeyJoinColumn(name="owner_id"))
@Entity
@Table(name="t_owner")
public class Owner extends Person {

    @Convert(converter=RatingAttributeConverter.class)
    //@Enumerated(EnumType.ORDINAL)
    private Rating rating;

    @Embedded
    private Address address;

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