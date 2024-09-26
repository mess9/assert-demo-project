package de.openapi.petstore.service;

import de.openapi.petstore.db.OrderRepository;
import de.openapi.petstore.model.Order;
import io.micrometer.observation.annotation.Observed;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// tag::javadoc[]

/**
 * A service that provides orders.
 */
// end::javadoc[]
@Component
@Observed(name = "de.openapi.petstore.OrderService")
public class OrderService {

  private final OrderRepository orderRepository;

  public OrderService(@Autowired OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
    if (orderRepository.findAll().isEmpty()){
      Order order = createOrder(ObjectId.get(), ObjectId.get().toString(), 5, OffsetDateTime.now(), Order.StatusEnum.APPROVED, true);
      orderRepository.save(order);
    }

  }

  private static Order createOrder(final ObjectId id,
      final String petId,
      final int quantity,
      final OffsetDateTime shipDate,
      final Order.StatusEnum status,
      final boolean complete) {
    final Order order = new Order();
    order.setId(id.toString());
    order.setPetId(petId);
    order.setComplete(complete);
    order.setQuantity(quantity);
    order.setShipDate(shipDate);
    order.setStatus(status);
    return order;
  }

  public Map<String, Integer> getInventory() {
    final List<Order> orders = orderRepository.findAll();
    final Map<Order.StatusEnum, Integer> countByStatus = new EnumMap<>(Order.StatusEnum.class);

    for (final Order order : orders) {
      final Order.StatusEnum status = order.getStatus();
      if (countByStatus.containsKey(status)) {
        countByStatus.put(status, countByStatus.get(status) + order.getQuantity());
      } else {
        countByStatus.put(status, order.getQuantity());
      }
    }
    // Convert Enum to String for the keys
    return countByStatus.entrySet().stream().collect(
        Collectors.toMap(
            e -> e.getKey().getValue(),
            Entry::getValue));
  }

  public Order getOrderById(String orderId) {
    return orderRepository.findOrderById(orderId);
  }

  public Order placeOrder(Order order) {
    order.setId(ObjectId.get().toString());
    return this.orderRepository.save(order);
  }

  public List<Order> findOrdersByStatus(final String status) {
    return orderRepository.findByStatusIn(
        Arrays.stream(status.split(","))
            .map(String::toUpperCase)
            .map(String::trim)
            .collect(Collectors.toList())
    );
  }

  public void deleteOrderById(String orderId) {
    this.orderRepository.deleteById(orderId);
  }

}
