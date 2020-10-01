package inc.roms.rcs.service.operatorpanel.domain.barcode.model;

import org.junit.jupiter.api.Test;

import static inc.roms.rcs.service.operatorpanel.domain.barcode.model.BarcodeCharacter.*;
import static org.assertj.core.api.Assertions.assertThat;

public class BarcodeBuilderTest {

    @Test
    public void shouldBuildBarcodeWithoutPrefix() {
        Barcode.BarcodeBuilder barcodeBuilder = Barcode.builder();

        barcodeBuilder.append(START);
        barcodeBuilder.append(new BarcodeCharacter('1'));
        barcodeBuilder.append(new BarcodeCharacter('1'));
        barcodeBuilder.append(new BarcodeCharacter('2'));
        barcodeBuilder.append(new BarcodeCharacter('3'));
        barcodeBuilder.append(new BarcodeCharacter('4'));
        barcodeBuilder.append(TERMINAL);

        Barcode barcode = barcodeBuilder.build();

        assertThat(barcode.getPrefix()).isNullOrEmpty();
        assertThat(barcode.getBarcode()).isEqualTo("11234");
    }

    @Test
    public void shouldOmitNullCharacters() {
        Barcode.BarcodeBuilder barcodeBuilder = Barcode.builder();

        barcodeBuilder.append(START);
        barcodeBuilder.append(new BarcodeCharacter('1'));
        barcodeBuilder.append(new BarcodeCharacter(null));
        barcodeBuilder.append(new BarcodeCharacter('2'));
        barcodeBuilder.append(new BarcodeCharacter('3'));
        barcodeBuilder.append(new BarcodeCharacter('4'));
        barcodeBuilder.append(TERMINAL);

        Barcode barcode = barcodeBuilder.build();

        assertThat(barcode.getPrefix()).isNullOrEmpty();
        assertThat(barcode.getBarcode()).isEqualTo("1234");
    }

    @Test
    public void shouldBuildBarcodeWithPrefix() {
        Barcode.BarcodeBuilder barcodeBuilder = Barcode.builder();

        barcodeBuilder.append(START);
        barcodeBuilder.append(new BarcodeCharacter('1'));
        barcodeBuilder.append(SEPARATOR);
        barcodeBuilder.append(new BarcodeCharacter('2'));
        barcodeBuilder.append(new BarcodeCharacter('3'));
        barcodeBuilder.append(new BarcodeCharacter('4'));
        barcodeBuilder.append(TERMINAL);

        Barcode barcode = barcodeBuilder.build();

        assertThat(barcode.getPrefix()).isEqualTo("1");
        assertThat(barcode.getBarcode()).isEqualTo("234");
    }
}
