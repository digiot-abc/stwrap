package digiot.stwrap.domain.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Payment<T> {
    private T userId;
    private String stripeChargeId;
    private BigDecimal amount;
    private String currency;
    private String status;
}
