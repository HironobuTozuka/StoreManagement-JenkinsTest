package inc.roms.rcs.service.operatorpanel.domain.barcode.model;

import inc.roms.rcs.service.operatorpanel.domain.barcode.exception.MalformedBarcodeException;
import lombok.Value;

@Value
public class Barcode {

    private final String prefix;
    private final String barcode;

    public Barcode(String prefix, String barcode) {
        this.prefix = prefix;
        this.barcode = barcode;
    }

    public static BarcodeBuilder builder() {
        return new BarcodeBuilder();
    }

    public static class BarcodeBuilder {
        private String prefix;
        private StringBuilder builder = new StringBuilder();
        private boolean foundSeparator = false;
        private boolean started = false;
        private boolean stopped = false;

        public BarcodeBuilder append(BarcodeCharacter character) {
            if(character.isNull()) return this;

            if(character.isStart()) {
                handleStart();
                return this;
            }

            if(!started || stopped) return this;

            if(character.isTerminal()) {
                handleTerminal();
                return this;
            }

            if(character.isSeparator())  {
                handleSeparator();
                return this;
            }

            builder.append(character.getChar());
            return this;
        }

        public boolean isFinished() {
            return stopped;
        }

        public Barcode build() {
            if(!started || !stopped) throw new MalformedBarcodeException();
            return new Barcode(this.prefix, builder.toString());
        }

        private void handleSeparator() {
            if(foundSeparator) {
                throw new MalformedBarcodeException();
            } else {
                foundSeparator = true;
                prefix = builder.toString();
                builder = new StringBuilder();
            }
        }

        private void handleTerminal() {
            stopped = true;
        }

        private void handleStart() {
            if(started) {
                throw new MalformedBarcodeException();
            } else {
                started = true;
            }
        }

    }
}
