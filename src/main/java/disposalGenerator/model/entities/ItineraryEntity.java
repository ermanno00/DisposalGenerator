package disposalGenerator.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItineraryEntity {
    private UUID id;
    private long timestamp;
    private UUID vehicleId;
    private List<Coordinates> coordinates = new LinkedList<>();
    private double cost;
    private ItineraryState state;
    private List<UUID> servedNodes;
}