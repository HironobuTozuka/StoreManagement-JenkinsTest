package inc.roms.rcs.api.external.v2_0.vo;

import java.time.*;

import static java.time.ZoneId.of;

public class JapanTimeHelper {

    public static final String DATE_PATTERN = "yyyy/MM/dd";
    public static final String DATETIME_PATTERN = "yyyy/MM/dd HH:mm:ss z";
    private static Clock clock = Clock.systemUTC();

    public static void setClock(Clock clock) {
        JapanTimeHelper.clock = clock;
    }

    public static ZonedDateTime nowInJapan() {
        return Instant.now(clock).atZone(japan());
    }

    public static ZoneId japan() {
        return of("Asia/Tokyo");
    }

    public static LocalDateTime toUtc(ZonedDateTime zonedDateTime) {
        if(zonedDateTime == null) return null;
        return LocalDateTime.ofInstant(zonedDateTime.toInstant(), ZoneOffset.UTC);
    }

    public static ZonedDateTime toJapanTime(LocalDateTime convertEta) {
        return convertEta.atZone(japan());
    }
}
