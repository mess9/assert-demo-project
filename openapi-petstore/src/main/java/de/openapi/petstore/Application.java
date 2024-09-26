package de.openapi.petstore;

import de.openapi.petstore.controller.PetApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
    LOGGER.info("swagger - http://localhost:8080/swagger-ui/index.html");
  }
}
