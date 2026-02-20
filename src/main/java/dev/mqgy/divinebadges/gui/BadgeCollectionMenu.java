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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.enchantments.Enchantment;


import java.util.*;

import static dev.mqgy.divinebadges.util.ColorUtil.colorize;
import static dev.mqgy.divinebadges.util.ColorUtil.makePane;

public class BadgeCollectionMenu implements InventoryHolder {

    // 7 per row across 4 content rows
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int PER_PAGE = SLOTS.length;

    private final DivineBadges plugin;
    private final Player viewer;
    private final Player target;
    private final Inventory inv;
    private int page;
    private int maxPage;

    public BadgeCollectionMenu(DivineBadges plugin, Player viewer, Player target) {
        this(plugin, viewer, target, 0);
    }

    public BadgeCollectionMenu(DivineBadges plugin, Player viewer, Player target, int page) {
        this.plugin = plugin;
        this.viewer = viewer;
        this.target = target;
        this.page = page;

        String title = viewer.equals(target)
                ? colorize("&8❖ &6&lYour Gym Badges &8❖")
                : colorize("&8❖ &6&l" + target.getName() + "'s Badges &8❖");

        this.inv = Bukkit.createInventory(this, 54, title);
        build();
    }

    private void build() {
        List<Badge> allBadges = new ArrayList<>(plugin.getBadgeManager().getBadges().values());
        Set<String> earned = plugin.getPlayerDataManager().getPlayerBadges(target.getUniqueId());

        this.maxPage = Math.max(1, (int) Math.ceil((double) allBadges.size() / PER_PAGE));
        page = Math.max(0, Math.min(page, maxPage - 1));

        int from = page * PER_PAGE;
        int to = Math.min(from + PER_PAGE, allBadges.size());

        // fill border
        for (int i = 0; i < 54; i++)
            inv.setItem(i, makePane(Material.PINK_STAINED_GLASS_PANE));

        for (int s : SLOTS)
            inv.setItem(s, null);

        // count earned across all pages for the stats
        int total = allBadges.size();
        int earnedCount = 0;
        for (Badge b : allBadges)
            if (earned.contains(b.getId()))
                earnedCount++;

        // place badges
        for (int i = from; i < to; i++) {
            Badge badge = allBadges.get(i);
            boolean has = earned.contains(badge.getId());

            ItemStack item;
            if (has) {
                item = new ItemStack(badge.getMaterial());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(colorize(badge.getName()));
                List<String> lore = new ArrayList<>();
                lore.add("");
                for (String l : badge.getLore())
                    lore.add(colorize(l));
                lore.add("");
                lore.add(colorize("&a✔ Earned!"));
                meta.setLore(lore);
                meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            } else {
                item = new ItemStack(Material.GRAY_DYE);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(colorize("&8&l" + badge.getName()));
                meta.setLore(List.of("", colorize("&7Not earned yet."),
                        colorize("&7Defeat the gym leader!"), "", colorize("&c✘ Not Earned")));
                item.setItemMeta(meta);
            }
            inv.setItem(SLOTS[i - from], item);
        }

        // nav buttons
        if (page > 0)
            setNavItem(45, Material.ARROW, "&e&l← Previous Page");
        setStatsItem(48, earnedCount, total);
        setPageItem(49, page, maxPage, from + 1, to, total);
        setNavItem(50, Material.BARRIER, "&c&lClose");
        if (page < maxPage - 1)
            setNavItem(53, Material.ARROW, "&e&lNext Page →");
    }

    private void setNavItem(int slot, Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize(name));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    private void setPageItem(int slot, int pg, int max, int from, int to, int total) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize("&fPage " + (pg + 1) + "&7/&f" + max));
        meta.setLore(List.of("", colorize("&7" + from + "-" + to + " of " + total)));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    private void setStatsItem(int slot, int earnedCount, int total) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize("&6&lBadge Progress"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(colorize("&7Earned: &f" + earnedCount + "&7/&f" + total));
        lore.add("");

        // progress bar
        int barLen = 20;
        int filled = total > 0 ? (int) Math.round((double) earnedCount / total * barLen) : 0;
        StringBuilder bar = new StringBuilder("&a");
        for (int i = 0; i < barLen; i++) {
            if (i == filled)
                bar.append("&7");
            bar.append("▌");
        }
        int pct = total > 0 ? (int) Math.round((double) earnedCount / total * 100) : 0;
        lore.add(colorize(bar + " &f" + pct + "%"));
        lore.add("");
        lore.add(earnedCount == total && total > 0
                ? colorize("&6★ &e&lChampion! &6★")
                : colorize("&7Keep battling!"));

        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public void open() {
        viewer.openInventory(inv);
    }

    public int getCurrentPage() {
        return page;
    }

    public int getTotalPages() {
        return maxPage;
    }

    public Player getViewer() {
        return viewer;
    }

    public Player getTarget() {
        return target;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
