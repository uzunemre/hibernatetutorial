package com.emreuzun.petclinic;

import com.emreuzun.petclinic.config.HibernateConfig;
import com.emreuzun.petclinic.model.Address;
import com.emreuzun.petclinic.model.Owner;
import com.emreuzun.petclinic.model.OwnerWithCompositePk;
import com.emreuzun.petclinic.model.OwnerWithCompositePk.OwnerId;
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
        OwnerWithCompositePk owner = new OwnerWithCompositePk();

        OwnerId id = new OwnerId();
        id.setFirstName("Emre");
        id.setLastName("Uzun");

        owner.setId(id);

        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();
        tx.begin();

        session.persist(owner);

        tx.commit();
        session.close();
    }


    @Test
    public void testEmbeddable() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();
        tx.begin();

        Owner owner = new Owner();
        owner.setFirstName("Emre");
        owner.setLastName("Uzun");

        Address address = new Address();
        address.setStreet("İstanbul");
        address.setPhone("3122101036");

        owner.setAddress(address);

        session.persist(owner);

        tx.commit();
        session.close();
    }


}
