package ru.demo.util.assertions;

import io.qameta.allure.Step;
import org.assertj.core.api.AbstractAssert;
import ru.demo.test._10CustomAssertObjectTest;
import ru.demo.tests.petstore.model.Pet;
import ru.demo.util.data.PetDto;

import java.util.Objects;

public class PetAssert extends AbstractAssert<PetAssert, Pet> {

    public PetAssert(Pet actual) {
        super(actual, PetAssert.class);
    }

    public static PetAssert assertThat(Pet actual){
        return new PetAssert(actual);
    }


    @Step("проверка питомца кастомным ассертом")
    public PetAssert hasEqualToPetDto(PetDto petDto /*smth check params*/){
        isNotNull();
        if (!Objects.equals(actual.getName(), petDto.getName())){
            failWithMessage("имя не совпадает, должно быть - {%s} , но было - {%s}", petDto.getName(), actual.getName());
        }
        // любое другое количество любых других сравнений

        //даже со вложенностью(если вдруг надо)
//        if (condition 1) {
//            failWithMessage(error text);
//            if (condition 2) {
//                failWithMessage(error text);
//                if (condition 3) {
//                    failWithMessage(error text);
//                }
//            }
//        }

        return this;
    }

    // любое количество методов для других сравнений

}
