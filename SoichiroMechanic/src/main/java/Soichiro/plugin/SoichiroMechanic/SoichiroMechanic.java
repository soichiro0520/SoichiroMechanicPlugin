package Soichiro.plugin.SoichiroMechanic;

import org.bukkit.plugin.java.JavaPlugin;


public class SoichiroMechanic extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getLogger().info("SoichiroMechanic has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SoichiroMechanic has been disabled!");
    }
    
}