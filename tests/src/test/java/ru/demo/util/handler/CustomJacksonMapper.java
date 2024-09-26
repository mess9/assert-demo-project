package ru.demo.util.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.internal.mapping.Jackson2Mapper;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;
import org.openapitools.jackson.nullable.JsonNullableModule;
import ru.demo.tests.petstore.RFC3339DateFormat;

import java.time.OffsetDateTime;
import java.util.Date;

public class CustomJacksonMapper extends Jackson2Mapper {

    private CustomJacksonMapper() {
        super(createFactory());
    }

    public static CustomJacksonMapper jackson() {
        return new CustomJacksonMapper();
    }


    public static Jackson2ObjectMapperFactory createFactory() {
        return (cls, charset) -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            mapper.registerModule(new JavaTimeModule());

            mapper.setDateFormat(new RFC3339DateFormat());
            mapper.registerModule(new JavaTimeModule());

            mapper = addDataSettingsMapper(mapper);

            JsonNullableModule jnm = new JsonNullableModule();
            mapper.registerModule(jnm);
            return mapper;
        };
    }

    public static ObjectMapper addDataSettingsMapper(ObjectMapper mapper) {
        SimpleModule offsetDateTimeSerializationModule = new SimpleModule();
        SimpleModule dateSerializationModule = new SimpleModule();
        SimpleModule dateDeserializationModule = new SimpleModule();

        offsetDateTimeSerializationModule.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
        dateSerializationModule.addSerializer(Date.class, new DateSerializer());
        dateDeserializationModule.addDeserializer(Date.class, new DateDeserializer());
        dateDeserializationModule.addDeserializer(OffsetDateTime.class, new OffsetDateTimeApiDeserializer());

        mapper.registerModule(offsetDateTimeSerializationModule);
        mapper.registerModule(dateSerializationModule);
        mapper.registerModule(dateDeserializationModule);

        return mapper;
    }

}
