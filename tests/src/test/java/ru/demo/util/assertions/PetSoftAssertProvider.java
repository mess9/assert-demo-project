package ru.demo.util.assertions;

import org.assertj.core.api.SoftAssertionsProvider;
import ru.demo.tests.petstore.model.Pet;

public interface PetSoftAssertProvider extends SoftAssertionsProvider {

    default PetAssert assertThat(Pet actual) {
        return proxy(PetAssert.class, Pet.class, actual);
    }

}
