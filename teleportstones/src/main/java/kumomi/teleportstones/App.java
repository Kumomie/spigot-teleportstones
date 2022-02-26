package kumomi.teleportstones;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import kumomi.teleportstones.build.TeleportStoneValidator;
import kumomi.teleportstones.build.structure.Blueprint;
import kumomi.teleportstones.build.structure.OffsetBlock;
import kumomi.teleportstones.build.structure.Scope;
import kumomi.teleportstones.command.CommandTeleportStone;
import kumomi.teleportstones.command.TapCompleterTeleportStone;
import kumomi.teleportstones.configuration.Configuration;
import kumomi.teleportstones.mechanics.PlayerJoinChecker;
import kumomi.teleportstones.mechanics.SignChangeListener;
import kumomi.teleportstones.mechanics.SignClickListener;
import kumomi.teleportstones.mechanics.TeleportStoneProtectListener;
import kumomi.teleportstones.mechanics.teleport.TeleportGUI;
import kumomi.teleportstones.storage.StorageMaster;
import kumomi.teleportstones.storage.model.SimpleBlock;
import kumomi.teleportstones.storage.model.SimpleDiscoveredTeleportStone;
import kumomi.teleportstones.storage.model.SimpleSign;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;

/**
 * Hello world!
 *
 */
public class App extends JavaPlugin {

    private boolean enableSucces;

    private Configuration configuration;

    private StorageMaster storageMaster;

    @Override
    public void onEnable() {

        enableSucces = true;

        ConfigurationSerialization.registerClass(SimpleSign.class);
        ConfigurationSerialization.registerClass(SimpleBlock.class);
        ConfigurationSerialization.registerClass(TeleportStone.class);
        ConfigurationSerialization.registerClass(SimpleDiscoveredTeleportStone.class);
        ConfigurationSerialization.registerClass(User.class);

        ConfigurationSerialization.registerClass(OffsetBlock.class);
        ConfigurationSerialization.registerClass(Blueprint.class);
        ConfigurationSerialization.registerClass(Scope.class);

        if (!enable()) {
            this.getLogger().severe("Error on startup.");
            Bukkit.getPluginManager().disablePlugin(this);
            enableSucces = false;
            return;
        }

        if (enableSucces == false) {
            return;
        }

        getServer().getPluginManager().registerEvents(new SignChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new SignClickListener(this), this);
        getServer().getPluginManager().registerEvents(this.storageMaster, this);
        getServer().getPluginManager().registerEvents(new TeleportGUI(this), this);
        getServer().getPluginManager().registerEvents(new TeleportStoneProtectListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinChecker(this), this);

        this.getCommand("tpst").setExecutor(new CommandTeleportStone(this));

        if (this.configuration.getYmlConfiguration().getBoolean("enableTabCompletion")) {
            this.getCommand("tpst").setTabCompleter(new TapCompleterTeleportStone());
        }

    }

    private boolean enable() {

        long start = System.currentTimeMillis();

        this.configuration = new Configuration(this);

        if (!configuration.loadConfiguration()) {
            return false;
        }

        if (!configuration.validateConfig()) {
            return false;
        }

        if (!configuration.getYmlConfiguration().getBoolean("enablePlugin")) {
            this.enableSucces = false;
            Bukkit.getPluginManager().disablePlugin(this);
            return true;
        }

        TeleportStoneValidator.keyword = configuration.getYmlConfiguration().getString("keyword");
        TeleportStoneValidator.minNameLength = configuration.getYmlConfiguration()
                .getInt("minimalTeleportStoneNameLength");

        this.storageMaster = new StorageMaster(this);

        if (!storageMaster.init()) {
            this.enableSucces = false;
            return false;
        }

        long finish = System.currentTimeMillis();

        getLogger().info("Succesfully enabled. (" + (finish - start) + "ms)");

        return true;
    }

    @Override
    public void onDisable() {

        if (!enableSucces) {
            return;
        }

        storageMaster.shutdown();
        getLogger().info("Bye Bye.");
    }

    public Configuration getConfiguration() {
        return configuration;
    }

}
