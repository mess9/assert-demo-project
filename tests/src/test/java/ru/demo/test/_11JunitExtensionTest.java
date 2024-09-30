package ru.demo.test;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.demo.step.DbStep;
import ru.demo.tests.petstore.model.Pet;
import ru.demo.tests.petstore.model.Tag;
import ru.demo.util.data.PetDto;
import ru.demo.util.data.TagDto;
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
        s.assertThat(pet.getId())
                .as("ид питомца")
                .isEqualTo(petDbId);
        s.assertThat(pet.getName())
                .as("имя питомца должно быть - %s", petDb.getName())
                .isEqualTo(petDb.getName());
        s.assertThat(pet.getStatus().getValue())
                .as("доступность пета к покупке")
                .isEqualTo(petDb.getStatus().getValue());
        listCheckPet(s, pet, petDb);
    }


    @Step("проверка поля со списком")
    private static void listCheckPet(SoftAssertions s, Pet pet, PetDto petDb) {
        s.assertThat(pet.getTags())
                .hasSize(petDb.getTags().size())
                .map(Tag::getName)
                .containsSequence(petDb.getTags().stream().map(TagDto::getName).toList());
    }

}
