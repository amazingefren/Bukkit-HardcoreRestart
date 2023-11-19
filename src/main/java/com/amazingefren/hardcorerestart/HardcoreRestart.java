package com.amazingefren.hardcorerestart;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;

public class HardcoreRestart extends JavaPlugin {
    @Override
    public void onEnable() {
        System.out.println("HardcoreRestart Loaded.");
        this.getServer().getPluginManager().registerEvents((Listener)new DeathTrigger(this), (Plugin)this);
        this.getCommand("deaths").setExecutor(new DeathCommand());
    }
}