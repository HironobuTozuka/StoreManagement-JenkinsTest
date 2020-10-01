package inc.roms.rcs.service.operatorpanel.domain.barcode.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class BarcodeCharacter {

    public static Character NULL_CHARACTER = null;
    public static BarcodeCharacter NULL = new BarcodeCharacter(NULL_CHARACTER);

    public static Character START_CHARACTER = '*';
    public static BarcodeCharacter START = new BarcodeCharacter(START_CHARACTER);

    public static Character TERMINAL_CHARACTER = '!';
    public static BarcodeCharacter TERMINAL = new BarcodeCharacter(TERMINAL_CHARACTER);

    public static Character SEPARATOR_CHARACTER = '|';
    public static BarcodeCharacter SEPARATOR = new BarcodeCharacter(SEPARATOR_CHARACTER);

    private Character c;

    public BarcodeCharacter(Character c) {
        this.c = c;
    }

    public boolean isStart() {
        return this.equals(START);
    }

    public boolean isTerminal() {
        return this.equals(TERMINAL);
    }

    public Character getChar() {
        return c;
    }

    public boolean isSeparator() {
        return this.equals(SEPARATOR);
    }

    public boolean isNull() {
        return this.equals(NULL);
    }
}
