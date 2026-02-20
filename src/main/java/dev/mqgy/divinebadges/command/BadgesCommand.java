package dev.mqgy.divinebadges.command;

import dev.mqgy.divinebadges.DivineBadges;
import dev.mqgy.divinebadges.gui.BadgeCollectionMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static dev.mqgy.divinebadges.util.ColorUtil.colorize;

public class BadgesCommand implements CommandExecutor {

    private final DivineBadges plugin;

    public BadgesCommand(DivineBadges plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(colorize("&cPlayers only."));
            return true;
        }

        if (args.length >= 1) {
            if (!player.hasPermission("divinebadges.admin")) {
                player.sendMessage(colorize("&8[&6DivineBadges&8] &cNo permission."));
                return true;
            }
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(colorize("&8[&6DivineBadges&8] &cPlayer not found."));
                return true;
            }
            new BadgeCollectionMenu(plugin, player, target).open();
            return true;
        }

        new BadgeCollectionMenu(plugin, player, player).open();
        return true;
    }
}
