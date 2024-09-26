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
public class _7AssertCatchExceptionTest {

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
//        pet.setCategory(null);

        //assert
        step("проверки", () -> SoftAssertions.assertSoftly(s -> {

            //1 - проверяем что код не выкинул исключений
//            s.assertThatCode(() -> {
//                s.assertThat(pet.getCategory()).satisfies(category -> {
//                    assertThat(category.getId())
//                            .isEqualTo(petDb.getCategory().getId());
//                    assertThat(category.getName())
//                            .isEqualTo(petDb.getCategory().getName());
//                });
//            }).doesNotThrowAnyException();

            //2 - проверяем на конкретное исключение
//            s.assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> {
//                s.assertThat(pet.getCategory()).satisfies(category -> {
//                    assertThat(category.getId())
//                            .isEqualTo(petDb.getCategory().getId());
//                    assertThat(category.getName())
//                            .isEqualTo(petDb.getCategory().getName());
//                });
//            });

//            s.assertThatThrownBy(() -> {
//                s.assertThat(pet.getCategory()).satisfies(category -> {
//                    assertThat(category.getId())
//                            .isEqualTo(petDb.getCategory().getId());
//                    assertThat(category.getName())
//                            .isEqualTo(petDb.getCategory().getName());
//                }).isInstanceOf(NullPointerException.class);
//            });

            //3 - ловим исключение трайкетчем и прописываем нужное сообщение в проверке для тестопса
            try {
                s.assertThat(pet.getCategory()).satisfies(category -> {
                    assertThat(category.getId())
                            .isEqualTo(petDb.getCategory().getId());
                    assertThat(category.getName())
//                            .isEqualTo(petDb.getCategory().getId());
                            .isEqualTo(petDb.getCategory().getName());
                    s.assertThat(true).isTrue();
                });
            } catch (Exception e) {
                s.assertThat(false).overridingErrorMessage("bla bla bla custom message")
                        .isTrue();
            }

        }));
    }
}
