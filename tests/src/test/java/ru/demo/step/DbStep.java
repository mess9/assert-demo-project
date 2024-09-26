package ru.demo.step;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import ru.demo.util.data.OrderDto;
import ru.demo.util.data.PetDto;

import java.util.ArrayList;
import java.util.List;

import static io.qameta.allure.Allure.step;

import static ru.demo.util.extension.MongoConnect.COLLECTION_ORDER;
import static ru.demo.util.extension.MongoConnect.COLLECTION_PET;
import static ru.demo.util.extension.MongoConnect.petstoredb;
import static ru.demo.util.handler.ToJson.fromJson;

public class DbStep {

    MongoCollection<Document> collectionPet = petstoredb.getCollection(COLLECTION_PET);
    MongoCollection<Document> collectionOrder = petstoredb.getCollection(COLLECTION_ORDER);

    public List<PetDto> getAllPets(){
        return step("достаём из бд всех питомцев", () -> {
            List<PetDto> pets = new ArrayList<>();
            for (Document document : collectionPet.find()) {
                pets.add(fromJson(document.toJson(), PetDto.class));
            }
            return pets;
        });
    }

    public List<OrderDto> getAllOrders(){
        return step("достаём из бд все ордера", () -> {
            List<OrderDto> pets = new ArrayList<>();
            for (Document document : collectionOrder.find()) {
                pets.add(fromJson(document.toJson(), OrderDto.class));
            }
            return pets;
        });
    }

}
