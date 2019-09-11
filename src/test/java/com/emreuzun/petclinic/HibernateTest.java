package com.emreuzun.petclinic;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emreuzun.petclinic.dao.ClinicDao;
import com.emreuzun.petclinic.dao.OwnerDao;
import com.emreuzun.petclinic.model.*;
import com.emreuzun.petclinic.service.PetClinicService;
import org.hibernate.*;
import org.hibernate.query.NativeQuery;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.junit.Test;
import com.emreuzun.petclinic.config.HibernateConfig;
import com.emreuzun.petclinic.model.OwnerWithCompositePK.OwnerId;

public class HibernateTest {


    @Test
    public void testNativeSQL() {
        Session session = HibernateConfig.getSessionFactory().openSession();

        NativeQuery<Pet> nativeQuery = session.createNativeQuery("select * from t_pet p where p.pet_name like ?",Pet.class);

        nativeQuery.setParameter(1, "K%");

        List<Pet> resultList = nativeQuery.getResultList();

//		for(Object[] row:resultList) {
//			System.out.println(row[0] + " - " + row[1] + " - " + row[2] + " - " + row[3] + " - " + row[4] + " - " + row[5]);
//		}

        resultList.forEach(System.out::println);
    }

    /**
     *
     */
    @Test
    public void testNamedQuery() {
        Session session = HibernateConfig.getSessionFactory().openSession();

        Query<Pet> query = session.createNamedQuery("findPetsByName", Pet.class);

        query.setParameter("name", "K%");

        List<Pet> resultList = query.getResultList();

        resultList.forEach(System.out::println);
    }

    @Test
    public void testQueriesWithDTO() {
        Session session = HibernateConfig.getSessionFactory().openSession();

        String queryString = "select p.name as name,p.birthDate as birthDate from Pet p";

        List<Pet> resultList = session.createQuery(queryString).setResultTransformer(new AliasToBeanResultTransformer(Pet.class)).getResultList();

        resultList.forEach(System.out::println);
    }

    @Test
    public void testReportQueries() {
        Session session = HibernateConfig.getSessionFactory().openSession();

        String queryString = "select p.name,p.birthDate from Pet p";

        List<Object[]> resultList = session.createQuery(queryString).getResultList();

        for(Object[] row:resultList) {
            String name = (String)row[0];
            Date birthDate = (Date)row[1];

            System.out.println("Pet with name :" + name + " and bidrth date :" + birthDate);
        }
    }

    @Test
    public void testJoins() {
        Session session = HibernateConfig.getSessionFactory().openSession();

        String queryString = "select distinct o from Owner o left outer join o.pets p where p.name like :petName";

        Query<Owner> query = session.createQuery(queryString);

        query.setParameter("petName", "%");

//		List<Object[]> resultList = query.getResultList();
//
//		for(Object[] row:resultList) {
//			Owner o = (Owner)row[0];
//			Pet p = (Pet)row[1];
//			System.out.println("Owner :" + o);
//			System.out.println("Pet :" + p);
//		}

        List<Owner> resultList = query.getResultList();

        resultList.forEach(System.out::println);
    }

    @Test
    public void testHql() {
        Session session = HibernateConfig.getSessionFactory().openSession();

        String queryString = "select p from Pet p where p.name like ? or p.type.id = ?";

        Query<Pet> query = session.createQuery(queryString);

        query.setParameter(0, "K%");
        query.setParameter(1, 2L);

        List<Pet> resultList = query.getResultList();

        System.out.println("--- query executed ---");

        resultList.forEach(System.out::println);
    }

    /**
     * AuditInterceptor yazılarak HibernateConfigde konfigürasyon varsa(interceptor persist delete ve dirty işlemlerinde araya girecektir)
     * event listener aktifse AuditEntityListenerler çalışır
     */
    @Test
    public void testAuditInterceptor() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        session.beginTransaction();

        Pet pet = new Pet("kedicik 4",new Date());

        session.persist(pet);

