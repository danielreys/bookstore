package com.adobe.bookstore.bookstock;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface BookStockRepository extends CrudRepository <BookStock, String> {
}
