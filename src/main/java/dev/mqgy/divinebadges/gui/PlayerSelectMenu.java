package dev.mqgy.divinebadges.gui;

import dev.mqgy.divinebadges.DivineBadges;
import dev.mqgy.divinebadges.model.Badge;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

import static dev.mqgy.divinebadges.util.ColorUtil.colorize;
import static dev.mqgy.divinebadges.util.ColorUtil.makePane;

public class PlayerSelectMenu implements InventoryHolder {

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int PER_PAGE = SLOTS.length;

    private final DivineBadges plugin;
    private final Player leader;
    private final Badge badge;
    private final Inventory inv;
    private final Map<Integer, Player> playerSlots = new HashMap<>();
    private int page, maxPage;

    public PlayerSelectMenu(DivineBadges plugin, Player leader, Badge badge) {
        this(plugin, leader, badge, 0);
    }

    public PlayerSelectMenu(DivineBadges plugin, Player leader, Badge badge, int page) {
        this.plugin = plugin;
        this.leader = leader;
        this.badge = badge;
        this.page = page;
        this.inv = Bukkit.createInventory(this, 54,
                colorize("&8❖ &6&lSelect Player &8- " + badge.getName() + " &8❖"));
        build();
    }

    private void build() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        players.remove(leader);

        maxPage = Math.max(1, (int) Math.ceil((double) players.size() / PER_PAGE));
        page = Math.max(0, Math.min(page, maxPage - 1));

        int from = page * PER_PAGE;
        int to = Math.min(from + PER_PAGE, players.size());

        for (int i = 0; i < 54; i++)
            inv.setItem(i, makePane(Material.PINK_STAINED_GLASS_PANE));
        for (int s : SLOTS)
            inv.setItem(s, null);

        // player heads
        playerSlots.clear();
        for (int i = from; i < to; i++) {
            Player p = players.get(i);
            boolean has = plugin.getPlayerDataManager().hasBadge(p.getUniqueId(), badge.getId());

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sm = (SkullMeta) head.getItemMeta();
            sm.setOwningPlayer(p);
            sm.setDisplayName(colorize("&f&l" + p.getName()));

            if (has) {
                sm.setLore(List.of("", colorize("&a✔ Has this badge"), "",
                        colorize("&7Click to &crevoke&7.")));
            } else {
                sm.setLore(List.of("", colorize("&c✘ Missing this badge"), "",
                        colorize("&7Click to &aaward&7.")));
            }

            head.setItemMeta(sm);
            int slot = SLOTS[i - from];
            inv.setItem(slot, head);
            playerSlots.put(slot, p);
        }

        // bottom row
        if (page > 0) {
            setItem(45, Material.ARROW, "&e&l← Previous Page");
        }

        // back to badge select
        setItem(46, Material.SPECTRAL_ARROW, "&e&l← Back to Badges");

        ItemStack pgInfo = new ItemStack(Material.PAPER);
        ItemMeta pgMeta = pgInfo.getItemMeta();
        pgMeta.setDisplayName(colorize("&fPage " + (page + 1) + "&7/&f" + maxPage));
        pgInfo.setItemMeta(pgMeta);
        inv.setItem(49, pgInfo);

        setItem(50, Material.BARRIER, "&c&lClose");

        if (page < maxPage - 1) {
            setItem(53, Material.ARROW, "&e&lNext Page →");
        }
    }

    private void setItem(int slot, Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize(name));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public Badge getBadge() {
        return badge;
    }

    public Player getPlayerAtSlot(int slot) {
        return playerSlots.get(slot);
    }

    public int getCurrentPage() {
        return page;
    }

    public int getTotalPages() {
        return maxPage;
    }

    public Player getLeader() {
        return leader;
    }

    public void open() {
        leader.openInventory(inv);
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
