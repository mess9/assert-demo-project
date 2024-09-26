package de.openapi.petstore.db;

import de.openapi.petstore.model.Order;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {

  void deleteById(String id);

  Order findOrderById(String id);

  List<Order> findByStatusIn(List<String> status);
}
