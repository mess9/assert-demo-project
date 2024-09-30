package ru.demo.test;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.demo.step.DbStep;
import ru.demo.tests.petstore.model.Pet;
import ru.demo.util.data.PetDto;
import ru.demo.util.extension.MongoConnect;
import ru.demo.util.extension.PrepareDb;

import static io.qameta.allure.Allure.step;
import static ru.demo.util.config.ApiConfig.api;


@ExtendWith({
        PrepareDb.class,
        MongoConnect.class
})
public class _3NotIntegerNumberTest {

    final DbStep dbStep = new DbStep();

    //проверка дробных чисел

    @Test
    void getPetsById() {
        //arrange
        var allPets = dbStep.getAllPets();
        PetDto petDb = allPets.getFirst();

        //act
        Pet pet = step("получить питомца api", () ->
                api.pet()
                        .getPetById()
                        .petIdPath(petDb.getId().toHexString())
                        .executeAs(r -> r));

        //assert
        step("проверки", () -> SoftAssertions.assertSoftly(s -> {
            s.assertThat(pet.getVolume())
                    .isEqualTo(petDb.getVolume());
        }));
    }


}
