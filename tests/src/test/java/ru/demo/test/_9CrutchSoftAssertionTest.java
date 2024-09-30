package ru.demo.test;

import io.qameta.allure.Step;
import org.assertj.core.api.AbstractSoftAssertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.SoftAssertionsProvider;
import org.assertj.core.api.StandardSoftAssertionsProvider;
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
import java.util.function.Consumer;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.demo.util.config.ApiConfig.api;


@ExtendWith({
        PrepareDb.class,
        MongoConnect.class
})
public class _9CrutchSoftAssertionTest {

    final DbStep dbStep = new DbStep();

    //проверка исключений


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

        pet.setCategory(null);     //создаём ошибки
        pet.setName("неправильное имя");

        //assert
        step("проверки", () -> CrutchSoftAssertions.assertSoftly(s -> {
            simpleCheckPet(s, pet, petDb);
            listCheckPet(s, pet, petDb);

            catchAssertion(s, pet, petDb);

        }));
    }

    @Step("проверка с исключением")
    private static void catchAssertion(SoftAssertions s, Pet pet, PetDto petDb) {
        try {
            s.assertThat(pet.getCategory()).satisfies(category -> {
                assertThat(category.getId())
                        .isEqualTo(petDb.getCategory().getId());
                assertThat(category.getName())
                        .isEqualTo(petDb.getCategory().getName());
                s.assertThat(true).isTrue();
            });
        } catch (Exception e) {
            s.assertThat(false).overridingErrorMessage("bla bla bla custom message")
                    .isTrue();
        }
    }

    @Step("проверка простых полей")
    private static void simpleCheckPet(SoftAssertions s, Pet pet, PetDto petDb) {
        s.assertThat(pet.getId())
                .as("ид питомца")
                .isEqualTo(petDb.getId().toHexString());

        s.assertThat(pet.getName())
                .as("имя питомца должно быть - %s", petDb.getName())
                .isEqualTo(petDb.getName());

        s.assertThat(pet.getStatus().getValue())
                .as("доступность пета к покупке")
                .isEqualTo(petDb.getStatus().getValue());
    }

    @Step("проверка поля со списком")
    private static void listCheckPet(SoftAssertions s, Pet pet, PetDto petDb) {
        s.assertThat(pet.getTags())
                .hasSize(petDb.getTags().size())
                .map(Tag::getName)
                .containsSequence(petDb.getTags().stream().map(TagDto::getName).toList());
    }

    class CrutchSoftAssertions extends AbstractSoftAssertions implements StandardSoftAssertionsProvider {

        public static void assertSoftly(Consumer<SoftAssertions> softly) {
            assertSoftly(SoftAssertions.class, softly);
        }

        static <S extends SoftAssertionsProvider> void assertSoftly(Class<S> type, Consumer<S> softly) {
            S assertions;
            try {
                assertions = type.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            softly.accept(assertions);

            //ТОЛЬКО если "бла-бла" - единственная найденная ошибка - то пасс, иначе выводим все ошибки считая и эту
            List<AssertionError> assertionErrors = assertions.assertionErrorsCollected();

            if (!(assertionErrors.stream().allMatch(e -> e.getMessage().contains("bla bla bla")))) {
                assertions.assertAll();
            }
        }
    }
}
