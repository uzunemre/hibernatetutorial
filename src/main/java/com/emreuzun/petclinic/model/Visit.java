package com.emreuzun.petclinic.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "t_visit")
public class Visit extends BaseEntity {

    @Column(name = "visit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date visitDate;

    @Lob
    @Column(name = "visit_description")
    private String visitDescription;

    @ManyToOne
    @JoinColumn(name = "pet_id", insertable = false, updatable = false)
    private Pet pet;

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public String getVisitDescription() {
        return visitDescription;
    }

    public void setVisitDescription(String visitDescription) {
        this.visitDescription = visitDescription;
    }
}