package disposalGenerator.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {
    private UUID collectionPointId;
    private double latitude;
    private double longitude;

}
