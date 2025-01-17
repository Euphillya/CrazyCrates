package com.badbones69.crazycrates.api.builders;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.ryderbelserion.vital.enums.Support;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import java.util.List;
import static java.util.regex.Matcher.quoteReplacement;

@SuppressWarnings("ALL")
public abstract class InventoryBuilder implements InventoryHolder {

    protected final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final Inventory inventory;
    private final Player player;
    private String title;
    private Crate crate;
    private int size;
    private int page;
    private List<Tier> tiers;

    public InventoryBuilder(Player player, int size, String title) {
        this.title = title;
        this.player = player;
        this.size = size;

        String inventoryTitle = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), MsgUtils.color(this.title)) : MsgUtils.color(this.title);

        this.inventory = this.plugin.getServer().createInventory(this, this.size, inventoryTitle);
    }

    public InventoryBuilder(Crate crate, Player player, int size, String title) {
        this.title = title;
        this.player = player;
        this.size = size;

        this.crate = crate;

        String inventoryTitle = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), MsgUtils.color(this.title)) : MsgUtils.color(this.title);

        this.inventory = this.plugin.getServer().createInventory(this, this.size, inventoryTitle);
    }

    public InventoryBuilder(Crate crate, Player player, int size, int page, String title) {
        this.title = title;
        this.player = player;
        this.size = size;
        this.page = page;

        this.crate = crate;

        String inventoryTitle = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), MsgUtils.color(this.title)) : MsgUtils.color(this.title);

        this.inventory = this.plugin.getServer().createInventory(this, this.size, inventoryTitle);
    }

    public InventoryBuilder(List<Tier> tiers, Crate crate, Player player, int size, String title) {
        this.title = title;
        this.player = player;
        this.size = size;

        this.crate = crate;

        this.tiers = tiers;

        String inventoryTitle = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), MsgUtils.color(this.title)) : MsgUtils.color(this.title);

        this.inventory = this.plugin.getServer().createInventory(this, this.size, inventoryTitle);
    }

    public boolean overrideMenu() {
        SettingsManager config = ConfigManager.getConfig();

        if (config.getProperty(ConfigKeys.menu_button_override)) {
            List<String> commands = config.getProperty(ConfigKeys.menu_button_command_list);

            if (!commands.isEmpty()) {
                commands.forEach(value -> {
                    String command = value.replaceAll("%player%", quoteReplacement(player.getName()))
                            .replaceAll("%crate%", quoteReplacement(crate.getName()));

                    MiscUtils.sendCommand(command);
                });

                return true;
            }

            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("The property " + ConfigKeys.menu_button_command_list.getPath() + " is empty so no commands were run.");

            return true;
        }

        return false;
    }

    public abstract InventoryBuilder build();

    public void size(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return this.page;
    }

    public Crate getCrate() {
        return this.crate;
    }

    public void title(String title) {
        this.title = title;
    }

    public boolean contains(String message) {
        return this.title.contains(message);
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<Tier> getTiers() {
        return this.tiers;
    }

    public InventoryView getView() {
        return getPlayer().getOpenInventory();
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }
}