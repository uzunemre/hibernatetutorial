package com.emreuzun.petclinic;

import com.emreuzun.petclinic.config.HibernateConfig;
import com.emreuzun.petclinic.model.Pet;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

public class HibernateTest {

    @Test
    public void testHibernateSetup(){
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        tx.commit();
        session.close();
        HibernateConfig.getSessionFactory().close();
    }

    @Test
    public void testCreateEntity() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("kedicik");

        session.persist(pet);

        tx.commit();
        session.close();
    }


}
