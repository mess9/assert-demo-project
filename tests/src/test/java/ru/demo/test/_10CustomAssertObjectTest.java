package ru.demo.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.demo.step.DbStep;
import ru.demo.tests.petstore.model.Pet;
import ru.demo.util.assertions.PetAssert;
import ru.demo.util.assertions.PetSoftAssert;
import ru.demo.util.data.PetDto;
import ru.demo.util.extension.MongoConnect;
import ru.demo.util.extension.PrepareDb;

import static io.qameta.allure.Allure.step;
import static ru.demo.util.config.ApiConfig.api;


@ExtendWith({
        PrepareDb.class,
        MongoConnect.class
})
public class _10CustomAssertObjectTest {

    final DbStep dbStep = new DbStep();

    //кастомные ассерты


    @Test
    void getPetsById() {
        //arrange
        var allPets = dbStep.getAllPets();
        PetDto petDb = allPets.getFirst();
        String petDbId = petDb.getId().toHexString();

        //act
        Pet pet = step("получить питомца api", () ->
                api.pet()
                        .getPetById()
                        .petIdPath(petDbId)
                        .executeAs(r -> r));

        //assert
        step("проверки", () ->
                PetAssert.assertThat(pet).hasEqualToPetDto(petDb)
        );
    }

    //имплементировали кастомный софт ассерт
    @Test
    void getPetsById2() {
        //arrange
        var allPets = dbStep.getAllPets();
        PetDto petDb = allPets.getFirst();
        String petDbId = petDb.getId().toHexString();

        //act
        Pet pet = step("получить питомца api", () ->
                api.pet()
                        .getPetById()
                        .petIdPath(petDbId)
                        .executeAs(r -> r));

        //assert
        PetSoftAssert petSoftAssert = new PetSoftAssert();
        petSoftAssert.assertThat(pet)
                .isNotNull()
                .hasEqualToPetDto(petDb);
        petSoftAssert.assertAll();
    }

    @Test
    void getPetsById3() {
        //arrange
        var allPets = dbStep.getAllPets();
        PetDto petDb = allPets.getFirst();
        String petDbId = petDb.getId().toHexString();

        //act
        Pet pet = step("получить питомца api", () ->
                api.pet()
                        .getPetById()
                        .petIdPath(petDbId)
                        .executeAs(r -> r));

        //assert
        step("проверки", () -> {
            PetSoftAssert petSoftAssert = new PetSoftAssert();

            petSoftAssert.assertThat(pet)
                    .isNotNull()
                    .hasEqualToPetDto(petDb);

            petSoftAssert.assertAll();
        });

    }


}
