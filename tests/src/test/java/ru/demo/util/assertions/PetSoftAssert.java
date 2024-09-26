package ru.demo.util.assertions;

import org.assertj.core.api.SoftAssertions;
import ru.demo.tests.petstore.model.Pet;

public class PetSoftAssert extends SoftAssertions implements PetSoftAssertProvider {

    public PetAssert assertThat(Pet actual){
        return proxy(PetAssert.class, Pet.class, actual);
    }

}
