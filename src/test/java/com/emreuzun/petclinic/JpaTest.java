package com.emreuzun.petclinic;

import com.emreuzun.petclinic.config.JpaConfig;
import com.emreuzun.petclinic.model.Owner;
import com.emreuzun.petclinic.model.Pet;
import com.emreuzun.petclinic.model.Rating;
import com.emreuzun.petclinic.model.Visit;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class JpaTest {

    @Test
    public void testDelete() {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        Visit visit = entityManager.find(Visit.class, 3L);

        entityManager.clear();

        visit = entityManager.merge(visit);

        entityManager.remove(visit);

        tx.commit();
        entityManager.close();
    }

    @Test
    public void testHibernateApiAccess2() {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        Session session = (Session) entityManager.getDelegate();
        Session session2 = entityManager.unwrap(Session.class);

        System.out.println(session == session2);

        SessionFactory sf = session.getSessionFactory();

        Statistics statistics = sf.getStatistics();
        session.setHibernateFlushMode(FlushMode.MANUAL);
    }

    /**
     * jpa ile çalışırken hibernate apisine erişim
     */
    @Test
    public void testHibernateApiAccess() {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        SessionFactory sf = (SessionFactory)JpaConfig.getEntityManagerFactory();
        Session s = (Session) entityManager;
        Transaction hibTx = (Transaction)tx;

        Statistics statistics = sf.getStatistics();
        s.setHibernateFlushMode(FlushMode.MANUAL);
    }

    @Test
    public void testFindAndGetReference() {
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        Pet pet = entityManager.find(Pet.class, 1L);

        System.out.println("--- pet loaded ---");

        System.out.println(pet.getName());
        System.out.println(pet.getClass());

        Pet pet2 = entityManager.getReference(Pet.class, 2L); // proxy nesne

        System.out.println("--- pet 2 loaded ---");

        System.out.println(pet2.getName());
        System.out.println(pet2.getClass());
    }

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
