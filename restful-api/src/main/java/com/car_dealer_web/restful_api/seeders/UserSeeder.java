package com.car_dealer_web.restful_api.seeders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.car_dealer_web.restful_api.enums.UserAccountStatus;
import com.car_dealer_web.restful_api.enums.UserActiveStatus;
import com.car_dealer_web.restful_api.models.User;

import io.github.thibaultmeyer.cuid.CUID;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Component
public class UserSeeder {
  private final EntityManager entityManager;
  private final static Logger LOG = LoggerFactory.getLogger(UserSeeder.class);

  public UserSeeder(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Transactional
  public final void loadUserData() throws Exception {
    LOG.info("Running User Seeder...");

    try {
      CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
      Root<User> baseUserRoot = countQuery.from(User.class);

      countQuery.select(criteriaBuilder.countDistinct(baseUserRoot));
      Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

      if (totalElements == 0) {
        /**
         * DATA PERTAMA.
         */
        final CUID initialCuid = CUID.randomCUID2(12);

        User initialData = new User();
        initialData.setId(initialCuid.toString());
        initialData.setFullname("John Doe");
        initialData.setBio("This is user example.");
        initialData.setEmail("example01@gmail.com");
        initialData.setPassword("Password123#");
        initialData.setPhoneNumber("62899555687");
        initialData.setAddress("Jl. Tengku Angkasa No. 37 Lebak Gede Coblong Bandung Jawa Barat");
        initialData.setAccountStatus(UserAccountStatus.ACTIVE.toString().toLowerCase());
        initialData.setActiveStatus(UserActiveStatus.OFFLINE.toString().toLowerCase());
        initialData.setAvatarUrl("https://example.com/testing");

        /**
         * DATA KEDUA.
         */
        final CUID secondCuid = CUID.randomCUID2(12);

        User secondData = new User();
        secondData.setId(secondCuid.toString());
        secondData.setFullname("John Smith");
        secondData.setBio("This is user example.");
        secondData.setEmail("example02@gmail.com");
        secondData.setPassword("Password123#");
        secondData.setPhoneNumber("62896555605");
        secondData.setAddress("Jl. Bintara No. 8 Bintara Bekasi Barat Bekasi Jawa Barat");
        secondData.setAccountStatus(UserAccountStatus.ACTIVE.toString().toLowerCase());
        secondData.setActiveStatus(UserActiveStatus.OFFLINE.toString().toLowerCase());
        secondData.setAvatarUrl("https://example.com/testing");

        entityManager.persist(initialData);
        entityManager.persist(secondData);
        entityManager.flush();
      }
    } catch (Exception error) {
      LOG.error("Error during user seeding: {}", error.getMessage());

      throw new Exception(error.getMessage());
    }
  }
}
