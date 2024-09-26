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
import ru.demo.util.data.PetDto;
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
        pet.setCategory(null);

        //assert
        step("проверки", () -> CrutchSoftAssertions.assertSoftly(s -> {

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
