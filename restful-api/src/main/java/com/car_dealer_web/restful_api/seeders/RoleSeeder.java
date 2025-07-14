package com.car_dealer_web.restful_api.seeders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.car_dealer_web.restful_api.enums.RoleStatus;
import com.car_dealer_web.restful_api.models.Role;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class RoleSeeder implements CommandLineRunner {
  private final EntityManager entityManager;
  private final static Logger LOG = LoggerFactory.getLogger(RoleSeeder.class);

  public RoleSeeder(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void run(String... args) throws Exception {
    loadRoleData();
  }

  public final void loadRoleData() {
    LOG.info("Running Role Seeder...");

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<Role> baseRoleRoot = countQuery.from(Role.class);

    countQuery.select(criteriaBuilder.countDistinct(baseRoleRoot));
    Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

    if (totalElements == 0) {
      Role initialData = new Role();
      initialData.setName("admin");
      initialData.setDescription("Limited access level, often focused on a subset of users or features.");
      initialData.setStatus(RoleStatus.ACTIVE.toString().toLowerCase());

      Role secondData = new Role();
      secondData.setName("super admin");
      secondData.setDescription("The highest level of access, typically with full control over the system.");
      secondData.setStatus(RoleStatus.ACTIVE.toString().toLowerCase());

      entityManager.persist(initialData);
      entityManager.persist(secondData);
      entityManager.flush();
    }
  }
}