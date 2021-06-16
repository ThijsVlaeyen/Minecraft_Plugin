package me.sthino.items.commands;

import me.sthino.items.managers.ItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("stormbreaker")) {
            player.getInventory().addItem(ItemManager.stormBreaker);
        }

        if (cmd.getName().equalsIgnoreCase("thunderwand")) {
            player.getInventory().addItem(ItemManager.thunderWand);
        }

        if (cmd.getName().equalsIgnoreCase("evokerwand")) {
            player.getInventory().addItem(ItemManager.evokerWand);
        }

        return false;
    }
}
