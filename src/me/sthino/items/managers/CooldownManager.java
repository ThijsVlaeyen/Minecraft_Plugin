package me.sthino.items.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CooldownManager {

    // Player id
    public static HashMap<CompositeKey, Double> cooldowns = new HashMap<>();

    public static void setCooldowns(Player player, ItemStack item, int seconds) {
        double delay = System.currentTimeMillis() + (seconds * 1000.0);
        CompositeKey c = new CompositeKey(player.getUniqueId(), item.getItemMeta().getCustomModelData());
        cooldowns.put(c , delay);
    }

    public static double getCooldowns(Player player, ItemStack item) {
        CompositeKey c = new CompositeKey(player.getUniqueId(), item.getItemMeta().getCustomModelData());
        return Math.ceil((cooldowns.get(c) - System.currentTimeMillis()) / 1000);
    }

    public static boolean checkCooldowns(Player player, ItemStack item) {
        CompositeKey c = new CompositeKey(player.getUniqueId(), item.getItemMeta().getCustomModelData());
        return !cooldowns.containsKey(c) || cooldowns.get(c) <= System.currentTimeMillis();
    }

}
