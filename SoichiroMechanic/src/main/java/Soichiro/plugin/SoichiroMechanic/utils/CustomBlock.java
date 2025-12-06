package Soichiro.plugin.SoichiroMechanic.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CustomBlock {
    private Location location;
    private Material baseMaterial;
    private String customName;
    private String customData;

    public CustomBlock(Location location, Material baseMaterial, String customName, String customData) {
        this.location = location;
        this.baseMaterial = baseMaterial;
        this.customName = customName;
        this.customData = customData;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getBaseMaterial() {
        return baseMaterial;
    }

    public void setBaseMaterial(Material baseMaterial) {
        this.baseMaterial = baseMaterial;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public Block getBlock() {
        return location != null ? location.getBlock() : null;
    }

    public boolean place() {
        if (location == null || baseMaterial == null) {
            return false;
        }
        getBlock().setType(baseMaterial);
        return true;
    }

    public void remove() {
        if (location != null) {
            getBlock().setType(Material.AIR);
        }
    }

    @Override
    public String toString() {
        return "CustomBlock{location=" + location + ", baseMaterial=" + baseMaterial + 
               ", customName='" + customName + "', customData='" + customData + "'}";
    }
}
