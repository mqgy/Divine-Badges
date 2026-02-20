package dev.mqgy.divinebadges.model;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Badge {

    private final String id;
    private String name;
    private Material material;
    private List<String> lore;
    private int slot;
    private final List<UUID> leaders = new ArrayList<>();

    public Badge(String id, String name, Material material, List<String> lore, int slot) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.lore = lore != null ? lore : new ArrayList<>();
        this.slot = slot;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public List<UUID> getLeaders() {
        return leaders;
    }

    public void addLeader(UUID uuid) {
        if (!leaders.contains(uuid))
            leaders.add(uuid);
    }

    public void removeLeader(UUID uuid) {
        leaders.remove(uuid);
    }

    public boolean isLeader(UUID uuid) {
        return leaders.contains(uuid);
    }
}
