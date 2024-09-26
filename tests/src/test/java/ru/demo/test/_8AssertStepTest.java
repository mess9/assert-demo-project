package ru.demo.test;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
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
import static org.assertj.core.api.Assertions.assertThat;
import static ru.demo.util.config.ApiConfig.api;


@ExtendWith({
        PrepareDb.class,
        MongoConnect.class
})
public class _8AssertStepTest {

    final DbStep dbStep = new DbStep();

    //группировка проверок в шаги

    //включить allure-assertj в мавене

    @Test
    void getPetsById() {
        //arrange
        var allPets = dbStep.getAllPets();
        PetDto petDb = allPets.getFirst();
        String petDbId = petDb.getId().toHexString();
//        petDb.getTags().removeFirst();
//        petDb.setName("абырвалг");

        //act
        Pet pet = step("получить питомца api", () ->
                api.pet()
                        .getPetById()
                        .petIdPath(petDbId)
                        .executeAs(r -> r));

        //assert
        step("проверки", () -> SoftAssertions.assertSoftly(s -> {
            simpleCheckPet(s, pet, petDb);
            compositionCheckPet(s, pet, petDb);
            listCheckPet(s, pet, petDb);
        }));
    }

    @Step("проверка составного поля")
    private static void compositionCheckPet(SoftAssertions s, Pet pet, PetDto petDb) {
        s.assertThat(pet.getCategory()).satisfies(category -> {
            assertThat(category.getId())
                    .isEqualTo(petDb.getCategory().getId());
            assertThat(category.getName())
                    .isEqualTo(petDb.getCategory().getName());
        });
    }

    @Step("проверка простых полей")
    private static void simpleCheckPet(SoftAssertions s, Pet pet, PetDto petDb) {
        s.assertThat(pet.getId())
                .as("ид питомца")
                .isEqualTo(petDb.getId().toHexString());

        s.assertThat(pet.getName())
                .as("имя питомца должно быть - %s", petDb.getName())
                .isEqualTo(petDb.getName());

        assert pet.getStatus() != null;
        s.assertThat(pet.getStatus().getValue())
                .as("доступность пета к покупке")
                .isEqualTo(petDb.getStatus().getValue());
    }

    @Step("проверка полея со списком")
    private static void listCheckPet(SoftAssertions s, Pet pet, PetDto petDb) {
        s.assertThat(pet.getTags())
                .hasSize(petDb.getTags().size())
                .map(Tag::getName)
                .containsSequence(petDb.getTags().stream().map(TagDto::getName).toList());
    }
}
