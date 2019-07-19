package com.emreuzun.petclinic;

import com.emreuzun.petclinic.config.JpaConfig;
import com.emreuzun.petclinic.model.Owner;
import com.emreuzun.petclinic.model.Pet;
import com.emreuzun.petclinic.model.Rating;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class JpaTest {

    @Test
    public void testJpaSetup() {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        tx.commit();
        entityManager.close();
        JpaConfig.getEntityManagerFactory().close();
    }

    @Test
    public void testWithoutTX() {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("kedicik");

        entityManager.persist(pet);
        //entityManager.flush();
        tx.commit();
        entityManager.close();
    }

    @Test
    public void testRating() {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        Owner owner = new Owner();
        owner.setFirstName("Emre");
        owner.setLastName("Uzun");
        owner.setRating(Rating.PREMIUM);

        entityManager.persist(owner);

        tx.commit();

        entityManager.close();
    }

}
