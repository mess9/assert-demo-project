package de.openapi.petstore.controller;

import de.openapi.petstore.api.PetApi;
import de.openapi.petstore.model.Pet;
import de.openapi.petstore.model.Session;
import de.openapi.petstore.service.PetService;
import de.openapi.petstore.service.SessionService;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.math.RoundingMode;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/petstore/v1")
@Observed(name = "de.openapi.petstore.PetApi")
public class PetApiController implements PetApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(PetApiController.class);

    private final PetService petService;
    private final SessionService sessionService;

    @Autowired
    public PetApiController(PetService petService, SessionService sessionService) {
        this.petService = petService;
        this.sessionService = sessionService;
    }

    public ResponseEntity<Pet> _addPet(
            @Parameter(name = "Pet", description = "Create a new pet in the store", required = true, schema = @Schema(description = ""))
            @Valid @RequestBody Pet pet) {

        if (pet == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#addPet",
                getClass().getCanonicalName());

        var addedPet = petService.addPet(pet);
        return ResponseEntity.ok().body(addedPet);
    }

    public ResponseEntity<Void> _deletePet(
            @Parameter(name = "petId", description = "Pet id to delete", required = true, schema = @Schema(description = "")) @PathVariable("petId") String petId,
            @Parameter(name = "api_key", description = "", schema = @Schema(description = "")) @RequestHeader(value = "api_key", required = false) String apiKey) {

        LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#deletePet",
                getClass().getCanonicalName());

        if (petId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        petService.deletePet(new ObjectId(petId));
        var pet = petService.getPetById(new ObjectId(petId));

        if (pet == null) {
            return ResponseEntity.ok().body(null);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(null);
        }
    }

    public ResponseEntity<List<Pet>> _findPetsByStatus(
            @Parameter(name = "status", description = "Status values that need to be considered for filter", schema = @Schema(description = "", allowableValues = {
                    "available", "pending", "sold"}, defaultValue = "available"))
            @Valid @RequestParam(value = "status", required = false, defaultValue = "available") String status) {

        LOGGER.info(
                "Hello from demo-petstore-service!\nThis log message was produced at {}#findPetsByStatus",
                getClass().getCanonicalName());

        if (status == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        var pets = petService.findPetsByStatus(status);
        if (pets != null) {
            return ResponseEntity.ok().body(pets);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    public ResponseEntity<Pet> _getPetById(
            @Parameter(name = "petId", description = "ID of pet to return", required = true, schema = @Schema(description = "")) @PathVariable("petId") String petId) {

        LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#getPetById",
                getClass().getCanonicalName());

        if (petId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        var pet = petService.getPetById(new ObjectId(petId));
        if (pet != null) {
            pet.volume(pet.getVolume().setScale(3, RoundingMode.HALF_UP));
            return ResponseEntity.ok().body(pet);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    public ResponseEntity<Pet> _updatePet(
            @Parameter(name = "Pet", description = "Update an existent pet in the store", required = true, schema = @Schema(description = "")) @Valid @RequestBody Pet pet,
            @Parameter(name = "X-Session-ID", description = "", schema = @Schema(description = "")) @RequestHeader(value = "X-Session-ID", required = false) String xSessionID) {

        LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#updatePet",
                getClass().getCanonicalName());

        if (pet == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Session session = this.sessionService.getSessionById(xSessionID);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        var updatedPet = petService.updatePet(pet);
        if (updatedPet != null) {
            return ResponseEntity.ok().body(updatedPet);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
