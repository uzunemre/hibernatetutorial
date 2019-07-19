package com.emreuzun.petclinic;

import com.emreuzun.petclinic.config.HibernateConfig;
import com.emreuzun.petclinic.model.Owner;
import com.emreuzun.petclinic.model.Owner.OwnerId;
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

    @Test
    public void testWithoutTX() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("kedicik");
        session.persist(pet);
        session.flush();
        session.close();
        // hibernate.allow_update_outside_transaction true olursa çalışıt
        // aktif bir transactiona ihtiyaç duymaz hibernate tarafında
        // tercih edilir bir yöntem değildir.
    }

    @Test
    public void testCheckNullability() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();
        tx.begin();

        Pet pet = new Pet();
        pet.setId(1L);

        session.persist(pet);

        tx.commit();
        session.close();
    }


    @Test
    public void testCompositePK() {
        Owner owner = new Owner();

        OwnerId id = new OwnerId();
        id.setFirstName("Kenan");
        id.setLastName("Sevindik");

        owner.setId(id);

        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();
        tx.begin();

        session.persist(owner);

        tx.commit();
        session.close();
    }


}
