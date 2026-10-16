package com.hotel.reservation.strategy;

import com.hotel.reservation.model.SeasonType;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class LowSeasonRate implements SeasonRateStrategy {

    @Override
    public BigDecimal apply(BigDecimal basePrice) {
        return basePrice.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public SeasonType season() {
        return SeasonType.LOW;
    }
}
