package com.loudsight.meta.serialization.transform;

import com.loudsight.meta.serialization.EntityType;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * A string survives a round trip whatever it holds, and leaves the stream exactly where the next field starts.
 * The second part is what the persistence server depends on: a string whose byte count differs from its
 * character count used to leave bytes behind, so the next length was read from the middle of the text
 * ("readStr: invalid length=615029") and the whole write was lost.
 */
class StringEntityTransformTest {

    private static final List<String> STRINGS = List.of(
            "", "plain ascii", "Café", "a · b", "é", "€ 5", "日本語",
            "emoji 😀 pair", "😀", "mixed é€😀 end");

    /** A transform subclass only to reach the protected helpers the generated entity code uses. */
    private static final class Helpers extends com.loudsight.meta.serialization.EntityTransform<String> {
        Helpers() {
            super(EntityType.STRING, String.class);
        }

        @Override
        public void serializeEntity(String entity, List<Byte> bytes) {
            writeStr(entity, bytes);
        }

        @Override
        public String deserializeEntity(Iterator<Byte> bytes) {
            return StringEntityTransform.getInstance().deserializeEntity(bytes);
        }

        @Override
        public boolean canTransform(Object entity) {
            return entity instanceof String;
        }

        void int32(int value, List<Byte> bytes) {
            writeInt(value, bytes);
        }

        int readInt32(Iterator<Byte> bytes) {
            return readInt(bytes);
        }
    }

    private static Iterator<Byte> afterTypeCode(List<Byte> bytes) {
        Iterator<Byte> it = bytes.iterator();
        it.next();
        return it;
    }

    @Test
    void everyStringRoundTripsThroughTheStringTransform() {
        for (String text : STRINGS) {
            List<Byte> bytes = new ArrayList<>();
            StringEntityTransform.getInstance().serializeEntity(text, bytes);

            assertEquals(text, StringEntityTransform.getInstance().deserializeEntity(afterTypeCode(bytes)), text);
        }
    }

    @Test
    void theStringTransformLeavesTheStreamAtTheNextField() {
        Helpers helpers = new Helpers();
        for (String text : STRINGS) {
            List<Byte> bytes = new ArrayList<>();
            StringEntityTransform.getInstance().serializeEntity(text, bytes);
            helpers.int32(424242, bytes);
            Iterator<Byte> it = afterTypeCode(bytes);

            assertEquals(text, StringEntityTransform.getInstance().deserializeEntity(it), text);
            assertEquals(424242, helpers.readInt32(it), "the field after \"" + text + "\"");
            assertFalse(it.hasNext());
        }
    }

    @Test
    void everyStringRoundTripsThroughTheHelperEntitiesUseForTheirFields() {
        Helpers helpers = new Helpers();
        for (String text : STRINGS) {
            List<Byte> bytes = new ArrayList<>();
            helpers.serializeEntity(text, bytes);
            helpers.int32(424242, bytes);
            Iterator<Byte> it = bytes.iterator();

            assertEquals(text, helpers.deserializeEntity(it), text);
            assertEquals(424242, helpers.readInt32(it), "the field after \"" + text + "\"");
        }
    }

    @Test
    void theLengthWrittenIsTheNumberOfBytesThatFollow() {
        Helpers helpers = new Helpers();
        for (String text : STRINGS) {
            List<Byte> bytes = new ArrayList<>();
            helpers.serializeEntity(text, bytes);
            Iterator<Byte> it = bytes.iterator();

            assertEquals(text.getBytes(StandardCharsets.UTF_8).length, helpers.readInt32(it), text);
        }
    }

    @Test
    void asciiKeepsTheOneByteForOneCharacterLayoutSoExistingDataStillReads() {
        List<Byte> bytes = new ArrayList<>();
        StringEntityTransform.getInstance().serializeEntity("abc", bytes);

        assertEquals(List.of(EntityType.STRING.getCode(), (byte) 3, (byte) 0, (byte) 0, (byte) 0,
                (byte) 'a', (byte) 'b', (byte) 'c'), bytes);
    }
}
