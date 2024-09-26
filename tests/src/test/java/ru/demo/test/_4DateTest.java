package ru.demo.test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.demo.step.DbStep;
import ru.demo.tests.petstore.model.Order;
import ru.demo.util.data.OrderDto;
import ru.demo.util.extension.MongoConnect;
import ru.demo.util.extension.PrepareDb;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.byLessThan;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static ru.demo.util.config.ApiConfig.api;


@ExtendWith({
        PrepareDb.class,
        MongoConnect.class
})
public class _4DateTest {

    final DbStep dbStep = new DbStep();

    //проверка дат

    @SneakyThrows
    @Test
    void getOrderById() {
        //arrange
        var allOrders = dbStep.getAllOrders();
        OrderDto firstOrder = allOrders.getFirst();
        firstOrder.setShipDate(OffsetDateTime.now());

        //act
        Order order = step("получить ордер api", () ->
                api.store().getOrderById()
                        .orderIdPath(firstOrder.getId().toHexString())
                        .executeAs(r -> r));

        //assert
        step("проверки", () -> assertSoftly(s -> {
//            s.assertThat(order.getShipDate())
//                    .isEqualTo(firstOrder.getShipDate());
            s.assertThat(order.getShipDate())
                    .isCloseTo(firstOrder.getShipDate(), byLessThan(10, ChronoUnit.DAYS));
        }));
    }


}
