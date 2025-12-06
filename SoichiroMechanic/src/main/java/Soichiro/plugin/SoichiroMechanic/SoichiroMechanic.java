package Soichiro.plugin.SoichiroMechanic;

import org.bukkit.plugin.java.JavaPlugin;
import Soichiro.plugin.SoichiroMechanic.managers.PluginManager;
import Soichiro.plugin.SoichiroMechanic.listeners.PlayerListener;

public class SoichiroMechanic extends JavaPlugin {
    
    @Override
    public void onEnable() {
        
        // Initialize managers
        PluginManager.getInstance().initialize();
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        getLogger().info("SoichiroMechanic has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SoichiroMechanic has been disabled!");
    }
    
}