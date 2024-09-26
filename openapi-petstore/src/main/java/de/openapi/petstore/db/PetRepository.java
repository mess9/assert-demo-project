package de.openapi.petstore.db;

import de.openapi.petstore.model.Pet;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PetRepository extends MongoRepository<Pet, String> {
    Pet findById(ObjectId id);
    void deleteById(ObjectId id);
    List<Pet> findByStatusIn(List<String> status);
}
