package disposalGenerator.disposal;

import disposalGenerator.model.entities.CollectionPointStatusEntity;
import disposalGenerator.model.entities.ItineraryEntity;

import java.util.List;

public interface DisposalGeneratorCallback {
    void onMongoConnectionStatusChange(boolean connected);
    void onArtemisConnectionStatusChange(boolean connected);
    void onError(String error);
    void onMessage(String message);

    void onRoutes(List<ItineraryEntity> itineraryEntities);
    void onCollectionPoint(List<CollectionPointStatusEntity> collectionPointStatusEntities);


}
