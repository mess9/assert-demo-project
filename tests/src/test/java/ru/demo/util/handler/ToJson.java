package ru.demo.util.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import ru.demo.tests.petstore.RFC3339DateFormat;
import ru.demo.util.data.PetDto;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static ru.demo.util.handler.CustomJacksonMapper.addDataSettingsMapper;

/**
 *
 */
@SuppressWarnings({"JavadocDeclaration", "unused"})
@Slf4j
public class ToJson {


    public static String toJsonShort(Object object) {
        String json = "";

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(NON_NULL); // убирает все NULL поля при сериализации
            mapper.setSerializationInclusion(NON_DEFAULT);
            mapper.setSerializationInclusion(NON_EMPTY);

            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            String failMessage = "Failed to convert to JSON\n";
            log.info(failMessage, e);
        }

        return json;
    }

    public static String toJson(Object object) {
        String json = "";

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            String failMessage = "Failed to convert to JSON\n";
            log.info(failMessage, e);
        }

        return json;
    }

    public static String toJsonFlat(Object object) {
        String json = "";

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(NON_NULL);
            mapper.setSerializationInclusion(NON_DEFAULT);
            mapper.setSerializationInclusion(NON_EMPTY);

            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            String failMessage = "Failed to convert to JSON\n";
            log.info(failMessage, e);
        }

        return json;
    }

    public static <T> T fromJson(String string, Class<T> valueType) {
        T t = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            t = mapper.readValue(string, valueType);
        } catch (JsonProcessingException e) {
            String failMessage = "Failed to convert from JSON\n" + string;
            log.info(failMessage, e);
        }
        return t;
    }

    public static <T> List<T> fromJsonList(String string) {
        List<T> list = null;
        try {
            ObjectMapper mapper = new ObjectMapper();

            list = mapper.readValue(string, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            String failMessage = "Failed to convert from JSON\n" + string;
            log.info(failMessage, e);
        }
        return list;
    }

    public static <T> List<Document> convertToDocuments(List<T> objects) {
        return objects.stream().map(p -> {
            try {
                return Document.parse(toJson(p));
            } catch (Exception e) {
                throw new RuntimeException("Ошибка сериализации OrderDto в Document", e);
            }
        }).toList();
    }

    public static <T> Document convertToDocument(T object) {
        try {
            return Document.parse(toJson(object));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации OrderDto в Document", e);
        }
    }

}
