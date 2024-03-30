package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.platform.crates.UserManager;
import com.badbones69.crazycrates.platform.crates.objects.Key;
import com.badbones69.crazycrates.platform.utils.MiscUtils;
import com.badbones69.crazycrates.platform.crates.CrateManager;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WheelCrate extends CrateBuilder {

    private final @NotNull UserManager userManager = null;
    private final @NotNull CrateManager crateManager = null;

    public WheelCrate(Key key, Crate crate, Player player, int size) {
        super(key, crate, player, size);
    }

    private Map<Integer, ItemStack> rewards;

    @Override
    public void open(KeyType keyType, boolean checkHand) {
        if (isCrateEventValid(keyType, checkHand)) {
            return;
        }

        Crate crate = getCrate();
        Key key = getKey();
        Player player = getPlayer();

        // Crate event failed so we return.
        //boolean keyCheck = this.userManager.takeKeys(1, player.getUniqueId(), crate.getName(), key.getName(), true, checkHand);

        if (!true) {

            // Remove from opening list.
            //this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        for (int index = 0; index < getSize(); index ++) {
            setCustomGlassPane(index);
        }

        this.rewards = new HashMap<>();

        for (int number : getBorder()) {
            Prize prize = crate.pickPrize(player);

            setItem(number, prize.getDisplayItem(player));

            this.rewards.put(number, prize.getDisplayItem(player));
        }

        getPlayer().openInventory(getInventory());

        Material material = Material.LIME_STAINED_GLASS_PANE;

        addCrateTask(new BukkitRunnable() {
            final List<Integer> slots = getBorder();

            int uh = 0;
            int what = 17;

            int full = 0;

            final int timer = MiscUtils.randomNumber(42, 68);

            int slower = 0;
            int open = 0;
            int slow = 0;

            @Override
            public void run() {
                if (this.uh >= 18) this.uh = 0;

                if (this.what >= 18) this.what = 0;

                if (this.full < this.timer) {
                    ItemStack itemStack = rewards.get(this.slots.get(this.uh));

                    if (itemStack.hasItemMeta()) {
                        ItemMeta itemMeta = itemStack.getItemMeta();

                        if (itemMeta != null) {
                            boolean hasLore = itemMeta.hasLore();

                            String displayName = itemMeta.getDisplayName();

                            if (hasLore) {
                                setItem(this.slots.get(this.uh), material, displayName, itemMeta.getLore());
                            } else {
                                setItem(this.slots.get(this.uh), material, displayName);
                            }

                            int otherSlot = this.slots.get(this.what);

                            setItem(this.slots.get(this.what), rewards.get(otherSlot));
                        }
                    }

                    playSound("cycle-sound", SoundCategory.MUSIC, "BLOCK_NOTE_BLOCK_XYLOPHONE");

                    this.uh++;
                    this.what++;
                }

                if (this.full >= this.timer) {
                    if (MiscUtils.slowSpin(46, 9).contains(this.slower)) {
                        ItemStack itemStack = rewards.get(this.slots.get(this.uh));

                        if (itemStack.hasItemMeta()) {
                            ItemMeta itemMeta = itemStack.getItemMeta();

                            if (itemMeta != null) {
                                boolean hasLore = itemMeta.hasLore();

                                String displayName = itemMeta.getDisplayName();

                                if (hasLore) {
                                    setItem(this.slots.get(this.uh), material, displayName, itemMeta.getLore());
                                } else {
                                    setItem(this.slots.get(this.uh), material, displayName);
                                }

                                int otherSlot = this.slots.get(this.what);

                                setItem(this.slots.get(this.what), rewards.get(otherSlot));
                            }
                        }

                        playSound("cycle-sound", SoundCategory.MUSIC, "BLOCK_NOTE_BLOCK_XYLOPHONE");

                        this.uh++;
                        this.what++;
                    }

                    if (this.full == this.timer + 47) {
                        playSound("stop-sound", SoundCategory.PLAYERS, "ENTITY_PLAYER_LEVELUP");
                    }

                    if (this.full >= this.timer + 47) {
                        this.slow++;

                        if (this.slow >= 2) {
                            for (int slot = 0; slot < getSize(); slot++) {
                                if (!getBorder().contains(slot)) setCustomGlassPane(slot);
                            }

                            this.slow = 0;
                        }
                    }

                    // Crate is done.
                    if (this.full >= (this.timer + 55 + 47)) {
                        Prize prize = null;

                        //if (crateManager.isInOpeningList(player)) {
                        //    prize = crate.getPrize(rewards.get(this.slots.get(this.what)));
                        //}

                        crate.givePrize(player, prize);

                        playSound("stop-sound", SoundCategory.PLAYERS, "ENTITY_PLAYER_LEVELUP");

                        player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);

                        //crateManager.removePlayerFromOpeningList(player);
                        //crateManager.endActiveTask(player);

                        // Clear it because why not.
                        rewards.clear();
                    }

                    this.slower++;
                }

                this.full++;
                this.open++;

                if (this.open > 5) {
                    player.openInventory(getInventory());
                    this.open = 0;
                }
            }
        }.runTaskTimer(this.plugin, 1, 1));
    }

    private List<Integer> getBorder() {
        return Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 25, 28, 34, 37, 38, 39, 40, 41, 42, 43);
    }

    @Override
    public void run() {

    }
}