package ru.demo.util.data.generate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.demo.util.data.TagDto;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum TagEnum {

    BEAUTIFUL("красивый",1L),
    PLAYFUL("игривый",2L),
    WARM("уютный",3L),
    HUNGRY("голодный",4L),
    RADIOACTIVE("опасный",5L);


    private final String value;
    private final Long id;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static TagEnum fromValue(String value) {
        for (TagEnum b : TagEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }


    public static TagEnum getRandomTag() {
        TagEnum[] directions = values();
        return directions[new Random().nextInt(directions.length)];
    }

    public static List<TagDto> getRandomTags(){
        return Stream.generate(TagEnum::getRandomTag)
                .limit(new Random().nextInt(1,4))
                .distinct()
                .map(e -> new TagDto(e.getId(), e.getValue())).toList();
    }

}
