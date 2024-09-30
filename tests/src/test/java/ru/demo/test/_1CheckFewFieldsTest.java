package ru.demo.test;

import org.assertj.core.api.SoftAssertions;
import org.bson.types.ObjectId;
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
public class _1CheckFewFieldsTest {

    final DbStep dbStep = new DbStep();

    //проверка полей софт ассертом

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
        step("проверки", () -> {
            SoftAssertions s = new SoftAssertions();

            s.assertThat(pet.getId())
                    .as("ид питомца")
                    .isEqualTo(petDbId);

            s.assertThat(pet.getName())
                    .as("имя питомца должно быть - %s", petDb.getName())
                    .isEqualTo(petDb.getName());

            assert pet.getStatus() != null;
            s.assertThat(pet.getStatus().getValue())
                    .as("доступность пета к покупке")
                    .isEqualTo(petDb.getStatus().getValue());

            s.assertAll();
        });
    }

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
        step("проверки", () -> SoftAssertions.assertSoftly(s -> {
            s.assertThat(pet.getId())
                    .as("ид питомца")
                    .isEqualTo(ObjectId.get().toHexString());

            s.assertThat(pet.getName())
                    .as("имя питомца должно быть - %s", allPets.getLast().getName())
                    .isEqualTo(allPets.getLast().getName());

            assert pet.getStatus() != null;
            s.assertThat(pet.getStatus().getValue())
                    .as("доступность пета к покупке")
                    .isEqualTo(allPets.getLast().getStatus().getValue());
        }));
    }

}
