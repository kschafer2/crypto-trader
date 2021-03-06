package com.protonmail.kschay.cryptotrader.domain.ticker

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.protonmail.kschay.cryptotrader.domain.symbol.Symbol
import com.protonmail.kschay.cryptotrader.util.DateAndTime

import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
class Ticker {
    Symbol symbol
    List<Double> changes

    Double dayClosePrice() {
        return dayClosePrice(DateAndTime.localCloseHour())
    }

    Double dayClosePrice(final int localCloseHour) {
        return this.changes.get(dayCloseIndex(localCloseHour))
    }

    private static int dayCloseIndex(final int localCloseHour) {
        int currentHour = LocalDateTime.now().getHour()

        if(currentHour < localCloseHour) {
            return currentHour + (24 - localCloseHour)
        }
        return Math.abs(currentHour - localCloseHour)
    }
}
