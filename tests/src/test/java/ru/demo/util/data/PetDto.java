package ru.demo.util.data;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.datafaker.Faker;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.demo.tests.petstore.model.Pet;
import ru.demo.util.handler.ObjectIdDeserializer;
import ru.demo.util.handler.ObjectIdSerializer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static ru.demo.util.handler.ToJson.toJson;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetDto {

    @JsonProperty("_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    private String name;
    private CategoryDto category;
    private List<String> photoUrls;
    private List<TagDto> tags;
    private StatusEnum status;
    private BigDecimal volume;

    @AllArgsConstructor
    @Getter
    public enum StatusEnum {
        AVAILABLE("available"),
        PENDING("pending"),
        SOLD("sold");

        private final String value;


        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static StatusEnum fromValue(String value) {
            for (StatusEnum b : StatusEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        public static StatusEnum getRandomStatus() {
            StatusEnum[] directions = values();
            return directions[new Random().nextInt(directions.length)];
        }
    }

    public static String getRandomName(){
        String s = "Барсик, Мурка, Рекс, Белка, Тиша, Лаки, Снежок, Боня, Чарли, Луна, Граф, Джесси, Арчи, Линда, Бублик, Ника, Джек, Соня, Джонни, Герда, Мишка, Рэкс, Зевс, Флора, Бакс, Пушок, Марс, Лея, Тоша, Феликс, Симба, Бетти, Ричи, Лола, Бим, Кузя, Фрося, Тиша, Боня, Ричи, Джесси, Арчи, Линда, Граф, Юта, Бублик, Ника, Джек, Соня, Джонни, Герда, Мишка, Рэкс";
        String[] split = s.split(", ");
        return split[new Random().nextInt(split.length)];
    }

    public static List<String> getRandomPhotoUrls(){
        Faker f = new Faker();
        return List.of(f.internet().url(), f.internet().url());
    }

}
