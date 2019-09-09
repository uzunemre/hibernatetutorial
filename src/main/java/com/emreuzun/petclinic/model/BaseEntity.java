package com.emreuzun.petclinic.model;

import javax.persistence.*;

@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    // primary key generation
    private Long id;

    @Version
    @Column(name = "version", columnDefinition = "bigint default 0")
    private Long version;  // timestampde olabilir fakat cluster ortamlarda sıkıntı oluyor senkronizasyon sebebiyle

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



}