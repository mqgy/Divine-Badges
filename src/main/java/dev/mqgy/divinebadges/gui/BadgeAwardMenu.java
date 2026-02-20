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

import java.util.*;

import static dev.mqgy.divinebadges.util.ColorUtil.colorize;
import static dev.mqgy.divinebadges.util.ColorUtil.makePane;

public class BadgeAwardMenu implements InventoryHolder {

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int PER_PAGE = SLOTS.length;

    private final DivineBadges plugin;
    private final Player leader;
    private final Inventory inv;
    private final Map<Integer, Badge> slotMap = new HashMap<>();
    private int page, maxPage;

    public BadgeAwardMenu(DivineBadges plugin, Player leader) {
        this(plugin, leader, 0);
    }

    public BadgeAwardMenu(DivineBadges plugin, Player leader, int page) {
        this.plugin = plugin;
        this.leader = leader;
        this.page = page;
        this.inv = Bukkit.createInventory(this, 54, colorize("&8❖ &6&lAward a Badge &8❖"));
        build();
    }

    private void build() {
        boolean admin = leader.hasPermission("divinebadges.admin");
        List<Badge> badges = admin
                ? new ArrayList<>(plugin.getBadgeManager().getBadges().values())
                : plugin.getBadgeManager().getBadgesForLeader(leader.getUniqueId());

        maxPage = Math.max(1, (int) Math.ceil((double) badges.size() / PER_PAGE));
        page = Math.max(0, Math.min(page, maxPage - 1));

        int from = page * PER_PAGE;
        int to = Math.min(from + PER_PAGE, badges.size());

        for (int i = 0; i < 54; i++)
            inv.setItem(i, makePane(Material.PINK_STAINED_GLASS_PANE));
        for (int s : SLOTS)
            inv.setItem(s, null);

        // info sign at top
        ItemStack sign = new ItemStack(Material.OAK_SIGN);
        ItemMeta signMeta = sign.getItemMeta();
        signMeta.setDisplayName(colorize("&e&lHow to Award"));
        signMeta.setLore(List.of("", colorize("&7Click a badge, then"), colorize("&7pick a player."), ""));
        sign.setItemMeta(signMeta);
        inv.setItem(4, sign);

        slotMap.clear();
        for (int i = from; i < to; i++) {
            Badge badge = badges.get(i);
            ItemStack item = new ItemStack(badge.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(colorize(badge.getName()));

            List<String> lore = new ArrayList<>();
            lore.add("");
            for (String line : badge.getLore())
                lore.add(colorize(line));
            lore.add("");
            lore.add(colorize("&eClick to award!"));
            meta.setLore(lore);
            item.setItemMeta(meta);

            int slot = SLOTS[i - from];
            inv.setItem(slot, item);
            slotMap.put(slot, badge);
        }

        // bottom nav
        if (page > 0)
            putBtn(45, Material.ARROW, "&e&l← Previous Page");

        ItemStack pg = new ItemStack(Material.PAPER);
        ItemMeta pgm = pg.getItemMeta();
        pgm.setDisplayName(colorize("&fPage " + (page + 1) + "&7/&f" + maxPage));
        pg.setItemMeta(pgm);
        inv.setItem(49, pg);

        putBtn(50, Material.BARRIER, "&c&lClose");

        if (page < maxPage - 1)
            putBtn(53, Material.ARROW, "&e&lNext Page →");
    }

    private void putBtn(int slot, Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize(name));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public Badge getBadgeAtSlot(int slot) {
        return slotMap.get(slot);
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
