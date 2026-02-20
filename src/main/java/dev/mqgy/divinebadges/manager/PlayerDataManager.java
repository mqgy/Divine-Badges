package dev.mqgy.divinebadges.manager;

import dev.mqgy.divinebadges.DivineBadges;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class PlayerDataManager {

    private final DivineBadges plugin;
    private File dataFile;
    private YamlConfiguration dataConfig;
    private final Map<UUID, Set<String>> playerBadges = new HashMap<>();

    public PlayerDataManager(DivineBadges plugin) {
        this.plugin = plugin;
    }

    public void loadAllData() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create playerdata.yml", e);
                return;
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection section = dataConfig.getConfigurationSection("players");
        if (section == null)
            return;

        for (String uuidStr : section.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                List<String> list = section.getStringList(uuidStr + ".badges");
                playerBadges.put(uuid, new LinkedHashSet<>(list));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in playerdata: " + uuidStr);
            }
        }

        plugin.getLogger().info("Loaded data for " + playerBadges.size() + " player(s)");
    }

    public void saveAllData() {
        if (dataConfig == null)
            return;

        dataConfig.set("players", null);
        for (var entry : playerBadges.entrySet()) {
            dataConfig.set("players." + entry.getKey() + ".badges", new ArrayList<>(entry.getValue()));
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save playerdata.yml", e);
        }
    }

    public boolean awardBadge(UUID uuid, String badgeId) {
        boolean added = playerBadges
                .computeIfAbsent(uuid, k -> new LinkedHashSet<>())
                .add(badgeId.toLowerCase());
        if (added)
            saveAllData();
        return added;
    }

    public boolean revokeBadge(UUID uuid, String badgeId) {
        Set<String> badges = playerBadges.get(uuid);
        if (badges == null)
            return false;

        boolean removed = badges.remove(badgeId.toLowerCase());
        if (removed)
            saveAllData();
        return removed;
    }

    public boolean hasBadge(UUID uuid, String badgeId) {
        Set<String> badges = playerBadges.get(uuid);
        return badges != null && badges.contains(badgeId.toLowerCase());
    }

    public Set<String> getPlayerBadges(UUID uuid) {
        return playerBadges.getOrDefault(uuid, Collections.emptySet());
    }

    public int getBadgeCount(UUID uuid) {
        Set<String> badges = playerBadges.get(uuid);
        return badges != null ? badges.size() : 0;
    }
}
