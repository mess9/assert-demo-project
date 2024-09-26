package ru.demo.util.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum MongoDbClient {

    INSTANCE;

    private final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

}
