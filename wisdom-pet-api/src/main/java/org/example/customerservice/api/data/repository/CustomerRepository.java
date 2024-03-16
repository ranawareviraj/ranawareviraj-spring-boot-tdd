package org.example.customerservice.api.data.repository;

import org.example.customerservice.api.data.entity.CustomerEntity;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface CustomerRepository extends CrudRepository<CustomerEntity, UUID> {
  CustomerEntity findByEmailAddress(String emailAddress);
}
