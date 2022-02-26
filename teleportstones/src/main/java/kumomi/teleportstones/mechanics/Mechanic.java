package kumomi.teleportstones.mechanics;

import kumomi.teleportstones.App;
import kumomi.teleportstones.util.Messenger;

public class Mechanic {

    private Messenger messenger;
    private App app;

    public Mechanic(App app) {
        this.app = app;
        this.messenger = new Messenger();
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public App getApp() {
        return app;
    }
}
