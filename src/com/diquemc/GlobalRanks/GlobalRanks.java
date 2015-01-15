package com.diquemc.GlobalRanks;

import com.diquemc.GlobalRanks.command.SetRankCommand;
import com.diquemc.GlobalRanks.manager.RankManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class GlobalRanks extends JavaPlugin {
	private static GlobalRanks plugin = null;
	public static String chatPrefix = "&4[&bGR&4]&r ";
    public static GlobalConfig global;


	public void onEnable() {
		plugin = this;
        loadConfiguration();
        global = new GlobalConfig(this);
        new RankManager(this);
        this.getCommand("setrank").setExecutor(new SetRankCommand(this));

        getLogger().info(chatPrefix + "GlobalRanks enabled");
	}

    public void loadConfiguration() {
        //See "Creating you're defaults"
        getConfig().options().copyDefaults(true); // NOTE: You do not have to use "plugin." if the class extends the java plugin
        //Save the config whenever you manipulate it
        saveConfig();
    }

	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
	}

	public static int scheduleSyncRepeatingTask(Runnable run, long delay) {
		return scheduleSyncRepeatingTask(run, delay, delay);
	}
	public static int scheduleSyncRepeatingTask(Runnable run, long start, long delay) {
		return plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, run, start, delay);
	}
	public static void cancelTask(int taskID) {
		plugin.getServer().getScheduler().cancelTask(taskID);
	}

    public static GlobalRanks getPlugin() {
        return plugin;
    }

}
