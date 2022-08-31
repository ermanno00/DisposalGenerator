package disposalGenerator.disposal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DisposalDriver implements Serializable {
    private String id;
    private Date date;
    private String typeOfDisposal;
    private Integer capacity;
    private UUID collectedAt;
    private UUID collectedBy;

}