        Pet pet2 = session.get(Pet.class, 1L);

        pet2.setBirthDate(new Date());

        session.delete(session.load(Pet.class, 8L));

        session.getTransaction().commit();
        session.close();
    }

    /**
     * session1 in yaptığı değişiklik en son commit edildiği için veritabanında o vardır.(base entityde version yoksa)
     * istenmeyen bir durum bu yüzden lock kullanılmalı(base entityde version eklenmeli)
     */
    @Test
    public void testConcurrency() {
        Session session1 = HibernateConfig.getSessionFactory().openSession();
        session1.beginTransaction();

        Session session2 = HibernateConfig.getSessionFactory().openSession();
        session2.beginTransaction();

        Pet pet1 = session1.get(Pet.class, 1L);

        Pet pet2 = session2.get(Pet.class, 1L);

        pet1.setOwner(session1.load(Owner.class, 8L));

        pet2.setType(session2.load(PetType.class, 6L));

        session2.getTransaction().commit();

        System.out.println("--- after session 2 commit ---");

        session1.getTransaction().commit();

        System.out.println("--- after session 1 commit ---");

        session1.close();
        session2.close();

    }

    @Test
    public void testLayeredArchitecture() {
        PetClinicService pcs = new PetClinicService();
        OwnerDao ownerDao = new OwnerDao();
        ClinicDao clinicDao = new ClinicDao();
        SessionFactory sf = HibernateConfig.getSessionFactory();

        ownerDao.setSessionFactory(sf);
        clinicDao.setSessionFactory(sf);

        pcs.setOwnerDao(ownerDao);
        pcs.setClinicDao(clinicDao);

        Transaction tx = sf.getCurrentSession().beginTransaction();

        Owner owner1 = new Owner();
        owner1.setFirstName("A");
        owner1.setLastName("B");

        Owner owner2 = new Owner();
        owner2.setFirstName("C");
        owner2.setLastName("D");
        try {
            pcs.addNewOwners(1L, owner1,owner2);
            tx.commit();
        } catch(Exception ex) {
            tx.rollback();
            throw ex;
        }
    }

    /**
     * aynı sessionları geri döndürür
     */
    @Test
    public void testContextualSession2() {
        Session session1 = HibernateConfig.getSessionFactory().getCurrentSession();
        Session session2 = HibernateConfig.getSessionFactory().getCurrentSession();

        System.out.println(session1 == session2); // true
        session1.beginTransaction().commit();
        session2 = HibernateConfig.getSessionFactory().getCurrentSession(); // session 2 kapatılmıştı tekrar açılır
        System.out.println(session1 == session2); // false
        Session session3 = HibernateConfig.getSessionFactory().openSession(); // yeni session açılır
        System.out.println(session2 == session3); // false
    }

    /**
     * sesiona herhangi bir yerden thread safe şekilde erişilmesini sağlar
     * open sessiona gerek kalmıyor. sessionyaksa session açıp döner. eğer session varsa onu geri döner
     * session close yazmaya gerek yok kendisi otomatik kapatır
     */
    @Test
    public void testContextualSession() {
        Session session = HibernateConfig.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        PetType petType = session.get(PetType.class, 1L);

        petType.setName("xxx");

        tx.commit();
        //session.close();
        System.out.println("--- after tx commit ---");
        System.out.println("Session open :" + session.isOpen());

    }

    @Test
    public void testCascade() {
        Session session = HibernateConfig.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        Owner owner = new Owner();
        owner.setFirstName("Emre");
        owner.setLastName("Uzun");

        Pet pet = new Pet();
        pet.setName("my puppie");

        owner.getPets().add(pet);
        //pet.setOwner(owner); pet ile owner arasında ilişki kaldırıldı. cascade olması bir işe yaramaz

        Visit visit = new Visit();
        visit.setVisitDescription("checkup");
        visit.setVisitDate(new Date());

        pet.getVisits().add(visit);

        session.persist(owner);

        tx.commit();
        session.close();
    }

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