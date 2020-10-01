package inc.roms.rcs.service.operatorpanel.domain.barcode.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static inc.roms.rcs.service.operatorpanel.domain.barcode.model.BarcodeCharacter.NULL;

public class ConversionTable {
    private static Map<ByteArray, BarcodeCharacter> usageIdToCharacterMapping = new HashMap<>();

    private static ByteArray fillArray(byte[] meaningfulCharacters) {
        return new ByteArray(Arrays.copyOf(meaningfulCharacters, 16));
    }

    static {
        put(new byte[]{0, 0, 30}, '1');
        put(new byte[]{0, 0, 31}, '2');
        put(new byte[]{0, 0, 32}, '3');
        put(new byte[]{0, 0, 33}, '4');
        put(new byte[]{0, 0, 34}, '5');
        put(new byte[]{0, 0, 35}, '6');
        put(new byte[]{0, 0, 36}, '7');
        put(new byte[]{0, 0, 37}, '8');
        put(new byte[]{0, 0, 38}, '9');
        put(new byte[]{0, 0, 39}, '0');

        put(new byte[]{2, 0, 30}, BarcodeCharacter.TERMINAL_CHARACTER);
        put(new byte[]{2, 0, 37}, BarcodeCharacter.START_CHARACTER);
        put(new byte[]{2, 0, 49}, BarcodeCharacter.SEPARATOR_CHARACTER);

    }

    private static void put(byte[] meaningfulCharacters, BarcodeCharacter c) {
        usageIdToCharacterMapping.put(fillArray(meaningfulCharacters), c);
    }

    private static void put(byte[] meaningfulCharacters, char c) {
        put(meaningfulCharacters, new BarcodeCharacter(c));
    }

    public static BarcodeCharacter decode(byte[] data) {
        return usageIdToCharacterMapping.getOrDefault(new ByteArray(data), NULL);
    }

    private static class ByteArray {
        private final byte[] internalArray;

        private ByteArray(byte[] internalArray) {
            this.internalArray = internalArray;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ByteArray byteArray = (ByteArray) o;
            return Arrays.equals(internalArray, byteArray.internalArray);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(internalArray);
        }
    }

}
