package com.hotel.reservation.strategy;

import com.hotel.reservation.model.SeasonType;
import java.math.BigDecimal;

public interface SeasonRateStrategy {

    BigDecimal apply(BigDecimal basePrice);

    SeasonType season();
}
