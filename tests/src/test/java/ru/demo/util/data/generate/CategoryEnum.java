package ru.demo.util.data.generate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.demo.util.data.CategoryDto;

import java.util.Random;

@AllArgsConstructor
@Getter
public enum CategoryEnum {

    CAT("котик",1L),
    DOG("собакен",2L),
    FISH("рыбка",3L),
    CROCODILE("крокодиль",4L),
    BIRD("птичка",5L);


    private final String value;
    private final Long id;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static CategoryEnum fromValue(String value) {
        for (CategoryEnum b : CategoryEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }


    public static CategoryEnum getRandomCategoryEnum() {
        CategoryEnum[] directions = values();
        return directions[new Random().nextInt(directions.length)];
    }

    public static CategoryDto getRandomCategory() {
        CategoryEnum[] directions = values();
        CategoryEnum category = directions[new Random().nextInt(directions.length)];
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getValue())
                .build();
    }


}
