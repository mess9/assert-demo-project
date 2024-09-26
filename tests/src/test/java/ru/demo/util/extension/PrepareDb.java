package ru.demo.util.extension;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.datafaker.Faker;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.demo.util.data.OrderDto;
import ru.demo.util.data.PetDto;
import ru.demo.util.data.generate.CategoryEnum;
import ru.demo.util.data.generate.TagEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static ru.demo.util.extension.MongoConnect.COLLECTION_ORDER;
import static ru.demo.util.extension.MongoConnect.COLLECTION_PET;
import static ru.demo.util.extension.MongoConnect.petstoredb;
import static ru.demo.util.handler.ToJson.convertToDocuments;


public class PrepareDb implements SuiteExtension {

    MongoCollection<Document> collectionPet = petstoredb.getCollection(COLLECTION_PET);
    MongoCollection<Document> collectionOrder = petstoredb.getCollection(COLLECTION_ORDER);

    @Override
    public void beforeAllTests(ExtensionContext context) {
        clearDb();

        var pets = createPets(5);
        var orders = createOrders(pets.stream().map(e -> e.getId().toString()).toList());

        List<Document> documentsPets = convertToDocuments(pets);
        List<Document> documentsOrders = convertToDocuments(orders);

        collectionPet.insertMany(documentsPets);
        collectionOrder.insertMany(documentsOrders);
    }

    private void clearDb(){
        collectionPet.deleteMany(new Document());
        collectionOrder.deleteMany(new Document());
    }

    private List<OrderDto> createOrders(List<String> petIds) {
        return petIds.stream()
                .map(id ->
                        OrderDto.builder()
                                .id(ObjectId.get())
                                .petId(id)
                                .quantity(OrderDto.getRandomQty())
                                .shipDate(OrderDto.getRandomDate())
                                .status(OrderDto.StatusEnum.getRandomStatus())
                                .complete(OrderDto.getRandomComplete())
                                .build())
                .toList();
    }

    private List<PetDto> createPets(int count) {
        Faker f = new Faker();
        return Stream.generate(ObjectId::get)
                .limit(count)
                .map(id -> PetDto.builder()
                        .id(id)
                        .name(PetDto.getRandomName())
                        .volume(BigDecimal.valueOf(new Random().nextDouble(5, 100)))
                        .category(CategoryEnum.getRandomCategory())
                        .photoUrls(PetDto.getRandomPhotoUrls())
                        .tags(TagEnum.getRandomTags())
                        .status(PetDto.StatusEnum.getRandomStatus())
                        .build()).toList();
    }

}
