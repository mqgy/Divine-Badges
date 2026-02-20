package dev.mqgy.divinebadges.manager;

import dev.mqgy.divinebadges.DivineBadges;
import dev.mqgy.divinebadges.model.Badge;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class BadgeManager {

    private final DivineBadges plugin;
    private final Map<String, Badge> badges = new LinkedHashMap<>();
    private File badgesFile;
    private YamlConfiguration badgesConfig;

    public BadgeManager(DivineBadges plugin) {
        this.plugin = plugin;
    }

    public void loadBadges() {
        badges.clear();
        badgesFile = new File(plugin.getDataFolder(), "badges.yml");

        if (!badgesFile.exists())
            plugin.saveResource("badges.yml", false);

        badgesConfig = YamlConfiguration.loadConfiguration(badgesFile);
        ConfigurationSection section = badgesConfig.getConfigurationSection("badges");

        if (section == null) {
            plugin.getLogger().warning("No 'badges' section in badges.yml");
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection bs = section.getConfigurationSection(key);
            if (bs == null)
                continue;

            String name = bs.getString("name", "&f" + key);
            Material mat = Material.matchMaterial(bs.getString("material", "PAPER"));
            if (mat == null) {
                plugin.getLogger().warning("Bad material for badge '" + key + "', defaulting to PAPER");
                mat = Material.PAPER;
            }

            Badge badge = new Badge(key, name, mat, bs.getStringList("lore"), bs.getInt("slot", -1));

            for (String uuidStr : bs.getStringList("leaders")) {
                try {
                    badge.addLeader(UUID.fromString(uuidStr));
                } catch (IllegalArgumentException ignored) {
                    plugin.getLogger().warning("Bad UUID in leaders for badge '" + key + "'");
                }
            }

            badges.put(key.toLowerCase(), badge);
        }

        plugin.getLogger().info("Loaded " + badges.size() + " badge(s)");
    }

    public void saveBadges() {
        badgesConfig.set("badges", null);

        for (var entry : badges.entrySet()) {
            String path = "badges." + entry.getKey();
            Badge b = entry.getValue();

            badgesConfig.set(path + ".name", b.getName());
            badgesConfig.set(path + ".material", b.getMaterial().name());
            badgesConfig.set(path + ".lore", b.getLore());
            badgesConfig.set(path + ".slot", b.getSlot());

            List<String> leaderUuids = new ArrayList<>();
            for (UUID uuid : b.getLeaders())
                leaderUuids.add(uuid.toString());
            if (!leaderUuids.isEmpty())
                badgesConfig.set(path + ".leaders", leaderUuids);
        }

        try {
            badgesConfig.save(badgesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save badges.yml", e);
        }
    }

    public boolean createBadge(String id, String name, Material material) {
        String key = id.toLowerCase();
        if (badges.containsKey(key))
            return false;

        List<String> lore = new ArrayList<>();
        lore.add("&7Awarded for beating the " + id.substring(0, 1).toUpperCase() + id.substring(1) + " Gym.");

        Badge badge = new Badge(key, name, material, lore, badges.size());
        badges.put(key, badge);
        saveBadges();
        return true;
    }

    public boolean deleteBadge(String id) {
        if (badges.remove(id.toLowerCase()) != null) {
            saveBadges();
            return true;
        }
        return false;
    }

    public Badge getBadge(String id) {
        return badges.get(id.toLowerCase());
    }

    public Map<String, Badge> getBadges() {
        return Collections.unmodifiableMap(badges);
    }

    public List<Badge> getBadgesForLeader(UUID uuid) {
        List<Badge> result = new ArrayList<>();
        for (Badge b : badges.values()) {
            if (b.isLeader(uuid))
                result.add(b);
        }
        return result;
    }

    public List<String> getBadgeIds() {
        return new ArrayList<>(badges.keySet());
    }
}
