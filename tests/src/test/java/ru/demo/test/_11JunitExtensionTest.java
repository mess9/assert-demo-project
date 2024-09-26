package ru.demo.test;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
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
        MongoConnect.class,
        SoftAssertionsExtension.class
})
public class _11JunitExtensionTest {

    final DbStep dbStep = new DbStep();

    @InjectSoftAssertions
    private SoftAssertions s;

    //проверка полей софт ассертом через расширение junit5


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
        });
    }

}
