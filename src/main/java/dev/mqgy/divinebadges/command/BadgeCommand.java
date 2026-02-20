package dev.mqgy.divinebadges.command;

import dev.mqgy.divinebadges.DivineBadges;
import dev.mqgy.divinebadges.gui.BadgeAwardMenu;
import dev.mqgy.divinebadges.manager.BadgeManager;
import dev.mqgy.divinebadges.manager.PlayerDataManager;
import dev.mqgy.divinebadges.model.Badge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.Sound;

import java.util.*;
import java.util.stream.Collectors;

import static dev.mqgy.divinebadges.util.ColorUtil.colorize;

public class BadgeCommand implements CommandExecutor, TabCompleter {

    private static final String PREFIX = "&8[&6DivineBadges&8] ";
    private final DivineBadges plugin;

    public BadgeCommand(DivineBadges plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(sender, args);
            case "delete" -> handleDelete(sender, args);
            case "list" -> handleList(sender);
            case "give" -> handleGive(sender, args);
            case "revoke" -> handleRevoke(sender, args);
            case "setleader" -> handleSetLeader(sender, args);
            case "removeleader" -> handleRemoveLeader(sender, args);
            case "award" -> handleAward(sender);
            case "reload" -> handleReload(sender);
            default -> sendHelp(sender);
        }
        return true;
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("divinebadges.admin")) {
            sender.sendMessage(colorize(PREFIX + "&cNo permission."));
            return;
        }
        if (args.length < 4) {
            sender.sendMessage(colorize(PREFIX + "&cUsage: /badge create <id> <material> &ColourCode<name...>"));
            sender.sendMessage(colorize(PREFIX + "&7Example: /badge create ice PACKED_ICE &3Ice Badge"));
            return;
        }

        String id = args[1].toLowerCase();

        // numeric IDs break yaml reload (int vs string key mismatch)
        if (id.matches("\\d+")) {
            sender.sendMessage(colorize(PREFIX + "&cID can't be purely numeric."));
            sender.sendMessage(colorize(PREFIX + "&7Try '&fbadge" + id + "&7' instead."));
            return;
        }

        Material mat = Material.matchMaterial(args[2].toUpperCase());
        if (mat == null) {
            sender.sendMessage(colorize(PREFIX + "&cInvalid material: " + args[2]));
            return;
        }

        StringBuilder name = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            if (i > 3)
                name.append(" ");
            name.append(args[i]);
        }

        if (plugin.getBadgeManager().createBadge(id, name.toString(), mat)) {
            sender.sendMessage(colorize(PREFIX + "&aCreated badge '&f" + name + "&a' (id: &f" + id + "&a)"));
        } else {
            sender.sendMessage(colorize(PREFIX + "&cBadge '&f" + id + "&c' already exists."));
        }
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("divinebadges.admin")) {
            sender.sendMessage(colorize(PREFIX + "&cNo permission."));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(colorize(PREFIX + "&cUsage: /badge delete <id>"));
            return;
        }

        String id = args[1].toLowerCase();
        if (plugin.getBadgeManager().deleteBadge(id)) {
            sender.sendMessage(colorize(PREFIX + "&aDeleted badge '&f" + id + "&a'."));
        } else {
            sender.sendMessage(colorize(PREFIX + "&cNo badge with id '&f" + id + "&c'."));
        }
    }

    private void handleList(CommandSender sender) {
        if (!sender.hasPermission("divinebadges.admin")) {
            sender.sendMessage(colorize(PREFIX + "&cNo permission."));
            return;
        }

        Map<String, Badge> badges = plugin.getBadgeManager().getBadges();
        if (badges.isEmpty()) {
            sender.sendMessage(colorize(PREFIX + "&7No badges exist yet."));
            return;
        }

        sender.sendMessage(colorize("&8&m                                                  "));
        sender.sendMessage(colorize("&6&lDivineBadges &8- &fBadge List &7(" + badges.size() + ")"));
        sender.sendMessage(colorize("&8&m                                                  "));

        for (Badge b : badges.values()) {
            sender.sendMessage(colorize(" &8▸ &f" + b.getId() + " &8| " + b.getName()
                    + " &8| &7" + b.getMaterial().name()
                    + " &8| &7Leaders: &f" + b.getLeaders().size()));
        }
        sender.sendMessage(colorize("&8&m                                                  "));
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("divinebadges.admin")) {
            sender.sendMessage(colorize(PREFIX + "&cNo permission."));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(colorize(PREFIX + "&cUsage: /badge give <player> <badgeId>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(colorize(PREFIX + "&c" + args[1] + " is not online."));
            return;
        }

        String id = args[2].toLowerCase();
        Badge badge = plugin.getBadgeManager().getBadge(id);
        if (badge == null) {
            sender.sendMessage(colorize(PREFIX + "&cBadge '&f" + id + "&c' doesn't exist."));
            return;
        }

        if (plugin.getPlayerDataManager().awardBadge(target.getUniqueId(), id)) {
            sender.sendMessage(colorize(PREFIX + "&aGave " + badge.getName() + " &ato &f" + target.getName()));
            target.sendMessage(colorize(PREFIX + "&aYou received the " + badge.getName() + "&a!"));
            target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        } else {
            sender.sendMessage(colorize(PREFIX + "&c" + target.getName() + " already has that."));
        }
    }

    private void handleRevoke(CommandSender sender, String[] args) {
        if (!sender.hasPermission("divinebadges.admin")) {
            sender.sendMessage(colorize(PREFIX + "&cNo permission."));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(colorize(PREFIX + "&cUsage: /badge revoke <player> <badgeId>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(colorize(PREFIX + "&c" + args[1] + " is not online."));
            return;
        }

        String id = args[2].toLowerCase();
        Badge badge = plugin.getBadgeManager().getBadge(id);
        if (badge == null) {
            sender.sendMessage(colorize(PREFIX + "&cBadge '&f" + id + "&c' doesn't exist."));
            return;
        }

        if (plugin.getPlayerDataManager().revokeBadge(target.getUniqueId(), id)) {
            sender.sendMessage(colorize(PREFIX + "&aRevoked " + badge.getName() + " &afrom &f" + target.getName()));
            target.sendMessage(colorize(PREFIX + "&cYour " + badge.getName() + " &cwas revoked."));
        } else {
            sender.sendMessage(colorize(PREFIX + "&c" + target.getName() + " doesn't have that."));
        }
    }

    private void handleSetLeader(CommandSender sender, String[] args) {
        if (!sender.hasPermission("divinebadges.admin")) {
            sender.sendMessage(colorize(PREFIX + "&cNo permission."));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(colorize(PREFIX + "&cUsage: /badge setleader <player> <badgeId>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(colorize(PREFIX + "&c" + args[1] + " is not online."));
            return;
        }

        Badge badge = plugin.getBadgeManager().getBadge(args[2].toLowerCase());
        if (badge == null) {
            sender.sendMessage(colorize(PREFIX + "&cBadge doesn't exist."));
            return;
        }

        badge.addLeader(target.getUniqueId());
        plugin.getBadgeManager().saveBadges();

        sender.sendMessage(colorize(PREFIX + "&a" + target.getName() + " is now a leader for " + badge.getName()));
        target.sendMessage(colorize(PREFIX + "&aYou're now a gym leader for " + badge.getName()
                + "&a! Use &f/badge award &ato give badges."));
    }

    private void handleRemoveLeader(CommandSender sender, String[] args) {
        if (!sender.hasPermission("divinebadges.admin")) {
            sender.sendMessage(colorize(PREFIX + "&cNo permission."));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(colorize(PREFIX + "&cUsage: /badge removeleader <player> <badgeId>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(colorize(PREFIX + "&c" + args[1] + " is not online."));
            return;
        }

        Badge badge = plugin.getBadgeManager().getBadge(args[2].toLowerCase());
        if (badge == null) {
            sender.sendMessage(colorize(PREFIX + "&cBadge doesn't exist."));
            return;
        }

        badge.removeLeader(target.getUniqueId());
        plugin.getBadgeManager().saveBadges();

        sender.sendMessage(colorize(PREFIX + "&a" + target.getName() + " removed as leader for " + badge.getName()));
        target.sendMessage(colorize(PREFIX + "&cYou're no longer a gym leader for " + badge.getName()));
    }

    private void handleAward(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(colorize(PREFIX + "&cPlayers only."));
            return;
        }

        boolean admin = player.hasPermission("divinebadges.admin");
        if (!admin && plugin.getBadgeManager().getBadgesForLeader(player.getUniqueId()).isEmpty()) {
            player.sendMessage(colorize(PREFIX + "&cYou're not a gym leader."));
            return;
        }

        new BadgeAwardMenu(plugin, player).open();
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("divinebadges.admin")) {
            sender.sendMessage(colorize(PREFIX + "&cNo permission."));
            return;
        }
        plugin.getBadgeManager().loadBadges();
        plugin.getPlayerDataManager().loadAllData();
        sender.sendMessage(
                colorize(PREFIX + "&aReloaded! " + plugin.getBadgeManager().getBadges().size() + " badges."));
    }

    private void sendHelp(CommandSender sender) {
        boolean admin = sender.hasPermission("divinebadges.admin");

        sender.sendMessage(colorize("&8&m                                                  "));
        sender.sendMessage(colorize("&6&lDivineBadges &8- &fHelp"));
        sender.sendMessage(colorize("&8&m                                                  "));

        if (admin) {
            sender.sendMessage(colorize(" &6/badge create &f<id> <material> <name...> &8- &7Create new badges"));
            sender.sendMessage(colorize(" &6/badge delete &f<id> &8- &7Delete badges"));
            sender.sendMessage(colorize(" &6/badge list &8- &7List all"));
            sender.sendMessage(colorize(" &6/badge give &f<player> <badge> &8- &7Force give badges"));
            sender.sendMessage(colorize(" &6/badge revoke &f<player> <badge> &8- &7Revoke badges"));
            sender.sendMessage(colorize(" &6/badge setleader &f<player> <badge> &8- &7Set gym leader for given badge"));
            sender.sendMessage(colorize(" &6/badge removeleader &f<player> <badge> &8- &7Remove player as gym leader"));
            sender.sendMessage(colorize(" &6/badge reload &8- &7Reload config"));
        }
        sender.sendMessage(colorize(" &6/badge award &8- &7Award menu (gym leaders)"));
        sender.sendMessage(colorize(" &6/badges &8- &7Your badges"));
        sender.sendMessage(colorize("&8&m                                                  "));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>(List.of("award"));
            if (sender.hasPermission("divinebadges.admin"))
                subs.addAll(
                        List.of("create", "delete", "list", "give", "revoke", "setleader", "removeleader", "reload"));
            return filter(subs, args[0]);
        }

        if (!sender.hasPermission("divinebadges.admin") && !args[0].equalsIgnoreCase("award"))
            return Collections.emptyList();

        String sub = args[0].toLowerCase();

        if (args.length == 2) {
            return switch (sub) {
                case "delete" -> filter(plugin.getBadgeManager().getBadgeIds(), args[1]);
                case "give", "revoke", "setleader", "removeleader" -> null; // default player completion
                case "create" -> List.of("<id>");
                default -> Collections.emptyList();
            };
        }

        if (args.length == 3) {
            return switch (sub) {
                case "give", "revoke", "setleader", "removeleader" ->
                    filter(plugin.getBadgeManager().getBadgeIds(), args[2]);
                case "create" -> filter(
                        Arrays.stream(Material.values()).filter(Material::isItem)
                                .map(Material::name).collect(Collectors.toList()),
                        args[2]);
                default -> Collections.emptyList();
            };
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> opts, String input) {
        String lower = input.toLowerCase();
        return opts.stream().filter(s -> s.toLowerCase().startsWith(lower)).collect(Collectors.toList());
    }
}
