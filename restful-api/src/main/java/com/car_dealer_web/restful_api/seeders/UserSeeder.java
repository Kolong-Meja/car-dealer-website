// package com.car_dealer_web.restful_api.seeders;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import com.car_dealer_web.restful_api.enums.UserAccountStatus;
// import com.car_dealer_web.restful_api.enums.UserActiveStatus;
// import com.car_dealer_web.restful_api.models.User;

// import io.github.thibaultmeyer.cuid.CUID;
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.criteria.CriteriaBuilder;
// import jakarta.persistence.criteria.CriteriaQuery;
// import jakarta.persistence.criteria.Root;
// import jakarta.transaction.Transactional;

// @Component
// @Transactional
// public class UserSeeder implements CommandLineRunner {
//   private final EntityManager entityManager;
//   private final static Logger LOG = LoggerFactory.getLogger(UserSeeder.class);

//   public UserSeeder(EntityManager entityManager) {
//     this.entityManager = entityManager;
//   }

//   @Override
//   public void run(String... args) throws Exception {
//     loadUserData();
//   }

//   public final void loadUserData() throws Exception {
//     LOG.info("Running User Seeder...");

//     try {
//       CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//       CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
//       Root<User> baseUserRoot = countQuery.from(User.class);

//       countQuery.select(criteriaBuilder.countDistinct(baseUserRoot));
//       Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

//       if (totalElements == 0) {
//         /**
//          * DATA PERTAMA.
//          */
//         final CUID initialCuid = CUID.randomCUID2(12);

//         User initialData = new User();
//         initialData.setId(initialCuid.toString());
//         initialData.setFullname("John Doe");
//         initialData.setBio("This is user example.");
//         initialData.setEmail("example01@gmail.com");
//         initialData.setPassword("Password123#");
//         initialData.setPhoneNumber("62899555687");
//         initialData.setAddress("Jl. Tengku Angkasa No. 37 Lebak Gede Coblong Bandung Jawa Barat");
//         initialData.setAccountStatus(UserAccountStatus.ACTIVE.toString().toLowerCase());
//         initialData.setActiveStatus(UserActiveStatus.OFFLINE.toString().toLowerCase());
//         initialData.setAvatarUrl("https://example.com/testing");

//         entityManager.persist(initialData);
//         entityManager.flush();
//       }
//     } catch (Exception error) {
//       LOG.error("Error during user seeding: {}", error.getMessage());

//       throw new Exception(error.getMessage());
//     }
//   }
// }
