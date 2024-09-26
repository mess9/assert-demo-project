package ru.demo.util.handler;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Класс для логирования в файл LOG4J2 данных http запросов RestAssured
 */
@Slf4j
public class RestAssuredLogFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext filterContext) {
        Response response = filterContext.next(requestSpec, responseSpec);

        log.info(request(requestSpec));
        log.info(response(response));

        return response;
    }


    private String request(FilterableRequestSpecification requestSpec) {
        StringBuilder sb = new StringBuilder();
        sb.append("<-- REQUEST -->\n");
//        sb.append("HTTP_CLIENT => ").append(requestSpec.getHttpClient()).append("\n");
        sb.append(requestSpec.getMethod()).append(" ").append(requestSpec.getURI()).append("\n");
        sb.append("HEADERS =>\n").append(getFormattedHeaderList(requestSpec.getHeaders())).append("\n");
        if (requestSpec.getBody() != null) {
            sb.append("Request Body =>\n").append(requestSpec.getBody().toString()).append("\n");
        }

        return sb.toString();
    }

    private String response(Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append("<-- RESPONSE -->\n");
        sb.append("TIME => ").append(response.getTimeIn(TimeUnit.MILLISECONDS)).append(" MILLISECONDS\n");
        sb.append("Status Code => ").append(response.getStatusCode()).append("\n");
        sb.append("HEADERS =>\n").append(getFormattedHeaderList(response.getHeaders()));
        if (!response.getBody().asString().isBlank()) {
            sb.append("Response body => \n").append(getFormattedResponseBody(response)).append("\n");
        }

        return sb.toString();
    }

    /**
     * Получить список headers в отформатированном виде (prettify)
     *
     * @param headers - хидеры
     * @return - formatted String
     */
    public static String getFormattedHeaderList(Headers headers) {
        StringBuilder builder = new StringBuilder();

        for (Header header : headers) {
            builder.append("\t");
            builder.append(header.getName());
            builder.append(" : ");
            builder.append(header.getValue());
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Получить Response Body в отформатированном виде (prettify) в зависимости от типа json, xml, html
     * иначе как простой текст
     *
     * @param response - Response
     * @return - formatted String
     */
    public static String getFormattedResponseBody(Response response) {

        String contentType = response.header("Content-Type");

        if (contentType != null && (!response.getBody().asString().isEmpty())) {
            if (contentType.startsWith("application/json")) {
                return response.getBody().jsonPath().prettify();
            }
            if (contentType.startsWith("application/xml")) {
                return response.getBody().xmlPath().prettify();
            }
            if (contentType.startsWith("text/xml")) {
                return response.getBody().xmlPath().prettify();
            }
            if (contentType.startsWith("application/html")) {
                return response.getBody().htmlPath().prettify();
            }
        }

        return "";
    }
}