package ru.demo.test;

import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.demo.step.DbStep;
import ru.demo.tests.petstore.model.Pet;
import ru.demo.util.data.PetDto;
import ru.demo.util.extension.MongoConnect;
import ru.demo.util.extension.PrepareDb;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.qameta.allure.Allure.step;
import static ru.demo.util.config.ApiConfig.api;


@ExtendWith({
        PrepareDb.class,
        MongoConnect.class
})
public class _7ListDeepCheckTest {

    final DbStep dbStep = new DbStep();

    //проверка списка со списком

    @Test
    void getPetsByIdAll() {
        //arrange
        var allPets = dbStep.getAllPets();

        //act
        List<Pet> responsePets = step("получить питомцев api", () ->
                allPets.stream()
                        .map(pet -> api.pet()
                                .getPetById()
                                .petIdPath(pet.getId().toHexString())
                                .executeAs(r -> r))
                        .toList());

        //assert
        step("проверки", () -> SoftAssertions.assertSoftly(s -> {
            Map<String, PetDto> mapForCheck = allPets.stream()
                    .collect(Collectors.toMap(
                            p -> p.getId().toHexString(), //ключ мапы
                            Function.identity()));        //значение по ключу, можно заменить на p -> p

            responsePets.forEach(responsePet -> {

                PetDto petDb = mapForCheck.get(responsePet.getId());

                s.assertThat(responsePet.getId())
                        .as("ид питомца")
                        .isEqualTo(petDb.getId().toHexString());

                s.assertThat(responsePet.getName())
                        .as("имя питомца должно быть - %s", petDb.getName())
                        .isEqualTo(petDb.getName());

                s.assertThat(responsePet.getStatus().getValue())
                        .as("доступность пета к покупке")
                        .isEqualTo(petDb.getStatus().getValue());
            });
        }));
    }

    @Test
    void getPetsByIdAll2() {
        //arrange
        var allPets = dbStep.getAllPets();

        //act
        List<Pet> responsePets = step("получить питомцев api", () ->
                allPets.stream()
                        .map(pet -> api.pet()
                                .getPetById()
                                .petIdPath(pet.getId().toHexString())
                                .executeAs(r -> r))
                        .toList());

        //assert
        step("проверки", () -> SoftAssertions.assertSoftly(s -> {
            Map<String, PetDto> mapForCheck = allPets.stream()
                    .collect(Collectors.toMap(
                            p -> p.getId().toHexString(), //ключ мапы
                            Function.identity()));        //значение по ключу, можно заменить на p -> p

            responsePets.forEach(resp -> commonCheck(s, resp, mapForCheck.get(resp.getId())));
        }));
    }

    @Step("общие проверки")
    private static void commonCheck(SoftAssertions s, Pet responsePet, PetDto petDb) {
        s.assertThat(responsePet.getId())
                .as("ид питомца")
                .isEqualTo(petDb.getId().toHexString());

        s.assertThat(responsePet.getName())
                .as("имя питомца должно быть - %s", petDb.getName())
                .isEqualTo(petDb.getName());

        s.assertThat(responsePet.getStatus().getValue())
                .as("доступность пета к покупке")
                .isEqualTo(petDb.getStatus().getValue());
    }


}
