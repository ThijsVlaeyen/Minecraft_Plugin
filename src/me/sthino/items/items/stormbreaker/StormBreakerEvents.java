package me.sthino.items.items.stormbreaker;

import me.sthino.items.Main;
import me.sthino.items.managers.CooldownManager;
import me.sthino.items.managers.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.*;

public class StormBreakerEvents implements Listener {
    private static final float STRENGTH = 2.2F;
    private static final float HEIGHT = 0.9F;
    private static final float DAMAGE = 6;
    private static final float MIN_PLAYER_DISTANCE = 0.2F;
    private static final float MAX_GROUND_DISTANCE = 3.5F;
    private static final int RANGE = 20;
    private static final int TIMEOUT = 20;

    // Checks if the player right clicked with Stormbreaker
    @EventHandler
    public static void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (event.getItem() == null)
            return;

        if (!event.getItem().getItemMeta().hasCustomModelData())
            return;

        if (event.getItem().getItemMeta().getCustomModelData() != ItemManager.stormBreaker.getItemMeta().getCustomModelData())
            return;

        Player player = event.getPlayer();

        // Check if player is on cooldown
        if (CooldownManager.checkCooldowns(player, event.getItem())) {
            CooldownManager.setCooldowns(player, event.getItem(), TIMEOUT);

            List<Block> blocks = player.getLineOfSight(null, RANGE);

            // Skip the first two, too close to the player.
            for (int i = 2; i < blocks.size(); i++) {
                Block ground = findGround(blocks.get(i));
                Location groundLocation = ground.getLocation();
                Set<UUID> pushedEntities = new HashSet<>();

                ground.getWorld().playEffect(groundLocation, Effect.STEP_SOUND, ground.getType());

                // Check if they have room above.
                Block blockAbove = ground.getRelative(BlockFace.UP);

                if (blockAbove.getType().isAir()) {
                    createJumpingBlock(ground, i);
                }
            }

            for (Entity n : findEntities(blocks, player)) {
                pushEntity(player, n);
            }
        } else {
            player.sendMessage("§4You are too tired to use this again (" + CooldownManager.getCooldowns(player, event.getItem()) + "s).");
        }
    }

    //Destroys falling block entity when they touch the ground
    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent e) {
        if (e.getEntity().getType() == EntityType.FALLING_BLOCK && e.getEntity().hasMetadata("stormbreaker")) {
            e.setCancelled(true);
            e.getEntity().removeMetadata("stormbreaker", Main.instance());
            e.getEntity().remove();
        }
    }

    //==================//
    // HELPER FUNCTIONS //
    //==================//

    // Shoots a block in the air
    private static void createJumpingBlock(Block ground, int index) {
        Location loc = ground.getRelative(BlockFace.UP).getLocation().add(0.5, 0.0, 0.5);
        FallingBlock block = ground.getWorld().spawnFallingBlock(loc, ground.getBlockData());
        block.setDropItem(false);
        block.setVelocity(new Vector(0, 0.4 + index * 0.01, 0));
        block.setMetadata("stormbreaker", new FixedMetadataValue(Main.instance(), "fake_block"));
    }

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

    // Checks if the player can reach
    private static boolean canReach(Location playerLocation, Location entityLocation, Location groundLocation) {
        // Too far away from ground
        double maxGroundDistanceSquared = MAX_GROUND_DISTANCE * MAX_GROUND_DISTANCE;

        // Too close to Player, knockback may be NaN.
        double minPlayerDistanceSquared = MIN_PLAYER_DISTANCE * MIN_PLAYER_DISTANCE;

        return entityLocation.distanceSquared(groundLocation) < maxGroundDistanceSquared
                && playerLocation.distanceSquared(entityLocation) > minPlayerDistanceSquared;
    }

    // Pushes the entities in away except armor stand to prevent griefing
    private static void pushEntity(Player p, Entity entity) {
        // Only damage players when PVP is enabled, other entities are fine.
        if (entity.getType() != EntityType.PLAYER || p.getWorld().getPVP()) {
            EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(p, entity, EntityDamageEvent.DamageCause.ENTITY_ATTACK, DAMAGE);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                Vector vector = entity.getLocation().toVector().subtract(p.getLocation().toVector()).normalize();
                vector.multiply(STRENGTH);
                vector.setY(HEIGHT);

                try {
                    entity.setVelocity(vector);
                    p.getWorld().strikeLightning(entity.getLocation());
                } catch (IllegalArgumentException x) {
                    p.sendMessage("§4Exception while trying to set velocity: " + vector);
                }

                ((LivingEntity) entity).damage(event.getDamage());
            }
        }
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
