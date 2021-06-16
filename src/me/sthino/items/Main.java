package me.sthino.items;

import me.sthino.items.commands.ItemCommands;
import me.sthino.items.items.wands.ThunderWand.ThunderWandEvents;
import me.sthino.items.items.stormbreaker.StormBreakerEvents;
import me.sthino.items.items.wands.evokerWand.EvokerWandEvents;
import me.sthino.items.managers.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        ItemManager.init();
        setInstance(this);
        setEvents();
        setCommands();
    }

    @Override
    public void onDisable() {

    }

    public static Main instance() {
        return instance;
    }

    private static void setInstance(Main pluginInstance) {
        instance = pluginInstance;
    }

    private void setEvents() {
        getServer().getPluginManager().registerEvents(new StormBreakerEvents(), this);
        getServer().getPluginManager().registerEvents(new ThunderWandEvents(), this);
        getServer().getPluginManager().registerEvents(new EvokerWandEvents(), this);
    }

    private void setCommands() {
        getCommand("stormbreaker").setExecutor(new ItemCommands());
        getCommand("thunderwand").setExecutor(new ItemCommands());
        getCommand("evokerwand").setExecutor(new ItemCommands());
    }
}
