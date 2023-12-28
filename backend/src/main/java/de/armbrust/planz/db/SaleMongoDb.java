package de.armbrust.planz.db;

import de.armbrust.planz.model.Sale;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SaleMongoDb extends PagingAndSortingRepository<Sale, String> {

    List<Sale> findAll();

    List<Sale> findAllByAsin(String asin);

}
