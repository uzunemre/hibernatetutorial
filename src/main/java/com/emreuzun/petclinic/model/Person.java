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
// Single Table; polymorphic sorgulara ve ilişkilere ihtiyacımız varsa ve performansta bizim için önemliyse tercih etmemiz gereken strateji ancak dikkat etmemiz gereken konu veri bütünlüğü(!) Bu stratejiyi kullanırken constraintleri kullanamıyoruz.
//Joined Table; eğer veri bütünlüğü bizim için performans, polymorphic sorgular ve ilişkilerden daha önemliyse seçmemiz gereken strateji olacaktır.
//Table-Per-Class; Polymorphic sorgulara ve ilişkilere ihtiyacımız yoksa bizim için daha uygun olan strateji bu olacaktır. Ayrıca bize constraintler tanımlayabilmemiz sayesinde veri bütünlüğünü oluşturma olanağı sağlar. Ayrıca bu stratejide polymorphic sorgu ve ilişkiler sağlayabiliriz ancak unutmamamız gereken; bu sorgular kompleks tablo yapımızdan dolayı bizim için çok kullanışsız ve performanssızdır.
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