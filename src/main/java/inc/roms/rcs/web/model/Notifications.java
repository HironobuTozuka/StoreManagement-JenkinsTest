package inc.roms.rcs.web.model;

import lombok.Data;

@Data
public class Notifications {

    private String success;
    private String error;

    public static Notifications success(String text) {
        Notifications notifications = new Notifications();
        notifications.setSuccess(text);
        return notifications;
    }

    public static Notifications error(String text) {
        Notifications notifications = new Notifications();
        notifications.setError(text);
        return notifications;
    }

}
