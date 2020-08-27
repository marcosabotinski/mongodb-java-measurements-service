package com.sabotinski.mongodbexample.measurementsservice.api.dao;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Projections.*;
import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.model.UpdateOptions;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.sabotinski.mongodbexample.measurementsservice.api.models.EmbeddedMeasurement;
import com.sabotinski.mongodbexample.measurementsservice.api.models.Measurement;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MeasurementsDao {
    private static String MEASUREMENTS_COLLECTION_NAME = "measurements";
    private static int BUCKET_SIZE = 100;
    // because we use the bucketing pattern, we never will directly serialize from
    // finds but using aggregations

    private final CodecProvider measurementCodecProvider = PojoCodecProvider.builder()
    // Register all classes that can be used for automatic mapping from MongoDB to
    // POJOs
    .register(Measurement.class.getPackage().getName())
    //.register(Measurement.class).register(EmbeddedMeasurement.class)
    .build();

    private MongoCollection<Document> measurementsCollection;
    private CodecRegistry pojoCodecRegistry;

    @Autowired
    public MeasurementsDao(MongoClient mongoClient, @Value("${db.databasename}") String databaseName) {
        this.pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
        fromProviders(measurementCodecProvider),
        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        
        measurementsCollection = mongoClient.getDatabase(databaseName).getCollection(MEASUREMENTS_COLLECTION_NAME).withCodecRegistry(pojoCodecRegistry);

    }

    public List<Measurement> getMeasuments(String device, LocalDateTime min_ts, LocalDateTime max_ts) {
        var embeddedmeasurements = measurementsCollection.aggregate(
            asList(
                match(
                    and(
                        eq("device", device),
                        // yepp, thats correct, think about it ;)
                        lte("min_ts", max_ts), 
                        gte("max_ts", min_ts)
                     )
                ), 
                sort(ascending("device", "min_ts")), 
                unwind("$m"),
                match(
                    and(
                        lte("m.ts", max_ts), 
                        gte("m.ts", min_ts)
                    )
                ),
                project(
                    fields(
                        computed("ts", "$m.ts"), 
                        computed("temperature", "$m.temperature"),
                        computed("angle", "$m.angle"), 
                        computed("rpm", "$m.rpm"),
                        computed("status", "$m.status")
                    )
                )
            ),
            EmbeddedMeasurement.class
        );
        var measurements  = new ArrayList<Measurement>();
        embeddedmeasurements.iterator().forEachRemaining(embdoc -> {
            measurements.add(new Measurement(embdoc, device));
        });

        return measurements;
    }

    public void addMeasurement(Measurement measurement) {
        var update = createUpdate(measurement);
        var updates = asList(update);
        measurementsCollection.bulkWrite(updates);
    }

    public void addMeasurements(List<Measurement> measurements) {
        var updates = new ArrayList<WriteModel<Document>>();
        for (var m : measurements) {
            updates.add(createUpdate(m));
        }
        measurementsCollection.bulkWrite(updates);
    }

    private WriteModel<Document> createUpdate(Measurement measurement) {
        var update = new UpdateOneModel<Document>(and(eq("device", measurement.getDevice()), lt("cnt", BUCKET_SIZE)),
                combine(push("m", new EmbeddedMeasurement(measurement)), max("max_ts", measurement.getTs()),
                        min("min_ts", measurement.getTs()), inc("cnt", 1)),
                new UpdateOptions().upsert(true));

        return update;
    }
}