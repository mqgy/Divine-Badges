package dev.mqgy.divinebadges.listener;

import dev.mqgy.divinebadges.DivineBadges;
import dev.mqgy.divinebadges.gui.BadgeAwardMenu;
import dev.mqgy.divinebadges.gui.BadgeCollectionMenu;
import dev.mqgy.divinebadges.gui.PlayerSelectMenu;
import dev.mqgy.divinebadges.model.Badge;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import static dev.mqgy.divinebadges.util.ColorUtil.colorize;

public class MenuListener implements Listener {

    private static final String PREFIX = "&8[&6DivineBadges&8] ";

    private final DivineBadges plugin;

    public MenuListener(DivineBadges plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player))
            return;

        InventoryHolder holder = e.getInventory().getHolder();
        if (holder == null)
            return;

        int slot = e.getRawSlot();

        // collection menu - just browsing, close, and page nav
        if (holder instanceof BadgeCollectionMenu menu) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null)
                return;

            if (slot == 50) {
                player.closeInventory();
                return;
            }

            if (slot == 45 && menu.getCurrentPage() > 0) {
                new BadgeCollectionMenu(plugin, menu.getViewer(), menu.getTarget(),
                        menu.getCurrentPage() - 1).open();
            } else if (slot == 53 && menu.getCurrentPage() < menu.getTotalPages() - 1) {
                new BadgeCollectionMenu(plugin, menu.getViewer(), menu.getTarget(),
                        menu.getCurrentPage() + 1).open();
            }
            return;
        }

        // award menu - click badge to open player select
        if (holder instanceof BadgeAwardMenu menu) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null)
                return;

            if (slot == 50) {
                player.closeInventory();
                return;
            }

            if (slot == 45 && menu.getCurrentPage() > 0) {
                new BadgeAwardMenu(plugin, menu.getLeader(), menu.getCurrentPage() - 1).open();
                return;
            }
            if (slot == 53 && menu.getCurrentPage() < menu.getTotalPages() - 1) {
                new BadgeAwardMenu(plugin, menu.getLeader(), menu.getCurrentPage() + 1).open();
                return;
            }

            Badge badge = menu.getBadgeAtSlot(slot);
            if (badge != null)
                new PlayerSelectMenu(plugin, player, badge).open();
            return;
        }

        // player select - toggle award/revoke
        if (holder instanceof PlayerSelectMenu menu) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null)
                return;

            if (slot == 50) {
                player.closeInventory();
                return;
            }
            if (slot == 46 && e.getCurrentItem().getType() == Material.SPECTRAL_ARROW) {
                new BadgeAwardMenu(plugin, player).open();
                return;
            }

            if (slot == 45 && menu.getCurrentPage() > 0) {
                new PlayerSelectMenu(plugin, menu.getLeader(), menu.getBadge(),
                        menu.getCurrentPage() - 1).open();
                return;
            }
            if (slot == 53 && menu.getCurrentPage() < menu.getTotalPages() - 1) {
                new PlayerSelectMenu(plugin, menu.getLeader(), menu.getBadge(),
                        menu.getCurrentPage() + 1).open();
                return;
            }

            Player target = menu.getPlayerAtSlot(slot);
            if (target == null || !target.isOnline())
                return;

            Badge badge = menu.getBadge();
            if (plugin.getPlayerDataManager().hasBadge(target.getUniqueId(), badge.getId())) {
                plugin.getPlayerDataManager().revokeBadge(target.getUniqueId(), badge.getId());
                player.sendMessage(colorize(PREFIX + "&cRevoked " + badge.getName()
                        + " &cfrom &f" + target.getName() + "&c."));
                target.sendMessage(colorize(PREFIX + "&cYour " + badge.getName() + " &cwas revoked."));
            } else {
                plugin.getPlayerDataManager().awardBadge(target.getUniqueId(), badge.getId());
                player.sendMessage(colorize(PREFIX + "&aGave " + badge.getName()
                        + " &ato &f" + target.getName() + "&a!"));
                target.sendMessage(colorize(PREFIX + "&aYou earned the " + badge.getName() + "&a! \uD83C\uDFC5"));
            }

            // refresh same page
            new PlayerSelectMenu(plugin, menu.getLeader(), badge, menu.getCurrentPage()).open();
        }
    }
}
