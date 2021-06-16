package me.sthino.items.managers;

import me.sthino.items.items.wands.ThunderWand.ThunderWandItem;
import me.sthino.items.items.stormbreaker.StormBreakerItem;
import me.sthino.items.items.wands.evokerWand.EvokerWandItem;
import org.bukkit.inventory.ItemStack;

public class ItemManager {

    public static ItemStack stormBreaker;
    public static ItemStack thunderWand;
    public static ItemStack evokerWand;

    public static void init() {
        stormBreaker = StormBreakerItem.createStormbreaker();
        thunderWand = ThunderWandItem.createThunderRing();
        evokerWand = EvokerWandItem.createEvokerWand();
    }
}
