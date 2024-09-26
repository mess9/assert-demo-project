package ru.demo.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.internal.mapping.Jackson2Mapper;
import ru.demo.tests.petstore.ApiClient;
import ru.demo.util.handler.CustomJacksonMapper;
import ru.demo.util.handler.RestAssuredLogFilter;

import java.util.List;

import static ru.demo.tests.petstore.ApiClient.BASE_URI;


public class ApiConfig {

    public final static ApiClient api = getApi();


    private static ApiClient getApi(){
        return ApiClient.api(ApiClient.Config.apiConfig().reqSpecSupplier(() -> new RequestSpecBuilder()
                .addFilters(List.of(new RestAssuredLogFilter(),  new AllureRestAssured()))
                .setBaseUri(BASE_URI)
                .setConfig(RestAssuredConfig.config().objectMapperConfig(
                        ObjectMapperConfig.objectMapperConfig().defaultObjectMapper(
                                CustomJacksonMapper.jackson())
                        ))));
    }



}
