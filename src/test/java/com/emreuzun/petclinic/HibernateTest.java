package com.emreuzun.petclinic;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.emreuzun.petclinic.model.*;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.junit.Test;
import com.emreuzun.petclinic.config.HibernateConfig;
import com.emreuzun.petclinic.model.OwnerWithCompositePK.OwnerId;

public class HibernateTest {

    /**
     * refresh methodu veritabanınan gider ve veriyi çeker.(first level cache'e bakmaz)
     * @throws InterruptedException
     */
    @Test
    public void testRefresh() throws  InterruptedException {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        PetType pt = session.get(PetType.class, 1L);
        System.out.println("--- PetType loaded ---");
        pt.setName("xxx");

        System.out.println("--- waiting... ---");

        Thread.sleep(10000);

        session.refresh(pt);

        System.out.println("--- after refresh ---");

        System.out.println(pt.getName());
    }

    /**
     * veritabanında herhangi bir değişiklik olmaz. commit gerekli
     */
    @Test
    public void testFlushTxRelationship() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Owner owner = session.get(Owner.class, 7L);
        owner.setRating(null);

        session.persist(new Pet("my pet", new Date()));

        session.flush();
        System.out.println("--- after flush ---");
    }

    @Test
    public void testDelete() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Visit visit = session.get(Visit.class, 2L);

        session.clear(); // detached olsa bile silme işlemi gerçekleşir



        session.delete(visit);

        tx.commit();
        session.close();
    }


    /**
     * hibernate.enable_lazy_load_no_trans true set edilince session kapalı olsa bile kendisi session açar ve hata vermez
     */
    @Test
    public void testHibernateInitialize() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Pet pet = session.load(Pet.class, 1L);

        System.out.println("--- before initialize ---");

        //Hibernate.initialize(pet);
        //Hibernate.initialize(pet.getImagesByFilePath());

        session.close();

        System.out.println("--- session closed ---");

        System.out.println(pet.getName());
        System.out.println(pet.getImagesByFilePath().size());
    }

    /**
     * pet nesnesi detached yapıldıktan sonra  pet.getImagesByFilePath()  çağırılıdğında  LazyInitializationException alırız.
     * imagesByFilePath nesnesi proxy dir. pet nesnesi detached olduğu için initialize edilemiyor
     * session.update(pet) çalıştırıldığında nesne tekrar attached olur hata vermez
     */
    @Test
    public void testDetachedEntitiesAndLazy() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Pet pet = session.get(Pet.class, 1L);

        //session.close();
        //session.clear();
        session.evict(pet);

        System.out.println("--- after session evict ---");

        System.out.println("session open :" + session.isOpen());

        //session.update(pet);
        //session.lock(pet, LockMode.NONE); reaatach yapar. ismi yanıltıcı. updateden farkı değişikliği locklarken yapmaz. parametre olarak verilen pet nesnesi
        // snapshota eklenir lockdan sonra değişiklik varsa commit aşamasında veritabanına yansır

        session.merge(pet);

        System.out.println(pet.getClass());

        Map<String, Image> imagesByFilePath = pet.getImagesByFilePath();
        System.out.println(imagesByFilePath.getClass());
        System.out.println("image size :" + imagesByFilePath.size());

    }


    @Test
    public void testDetachedEntities() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        // pet nesnesi attached ediliyor
        Pet pet = session.get(Pet.class, 1L);

        tx.commit();
        session.close();
        // pet nesnesi detached ediliyor


        session = HibernateConfig.getSessionFactory().openSession();
        tx = session.beginTransaction();

        // pet nesnesi detached olduğu için yapılan değişikler veritabanınan yansıtılmayacaktır. reattach yapılırsa değişiklik yansır
        pet.setBirthDate(new Date());

        tx.commit();
        session.close();
    }

    @Test
    public void testUpdate() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Pet pet = session.get(Pet.class, 1L);

        pet.setBirthDate(null);
        pet.setType(session.load(PetType.class, 3L));

        // pet nesesinde işlem yapıldığı için commit aşamasında pet için update sorguları çalıştırılır
        tx.commit();
        session.close();
    }

    /**
     * yeni bir entity referansı döner. pet nesnesinde işlem yapıldıktan sonra bir daha işlem yapılmaması gerekiyor.
     * pet nesnesinde id değeri set edilmez merge kullanıldığı için
     */
    @Test
    public void testInsertWithMerge() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Pet pet = new Pet();
        pet.setName("kedicik 3");

        Pet pet2 = (Pet) session.merge(pet);

        System.out.println("--- after merge ---");

        pet.setBirthDate(new Date());

        System.out.println("pet id :" + pet.getId());
        System.out.println("pet 2 id :" + pet2.getId());
        System.out.println(pet == pet2);
        System.out.println("pet 2 birth date :" + pet2.getBirthDate());

        tx.commit();
        session.close();
    }


    @Test
    public void testSave() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Pet pet = new Pet();
        pet.setName("kedicik 2");

        Serializable pk = session.save(pet); // save methodu oluşturulan pk döndürür

        System.out.println("--- after save called ---");

        tx.commit();
        session.close();

        System.out.println(pk == pet.getId());
    }

    @Test
    public void testPersist() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Pet pet = new Pet();
        pet.setName("kedicik 1");

        session.persist(pet);
        // persist yapılınca id değeri set edilir
        System.out.println("--- after persist called ---");
        tx.commit();
        session.close();
    }

    @Test
    public void testStatistics() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        session.persist(new Pet("kedicik", new Date()));

        session.get(Pet.class, 1L);

        session.flush();

        session.createQuery("select p.name from Pet p").getResultList();

        Statistics statistics = HibernateConfig.getSessionFactory().getStatistics();

        EntityStatistics entityStatistics = statistics.getEntityStatistics("com.emreuzun.petclinic.model.Pet");
        QueryStatistics queryStatistics = statistics.getQueryStatistics("select p.name from Pet p");

        System.out.println("load count :" + entityStatistics.getLoadCount());
        System.out.println("insert count :" + entityStatistics.getInsertCount());

        System.out.println("query exec count :" + queryStatistics.getExecutionCount());
        System.out.println("query avg exec time :" + queryStatistics.getExecutionAvgTime());
    }

    @Test
    public void testLoad() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Pet pet = session.load(Pet.class, 1L);
        // pet kaydı için veritabanına bir select atmadı. bu nesne bir proxy nesne
        // proxy nesnede sadece primary key değer ivar. proxy nesneden name alanının çektiğimiz an select atar
        System.out.println("--- pet loaded ---");
        if (pet == null) {
            System.out.println("pet is null returning");
            return;
        }
        System.out.println(pet.getName());
        System.out.println(pet.getClass());

        Pet pet2 = session.get(Pet.class, 1L);
        System.out.println("--- pet loaded second time ---");
        System.out.println(pet2.getName());
        System.out.println(pet == pet2);
        // session loada niye ihtiyaç duyulur.
        // entity stateine ihtiyaç yoksa sadece referans ihtiyaçı varsa o zaman kullanılır.
    }

    // entity load ile persistence contexte yüklenmişse sonradan get ile çekildiğinde proxy nesneyi döndürür
    // first level cacheden dolayı
    @Test
    public void testGet() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Pet pet = session.get(Pet.class, 1L);
        System.out.println("--- pet loaded ---");
        if (pet == null) {
            System.out.println("pet is null returning");
            return;
        }
        System.out.println(pet.getName());
        System.out.println(pet.getClass());
        Pet pet2 = session.get(Pet.class, 1L);
        System.out.println("--- pet loaded second time ---");
        System.out.println(pet2.getName());
        System.out.println(pet == pet2);
        // aynı referans (first level cache)
    }

    @Test
    public void testHibernateSetup() {
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
        //pet.setId(1L);
        pet.setName("kedicik");

        session.persist(pet);

        tx.commit();
        session.close();
    }

    @Test
    public void testFieldLevelAccess() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Pet pet = new Pet("kedicik", new Date());
        pet.setId(1L);

        session.persist(pet);

        tx.commit();
        session.close();

        session = HibernateConfig.getSessionFactory().openSession();

        Pet pet2 = session.get(Pet.class, 1L);

        System.out.println(pet2);
    }

    @Test
    public void testWithoutTX() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();
        tx.begin();

        Pet pet = new Pet("kedicik", new Date());
        pet.setId(1L);

        session.persist(pet);

        //session.flush();
        tx.commit();

        session.close();

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
        OwnerWithCompositePK owner = new OwnerWithCompositePK();

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
        owner.setRating(Rating.PREMIUM);

        Address address = new Address();
        address.setStreet("İstanbul");
        address.setPhone("3122101036");

        owner.setAddress(address);

        session.persist(owner);

        tx.commit();
        session.close();
    }

    @Test
    public void testMappedBy() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();
        tx.begin();

        Owner owner = session.get(Owner.class, 1L);
        Pet pet = session.get(Pet.class, 101L);

        //owner.getPets().add(pet);

        //pet.setOwner(owner);

        //owner.getPets().remove(pet);

        pet.setOwner(null);

        //session.update(owner);
        //session.merge(owner);

        tx.commit();
        session.close();

    }

    @Test
    public void testParentChildAssoc() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();
        tx.begin();

        Pet pet = session.get(Pet.class, 1L);
        Visit visit = session.get(Visit.class, 101L);
        Image image = session.get(Image.class, 1001L);

        pet.getVisits().remove(visit);
        pet.getImagesByFilePath().remove("/myimage");

        tx.commit();
        session.close();
    }

    @Test
    public void testLazyEagerAccess() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();
        tx.begin();

        Pet pet = session.get(Pet.class, 101L);
        System.out.println("---pet loaded---");

        System.out.println("visits size :" + pet.getVisits().size());
        System.out.println("---");
        System.out.println("pet type name :" + pet.getType().getName());
        System.out.println(pet.getType().getClass());

        tx.commit();
        session.close();


    }

    @Test
    public void testOneToOneLazyProblem() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();
        tx.begin();

        Image image = session.get(Image.class, 1L);
        System.out.println("---image loaded---");
        System.out.println(new String(image.getImageContent().getContent()));
        System.out.println(image.getImageContent().getClass());
        tx.commit();
        session.close();
    }
}