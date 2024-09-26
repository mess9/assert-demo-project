package ru.demo.util.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import ru.demo.util.handler.ObjectIdDeserializer;
import ru.demo.util.handler.ObjectIdSerializer;
import ru.demo.util.handler.OffsetDateTimeApiDeserializer;
import ru.demo.util.handler.OffsetDateTimeMongoDeserializer;
import ru.demo.util.handler.OffsetDateTimeSerializer;

import java.time.OffsetDateTime;
import java.util.Random;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {

    @JsonProperty("_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    @JsonSerialize(using = ObjectIdSerializer.class)
    ObjectId id;
    String petId;
    Integer quantity;
    @JsonDeserialize(using = OffsetDateTimeMongoDeserializer.class)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    OffsetDateTime shipDate;
    StatusEnum status;
    Boolean complete;

    @AllArgsConstructor
    @Getter
    public enum StatusEnum {
        PLACED("placed"),
        APPROVED("approved"),
        DELIVERED("delivered");

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


    public static Integer getRandomQty() {
        return new Random().nextInt(3);
    }

    public static OffsetDateTime getRandomDate() {
        return OffsetDateTime.now().plusDays(new Random().nextInt(-10, 10));
    }

    public static Boolean getRandomComplete() {
        return new Random().nextBoolean();
    }
}
