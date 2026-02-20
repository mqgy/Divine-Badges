package dev.mqgy.divinebadges;

import dev.mqgy.divinebadges.command.BadgeCommand;
import dev.mqgy.divinebadges.command.BadgesCommand;
import dev.mqgy.divinebadges.listener.MenuListener;
import dev.mqgy.divinebadges.manager.BadgeManager;
import dev.mqgy.divinebadges.manager.PlayerDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class DivineBadges extends JavaPlugin {

	private static DivineBadges instance;
	private BadgeManager badgeManager;
	private PlayerDataManager playerDataManager;

	@Override
	public void onEnable() {
		instance = this;
		saveResource("badges.yml", false);

		badgeManager = new BadgeManager(this);
		badgeManager.loadBadges();

		playerDataManager = new PlayerDataManager(this);
		playerDataManager.loadAllData();

		BadgeCommand badgeCmd = new BadgeCommand(this);
		getCommand("badges").setExecutor(new BadgesCommand(this));
		getCommand("badge").setExecutor(badgeCmd);
		getCommand("badge").setTabCompleter(badgeCmd);

		getServer().getPluginManager().registerEvents(new MenuListener(this), this);
		getLogger().info("DivineBadges enabled! " + badgeManager.getBadges().size() + " badges loaded.");
	}

	@Override
	public void onDisable() {
		if (playerDataManager != null)
			playerDataManager.saveAllData();
	}

	public static DivineBadges getInstance() {
		return instance;
	}

	public BadgeManager getBadgeManager() {
		return badgeManager;
	}

	public PlayerDataManager getPlayerDataManager() {
		return playerDataManager;
	}
}
