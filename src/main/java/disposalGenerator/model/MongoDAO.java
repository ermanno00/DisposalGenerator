package disposalGenerator.model;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import disposalGenerator.configuration.Configuration;
import disposalGenerator.model.entities.CacheEntity;
import disposalGenerator.model.entities.CollectionPointStatusEntity;
import disposalGenerator.model.entities.ItineraryEntity;
import disposalGenerator.model.entities.ItineraryState;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDAO {
    private final String connectionUri;
    private final CodecRegistry pojoCodecRegistry;
    private final MongoClient mongoClient;

    public MongoDAO() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        this.connectionUri = "mongodb://" + Configuration.mongoHost + ":" + Configuration.mongoPort;
        mongoClient = MongoClients.create(connectionUri);
    }

    private MongoCollection getCollection(String collectionName, Class classType) {
        MongoDatabase database= mongoClient.getDatabase("scheduling").withCodecRegistry(pojoCodecRegistry);
        return database.getCollection(collectionName, classType);
    }


//    public ItineraryEntity getActiveItineraryByVehicleId(UUID vehicleId) {
//        MongoCollection<ItineraryEntity> collection =getCollection("Itineraries",ItineraryEntity.class);
//        return collection.find(and(
//                eq("state", ItineraryState.RUNNING),
//                eq("vehicleId",vehicleId))
//        ).first();
//    }

    public List<ItineraryEntity> getItinerariesByVehicleId(UUID vehicleId) {
        MongoCollection<ItineraryEntity> collection =getCollection("Itineraries", ItineraryEntity.class);
        List<ItineraryEntity> itineraryEntityList = new ArrayList<>();
        collection.find(eq("vehicleId",vehicleId)).into(itineraryEntityList);

        return itineraryEntityList;
    }

//    public int getEffectiveDemandByCollectionPointId(UUID collectionPointId){
//        MongoCollection<CollectionPointStatusEntity> collection =getCollection("CollectionPointsStatus", CollectionPointStatusEntity.class);
//        List<CollectionPointStatusEntity> collectionPointStatus = new ArrayList<>();
//        return collection.find(eq("id", collectionPointId)).first().getEffectiveDemand();
//    }
//
//    public int getExpectedDemandByCollectionPointId(UUID collectionPointId){
//        MongoCollection<CollectionPointStatusEntity> collection =getCollection("CollectionPointsStatus", CollectionPointStatusEntity.class);
//        List<CollectionPointStatusEntity> collectionPointStatus = new ArrayList<>();
//        return collection.find(eq("id", collectionPointId)).first().getAverageDemand();
//    }

    public List<CollectionPointStatusEntity> getCollectionPointStatusByIDIn(List<UUID> collectionPointId){
        MongoCollection<CollectionPointStatusEntity> collection =getCollection("CollectionPointsStatus", CollectionPointStatusEntity.class);
        List<CollectionPointStatusEntity>result = new ArrayList<>();

        return collection.find(in("_id", collectionPointId)).into(result);
    }

    public List<CacheEntity> getCacheEntity(){
        MongoCollection<CacheEntity> collection =getCollection("cache", CacheEntity.class);
        List<CacheEntity>result = new ArrayList<>();

        return collection.find().into(result);
    }

    public void closeSession(){
        this.mongoClient.close();
    }
}
