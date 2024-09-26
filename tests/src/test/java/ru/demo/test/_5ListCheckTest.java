package ru.demo.test;

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

import java.util.List;

import static io.qameta.allure.Allure.step;
import static ru.demo.util.config.ApiConfig.api;


@ExtendWith({
        PrepareDb.class,
        MongoConnect.class
})
public class _5ListCheckTest {

    final DbStep dbStep = new DbStep();

    //проверка списка

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
        step("проверки", () -> SoftAssertions.assertSoftly(s -> {
            s.assertThat(pet.getTags())
                    .hasSize(petDb.getTags().size());

            List<String> tags = pet.getTags().stream().map(Tag::getName).toList();
            List<String> tagsDb = petDb.getTags().stream().map(TagDto::getName).toList();

            s.assertThat(tags)
                    .containsSequence(tagsDb);
        }));
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
            s.assertThat(pet.getTags())
                    .hasSize(petDb.getTags().size())
                    .map(Tag::getName)
                    .containsSequence(petDb.getTags().stream().map(TagDto::getName).toList());
        }));
    }


}
