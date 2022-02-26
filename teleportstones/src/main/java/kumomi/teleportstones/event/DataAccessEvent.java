package kumomi.teleportstones.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DataAccessEvent extends Event {

    public static enum DataAccessEventTyp {
        ADD_TELEPORTSTONE, DELETE_TELEPORTSTONE, REQUEST_TELEPORTSTONE
    };

    private static final HandlerList HANDLERS = new HandlerList();

    private DataAccessEventTyp eventTyp;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public DataAccessEvent(DataAccessEventTyp eventTyp) {
        this.eventTyp = eventTyp;

    }

    public DataAccessEventTyp getEventTyp() {
        return this.eventTyp;
    }

    public void setEventTyp(DataAccessEventTyp eventTyp) {
        this.eventTyp = eventTyp;
    }

}
