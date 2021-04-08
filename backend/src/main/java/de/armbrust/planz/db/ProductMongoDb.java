package de.armbrust.planz.db;

import de.armbrust.planz.model.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductMongoDb extends PagingAndSortingRepository<Product, String> {

    List<Product> findAll();

    List<Product> findAllByAsin(String asin);

    Product findFirstByAsin(String asin);
}
