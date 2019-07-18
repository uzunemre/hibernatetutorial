package com.emreuzun.petclinic;

import com.emreuzun.petclinic.config.HibernateConfig;
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



}
