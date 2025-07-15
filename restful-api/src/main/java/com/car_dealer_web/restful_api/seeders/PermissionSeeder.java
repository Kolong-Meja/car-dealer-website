package com.car_dealer_web.restful_api.seeders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.car_dealer_web.restful_api.enums.PermissionStatus;
import com.car_dealer_web.restful_api.models.Permission;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class PermissionSeeder implements CommandLineRunner {
  private final EntityManager entityManager;
  private final static Logger LOG = LoggerFactory.getLogger(PermissionSeeder.class);

  public PermissionSeeder(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void run(String... args) throws Exception {
    loadPermissionData();
  }

  public final void loadPermissionData() {
    LOG.info("Running permission object seeder...");

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<Permission> basePermissionRoot = countQuery.from(Permission.class);

    countQuery.select(criteriaBuilder.countDistinct(basePermissionRoot));
    Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

    if (totalElements == 0) {
      Permission initialData = new Permission();
      initialData.setName("read_permission");
      initialData
          .setDescription("Permission level, focused on read data only.");
      initialData.setStatus(PermissionStatus.ACTIVE.toString().toLowerCase());

      Permission secondData = new Permission();
      secondData.setName("write_permission");
      secondData.setDescription("Permission level, focused on write data only.");
      secondData.setStatus(PermissionStatus.ACTIVE.toString().toLowerCase());

      Permission thirdData = new Permission();
      thirdData.setName("modify_permission");
      thirdData.setDescription("Permission level, focused on modify data only.");
      thirdData.setStatus(PermissionStatus.ACTIVE.toString().toLowerCase());

      Permission fourthData = new Permission();
      fourthData.setName("remove_permission");
      fourthData.setDescription("Permission level, focused on remove data only.");
      fourthData.setStatus(PermissionStatus.ACTIVE.toString().toLowerCase());

      entityManager.persist(initialData);
      entityManager.persist(secondData);
      entityManager.persist(thirdData);
      entityManager.flush();

      LOG.info("Seeding permission object successfully.");
    }
  }
}
