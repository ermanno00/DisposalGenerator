package disposalGenerator.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class DisposalEntity {

    private UUID id;
    private String externalId;
    private LocalDate localDate;
    private String typeOfDisposal;
    private int capacity;
    private UUID collectedAt;
    private UUID collectedBy;
}
