package com.car_dealer_web.restful_api.seeders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.car_dealer_web.restful_api.enums.RoleStatus;
import com.car_dealer_web.restful_api.models.Role;

import io.github.thibaultmeyer.cuid.CUID;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Component
public class RoleSeeder {
  private final EntityManager entityManager;
  private final static Logger LOG = LoggerFactory.getLogger(RoleSeeder.class);

  public RoleSeeder(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Transactional
  public final void loadRoleData() throws Exception {
    LOG.info("Running Role Seeder...");

    try {
      CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
      CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
      Root<Role> baseRoleoot = countQuery.from(Role.class);

      countQuery.select(criteriaBuilder.countDistinct(baseRoleoot));
      Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

      if (totalElements == 0) {
        /**
         * DATA PERTAMA.
         */
        final CUID initialCuid = CUID.randomCUID2(12);

        Role initialData = new Role();
        initialData.setId(initialCuid.toString());
        initialData.setName("admin");
        initialData.setDescription("Admin role.");
        initialData.setStatus(RoleStatus.ACTIVE.toString().toLowerCase());

        /**
         * DATA KEDUA.
         */
        final CUID secondCuid = CUID.randomCUID2(12);

        Role secondData = new Role();
        secondData.setId(secondCuid.toString());
        secondData.setName("super admin");
        initialData.setDescription("Super Admin role.");
        secondData.setStatus(RoleStatus.ACTIVE.toString().toLowerCase());

        entityManager.persist(initialData);
        entityManager.persist(secondData);
        entityManager.flush();
      }
    } catch (Exception error) {
      LOG.error("Error during role seeding: {}", error.getMessage());

      throw new Exception(error.getMessage());
    }
  }
}