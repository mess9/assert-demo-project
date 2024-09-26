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
import static org.assertj.core.api.Assertions.assertThat;
import static ru.demo.util.config.ApiConfig.api;


@ExtendWith({
        PrepareDb.class,
        MongoConnect.class
})
public class _2CheckFewFieldsFromFieldTest {

    final DbStep dbStep = new DbStep();

    //проверка нескольких полей у поля

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
            s.assertThat(pet.getCategory()).satisfies(category -> {
                assertThat(category.getId())
                        .isEqualTo(petDb.getCategory().getId());
                assertThat(category.getName())
                        .isEqualTo(petDb.getCategory().getName());
            });
        }));
    }


}
