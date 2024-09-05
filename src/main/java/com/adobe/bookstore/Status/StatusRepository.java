package com.adobe.bookstore.status;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository; 
import java.util.Optional;

@Repository
public interface StatusRepository extends CrudRepository<Status, Long> {
    Optional<Status> findByName(OrderStatus name);
}