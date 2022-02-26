package kumomi.teleportstones.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;

public class EventHandler {

    public static enum EventTyp {
        INFO, ERROR, TELEPORT, CREATE, DELETE, DESTROY, DISCOVER, REQUEST,
    };

    public static class Event {

        private EventTyp eventTyp;
        private String message;

        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;

        public Event(EventTyp eventTyp, String message) {
            this.eventTyp = eventTyp;
            this.message = message;

            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
            cal.setTime(new Date());

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            day = cal.get(Calendar.DAY_OF_MONTH);
            hour = cal.get(Calendar.HOUR_OF_DAY);
            minute = cal.get(Calendar.MINUTE);

            switch (eventTyp) {
                case INFO:
                case CREATE:
                case TELEPORT:
                case DISCOVER:
                    Bukkit.getLogger().info("[TeleportStones] [" + eventTyp.toString() + "] " + message);
                    break;

                case ERROR:
                    Bukkit.getLogger().severe("[TeleportStones] [" + eventTyp.toString() + "] " + message);
                    break;

                case DELETE:
                case DESTROY:
                    Bukkit.getLogger().warning("[TeleportStones] [" + eventTyp.toString() + "] " + message);
                    break;

                default:
                    Bukkit.getLogger().info("[TeleportStones] " + message);
                    break;
            }
        }

        public EventTyp getEventTyp() {
            return eventTyp;
        }

        public void setEventTyp(EventTyp eventTyp) {
            this.eventTyp = eventTyp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }
    }

    private static ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();

    public static void add(Event event) {

        if (queue.size() > 100) {
            queue.poll();
        }

        if (!queue.add(event)) {
            Bukkit.getLogger().warning("[TeleportStone] Couldn't add Event to Queue.");
        }
    }

    public static ConcurrentLinkedQueue<Event> getQueue() {
        return queue;
    }

}
