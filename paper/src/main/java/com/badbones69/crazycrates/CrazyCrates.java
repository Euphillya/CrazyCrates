package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.builders.types.CratePreviewMenu;
import com.badbones69.crazycrates.api.builders.types.CrateTierMenu;
import com.badbones69.crazycrates.api.utils.FileUtils;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import com.badbones69.crazycrates.commands.CommandManager;
import com.badbones69.crazycrates.listeners.BrokeLocationsListener;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.MiscListener;
import com.badbones69.crazycrates.listeners.crates.CosmicCrateListener;
import com.badbones69.crazycrates.listeners.crates.CrateOpenListener;
import com.badbones69.crazycrates.listeners.crates.MobileCrateListener;
import com.badbones69.crazycrates.listeners.crates.QuadCrateListener;
import com.badbones69.crazycrates.listeners.crates.WarCrateListener;
import com.badbones69.crazycrates.listeners.other.EntityDamageListener;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.badbones69.crazycrates.support.metrics.MetricsManager;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.MigrationManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.VitalPaper;
import com.ryderbelserion.vital.enums.Support;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.plugin.java.JavaPlugin;
import com.badbones69.crazycrates.api.FileManager;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.Server;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import java.util.List;
import java.util.Timer;
import static com.badbones69.crazycrates.api.utils.MiscUtils.isLogging;
import static com.badbones69.crazycrates.api.utils.MiscUtils.registerPermissions;

public class CrazyCrates extends JavaPlugin {

    private Server instance;

    private final Timer timer;

    public CrazyCrates() {
        // Create timer object.
        this.timer = new Timer();
    }

    private InventoryManager inventoryManager;
    private BukkitUserManager userManager;
    private CrateManager crateManager;
    private FileManager fileManager;

    private MetricsManager metrics;

    @Override
    public void onLoad() {
        // Migrate as early as possible.
        MigrationManager.migrate();

        this.instance = new Server(this);
        this.instance.enable();

        // Register files.
        this.fileManager = new FileManager();
        this.fileManager.registerDefaultGenerateFiles("CrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuadCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("CosmicCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuickCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("WarCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("CasinoExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("classic.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("nether.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("outdoors.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("sea.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("soul.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("wooden.nbt", "/schematics", "/schematics")
                .registerCustomFilesFolder("/crates")
                .registerCustomFilesFolder("/schematics")
                .setup();
    }

    @Override
    public void onEnable() {
        new VitalPaper(this);

        int radius = DedicatedServer.getServer().getSpawnProtectionRadius();

        if (radius > 0) {
            if (isLogging()) {
                List.of(
                        "The spawn protection is set to " + radius,
                        "Crates placed in the spawn protection will not function",
                        "correctly as spawn protection overrides everything",
                        "",
                        "Change the value in server.properties to 0 then restart"
                ).forEach(getLogger()::warning);
            }
        }

        // Register permissions that we need.
        registerPermissions();

        this.inventoryManager = new InventoryManager();
        this.crateManager = new CrateManager();
        this.userManager = new BukkitUserManager();

        this.instance.setUserManager(this.userManager);

        // Load holograms.
        this.crateManager.loadHolograms();

        // Load example files.
        FileUtils.loadFiles();

        // Load the buttons.
        this.inventoryManager.loadButtons();

        // Load the crates.
        this.crateManager.loadCrates();

        // Load commands.
        CommandManager.load();

        this.metrics = new MetricsManager();

        // Load metrics.
        if (ConfigManager.getConfig().getProperty(ConfigKeys.toggle_metrics)) {
            this.metrics.start();
        }

        List.of(
                // Menu listeners.
                new CratePreviewMenu.CratePreviewListener(),
                new CrateAdminMenu.CrateAdminListener(),
                new CrateMainMenu.CrateMenuListener(),
                new CrateTierMenu.CrateTierListener(),

                // Other listeners.
                new BrokeLocationsListener(),
                new CrateControlListener(),
                new EntityDamageListener(),
                new MobileCrateListener(),
                new CosmicCrateListener(),
                new QuadCrateListener(),
                new CrateOpenListener(),
                new WarCrateListener(),
                new MiscListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        if (MiscUtils.isLogging()) {
            String prefix = ConfigManager.getConfig().getProperty(ConfigKeys.console_prefix);

            // Print dependency garbage
            for (Support value : Support.values()) {
                if (value.isEnabled()) {
                    getServer().getConsoleSender().sendMessage(MsgUtils.color(prefix + "&6&l" + value.getName() + " &a&lFOUND"));
                } else {
                    getServer().getConsoleSender().sendMessage(MsgUtils.color(prefix + "&6&l" + value.getName() + " &c&lNOT FOUND"));
                }
            }
        }

        if (Support.placeholder_api.isEnabled()) {
            if (MiscUtils.isLogging()) getLogger().info("PlaceholderAPI support is enabled!");

            new PlaceholderAPISupport().register();
        }

        if (MiscUtils.isLogging()) getLogger().info("You can disable logging by going to the plugin-config.yml and setting verbose to false.");
    }

    @Override
    public void onDisable() {
        // Cancel the timer task.
        this.timer.cancel();

        // Clean up any mess we may have left behind.
        if (this.crateManager != null) {
            this.crateManager.purgeRewards();

            HologramManager holograms = this.crateManager.getHolograms();

            if (holograms != null && !holograms.isEmpty()) {
                holograms.removeAllHolograms();
            }
        }

        if (this.instance != null) {
            this.instance.disable();
        }
    }

    public @NotNull InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public @NotNull BukkitUserManager getUserManager() {
        return this.userManager;
    }

    public @NotNull CrateManager getCrateManager() {
        return this.crateManager;
    }

    public @NotNull FileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull MetricsManager getMetrics() {
        return this.metrics;
    }

    public @NotNull Timer getTimer() {
        return this.timer;
    }
}