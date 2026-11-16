package es.academia.campushub.config;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationOptions;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Prepara la colección {@code seguimientos}: validación mínima {@code $jsonSchema} (campos y tipos,
 * sin fijar el contenido de {@code datos}) e índice por actividad y fecha.
 */
@Component
@RequiredArgsConstructor
public class MongoInicializador implements ApplicationRunner {

  static final String COLECCION = "seguimientos";

  private final MongoTemplate mongoTemplate;

  @Override
  public void run(ApplicationArguments args) {
    MongoDatabase db = mongoTemplate.getDb();
    Document validador = new Document("$jsonSchema", esquema());

    if (mongoTemplate.collectionExists(COLECCION)) {
      db.runCommand(
          new Document("collMod", COLECCION)
              .append("validator", validador)
              .append("validationLevel", "strict")
              .append("validationAction", "error"));
    } else {
      db.createCollection(
          COLECCION,
          new CreateCollectionOptions()
              .validationOptions(new ValidationOptions().validator(validador)));
    }

    db.getCollection(COLECCION)
        .createIndex(
            Indexes.ascending("actividadId", "creadoEn"),
            new IndexOptions().name("idx_seguimiento_actividad_fecha"));
  }

  private static Document esquema() {
    return new Document("bsonType", "object")
        .append("required", List.of("actividadId", "tipo", "creadoEn"))
        .append(
            "properties",
            new Document("actividadId", new Document("bsonType", "number"))
                .append("tipo", new Document("bsonType", "string").append("minLength", 1))
                .append("datos", new Document("bsonType", "object"))
                .append("creadoEn", new Document("bsonType", "date")));
  }
}
