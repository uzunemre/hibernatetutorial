package com.emreuzun.petclinic.dao;

import com.emreuzun.petclinic.model.Owner;
import org.hibernate.SessionFactory;

public class OwnerDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void create(Owner owner) {
        sessionFactory.getCurrentSession().persist(owner);
    }
}