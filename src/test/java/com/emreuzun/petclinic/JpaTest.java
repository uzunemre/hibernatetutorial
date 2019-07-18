package com.emreuzun.petclinic;

import com.emreuzun.petclinic.config.JpaConfig;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class JpaTest {

    @Test
    public void testJpaSetup(){
        EntityManager entityManager = JpaConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        tx.commit();
        entityManager.close();
        JpaConfig.getEntityManagerFactory().close();
    }

}
