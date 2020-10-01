package inc.roms.rcs.service.operatorpanel.domain.barcode.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConversionTableTest {

    private byte[] two = convertToByteArray((byte)31);
    private byte[] four = convertToByteArray((byte)33);
    private byte[] unknown = convertToByteArray((byte)93);

    @Test
    public void shouldReturnNullCharacterForUnknown() {
        assertThat(ConversionTable.decode(unknown)).isEqualTo(BarcodeCharacter.NULL);
    }

    @Test
    public void shouldConvertTwo() {
        Character expected = '2';

        assertThat(ConversionTable.decode(two)).isEqualTo(new BarcodeCharacter(expected));
    }

    @Test
    public void shouldConvertFour() {
        Character expected = '4';

        assertThat(ConversionTable.decode(four)).isEqualTo(new BarcodeCharacter(expected));
    }

    private static byte[] convertToByteArray(byte thirdByteValue) {
        byte[] result = new byte[16];
        result[2] = thirdByteValue;
        return result;
    }

}