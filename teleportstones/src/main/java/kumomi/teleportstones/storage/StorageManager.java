package kumomi.teleportstones.storage;

public interface StorageManager {
    public boolean init();
    public boolean shutdown();
    public boolean persistData();
}
