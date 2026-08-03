package com.loudsight.meta.serialization.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Temporals are stored as UTC instants (see TimeProvider). These pin the distinction that makes
 * that contract hold: an offset-carrying value must be *converted* to UTC, while a LocalDateTime
 * - which is already UTC by contract - must be stamped, not shifted.
 */
class TemporalEntityTransformTest {

    @Test
    void zonedDateTimeIsConvertedToUtcNotRelabelled() {
        // 10:00 at +01:00 is 09:00 UTC. withZoneSameLocal would have kept the wall clock and
        // stored 10:00 UTC, moving the instant an hour into the future.
        ZonedDateTime plusOne = ZonedDateTime.of(2026, 8, 3, 10, 0, 0, 0, ZoneOffset.ofHours(1));

        assertEquals(LocalDateTime.of(2026, 8, 3, 9, 0), roundTrip(plusOne));
    }

    @Test
    void negativeOffsetIsConvertedToUtc() {
        ZonedDateTime minusFive = ZonedDateTime.of(2026, 8, 3, 10, 0, 0, 0, ZoneOffset.ofHours(-5));

        assertEquals(LocalDateTime.of(2026, 8, 3, 15, 0), roundTrip(minusFive));
    }

    @Test
    void utcZonedDateTimeIsUnchanged() {
        ZonedDateTime utc = ZonedDateTime.of(2026, 8, 3, 10, 0, 0, 0, ZoneOffset.UTC);

        assertEquals(LocalDateTime.of(2026, 8, 3, 10, 0), roundTrip(utc));
    }

    @Test
    void localDateTimeIsAlreadyUtcAndKeepsItsWallClock() {
        LocalDateTime local = LocalDateTime.of(2026, 8, 3, 10, 0);

        assertEquals(local, roundTrip(local));
    }

    private static Temporal roundTrip(Temporal entity) {
        List<Byte> bytes = new ArrayList<>();
        TemporalEntityTransform.getInstance().serializeEntity(entity, bytes);
        var iterator = bytes.iterator();
        iterator.next(); // discard the EntityType.DATETIME code written by serializeEntity
        return TemporalEntityTransform.getInstance().deserializeEntity(iterator);
    }
}
