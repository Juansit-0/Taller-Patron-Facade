package com.hotel.reservation.service;

import com.hotel.reservation.model.Room;
import com.hotel.reservation.strategy.HighSeasonRate;
import com.hotel.reservation.strategy.LowSeasonRate;
import com.hotel.reservation.strategy.SeasonRateStrategy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class RateService {

    private static final Set<Month> HIGH_SEASON_MONTHS =
            Set.of(Month.DECEMBER, Month.JANUARY, Month.JUNE, Month.JULY);

    private final SeasonRateStrategy highSeasonRate = new HighSeasonRate();
    private final SeasonRateStrategy lowSeasonRate = new LowSeasonRate();

    public SeasonRateStrategy strategyFor(LocalDate checkIn) {
        return HIGH_SEASON_MONTHS.contains(checkIn.getMonth()) ? highSeasonRate : lowSeasonRate;
    }

    public BigDecimal nightlyRate(Room room, LocalDate checkIn) {
        return strategyFor(checkIn).apply(room.getBasePrice());
    }

    public BigDecimal calculateTotal(Room room, LocalDate checkIn, long nights) {
        return nightlyRate(room, checkIn).multiply(BigDecimal.valueOf(nights));
    }
}
