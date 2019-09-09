package com.emreuzun.petclinic.dao;

import com.emreuzun.petclinic.model.Clinic;
import org.hibernate.SessionFactory;

public class ClinicDao {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Clinic findById(Long id) {
        return sessionFactory.getCurrentSession().get(Clinic.class, id);
    }

}