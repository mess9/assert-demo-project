package ru.demo.util.extension;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.demo.util.config.MongoDbClient;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoConnect implements SuiteExtension {

    public static final String COLLECTION_PET = "pet";
    public static final String COLLECTION_ORDER = "order";

    private static final MongoClient mongoClient = MongoDbClient.INSTANCE.getMongoClient();
    public static MongoDatabase petstoredb = mongoClient.getDatabase("petstoredb");


    @Override
    public void afterAllTests() {
        mongoClient.close();
    }
}
