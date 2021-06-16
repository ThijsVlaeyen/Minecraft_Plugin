package me.sthino.items.items.wands.evokerWand;

import me.sthino.items.managers.CooldownManager;
import me.sthino.items.managers.ItemManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class EvokerWandEvents implements Listener {

    private static final int RANGE = 12;
    private static final int TIMEOUT = 20;

    @EventHandler
    public static void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (event.getItem() == null)
            return;

        if (!event.getItem().getItemMeta().hasCustomModelData())
            return;

        if (event.getItem().getItemMeta().getCustomModelData() != ItemManager.evokerWand.getItemMeta().getCustomModelData())
            return;

        Player player = event.getPlayer();
        List<Block> blocks = player.getLineOfSight(null, RANGE);

        // Check if player is on cooldown
        if (CooldownManager.checkCooldowns(player, event.getItem())) {
            CooldownManager.setCooldowns(player, event.getItem(), TIMEOUT);

            // Skip the first two, too close to the player.
            for (int i = 2; i < blocks.size(); i++) {
                Block ground = findGround(blocks.get(i));
                Location groundLocation = ground.getLocation();

                // Check if they have room above.
                Block blockAbove = ground.getRelative(BlockFace.UP);

                if (blockAbove.getType().isAir()) {
                    player.getWorld().spawnEntity(ground.getLocation().add(0, 1, 0), EntityType.EVOKER_FANGS);
                }
            }

        }else {
            player.sendMessage("§4You are too tired to use this again (" + CooldownManager.getCooldowns(player, event.getItem()) + "s).");
        }
    }

    //==================//
    // HELPER FUNCTIONS //
    //==================//

    // Finds the ground
    private static Block findGround( Block b) {
        if (b.getType() == Material.AIR) {
            int minHeight = b.getWorld().getMinHeight();
            for (int y = 0; b.getY() - y > minHeight; y++) {
                Block block = b.getRelative(0, -y, 0);

                if (block.getType() != Material.AIR) {
                    return block;
                }
            }
        }

        return b;
    }

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
