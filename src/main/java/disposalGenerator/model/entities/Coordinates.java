package disposalGenerator.model.entities;

import lombok.*;

import java.util.UUID;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {
    private UUID collectionPointId;
    private double latitude;
    private double longitude;

}
