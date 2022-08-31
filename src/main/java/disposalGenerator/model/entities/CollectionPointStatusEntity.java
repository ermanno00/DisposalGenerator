package disposalGenerator.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionPointStatusEntity {
    private UUID id;
    private UUID vehicleId;
    private int averageDemand;
    private int effectiveDemand=0;
    private boolean isRouted;

    public void addDisposal(){
        this.effectiveDemand++;
    }

}
