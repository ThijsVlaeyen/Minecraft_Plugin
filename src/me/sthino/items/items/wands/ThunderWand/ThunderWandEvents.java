package me.sthino.items.items.wands.ThunderWand;

import me.sthino.items.managers.CooldownManager;
import me.sthino.items.managers.ItemManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class ThunderWandEvents implements Listener {

    private static final int RANGE = 5;
    private static final int TIMEOUT = 20;

    @EventHandler
    public static void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (event.getItem() == null)
            return;

        if (!event.getItem().getItemMeta().hasCustomModelData())
            return;

        if (event.getItem().getItemMeta().getCustomModelData() != ItemManager.thunderWand.getItemMeta().getCustomModelData())
            return;

        Player player = event.getPlayer();
        List<Block> blocks = getNearbyBlocks(player.getLocation());

        // Check if player is on cooldown
        if (CooldownManager.checkCooldowns(player, event.getItem())) {
            CooldownManager.setCooldowns(player, event.getItem(), TIMEOUT);

            for (Entity n: findEntities(blocks, player)) {
                player.getWorld().strikeLightning(n.getLocation());
            }
        }else {
            player.sendMessage("§4You are too tired to use this again (" + CooldownManager.getCooldowns(player, event.getItem()) + "s).");
        }
    }

    public static List<Block> getNearbyBlocks(Location location) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - RANGE; x <= location.getBlockX() + RANGE; x++) {
            for(int y = location.getBlockY() - RANGE; y <= location.getBlockY() + RANGE; y++) {
                for(int z = location.getBlockZ() - RANGE; z <= location.getBlockZ() + RANGE; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR) {
                        blocks.add(block);
                    }
                }
            }
        }
        return blocks;
    }

    //==================//
    // HELPER FUNCTIONS //
    //==================//

    // Finds all unique living entities in given List<Blocks>
    public static List<Entity> findEntities(List<Block> blocks, Player player) {
        Map<UUID, Entity> entities = new HashMap<>();

        for (Block block: blocks) {
            for (Entity n: block.getChunk().getEntities()) {
                if (n instanceof LivingEntity && n.getType() != EntityType.ARMOR_STAND
                        && !n.getUniqueId().equals(player.getUniqueId())
                ) {
                    entities.put(n.getUniqueId(), n);
                }
            }
        }

        return new ArrayList<Entity>(entities.values());
    }
}
