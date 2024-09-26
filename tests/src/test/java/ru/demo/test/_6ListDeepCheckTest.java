package ru.demo.test;

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
public class _6ListDeepCheckTest {

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
                            p -> p.getId().toHexString(),
                            Function.identity()));
            responsePets.forEach(responsePet -> checkPet(s, responsePet, mapForCheck.get(responsePet.getId())));
        }));
    }

    private static void checkPet(SoftAssertions s, Pet pet, PetDto petDb) {
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


}
