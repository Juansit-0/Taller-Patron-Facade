package com.hotel.reservation.strategy;

import com.hotel.reservation.model.SeasonType;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class HighSeasonRate implements SeasonRateStrategy {

    private static final BigDecimal MULTIPLIER = new BigDecimal("1.5");

    @Override
    public BigDecimal apply(BigDecimal basePrice) {
        return basePrice.multiply(MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public SeasonType season() {
        return SeasonType.HIGH;
    }
}
