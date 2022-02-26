package kumomi.teleportstones.accessmethods;

import kumomi.teleportstones.App;

public abstract class AccessMethod implements StatusMessage{

    private String statusMessage;
    private App app;

    public AccessMethod(App app) {
        this.app = app;
        this.statusMessage = "Not run.";
    }

    public App getApp() {
        return app;
    }
    
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Override
    public String getStatusMessage() {
        return this.statusMessage;
    }

    

}
