package com.diquemc.GlobalRanks;

import com.diquemc.GlobalRanks.command.SetRankCommand;
import com.diquemc.GlobalRanks.manager.RankManager;
import com.diquemc.GlobalRanks.manager.SyncRank;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class GlobalRanks extends JavaPlugin {
	private static GlobalRanks plugin = null;
	public static String chatPrefix = "&4[&bGR&4]&r ";
    public GlobalConfig global;
    public static RankManager rankManager;


	public void onEnable() {
		plugin = this;
        loadConfiguration();
        Rank.init();
        global = new GlobalConfig(this);
        rankManager = new RankManager(this);
        this.getCommand("setrank").setExecutor(new SetRankCommand(this));
        getLogger().info(chatPrefix + "GlobalRanks enabled");
        SyncRank syncRank = new SyncRank(this);
        scheduleSyncRepeatingTask(syncRank,0, 20L * 10); //EVERY MINUTE

	}

    public void loadConfiguration() {
        //See "Creating you're defaults"
        getConfig().options().copyDefaults(true); // NOTE: You do not have to use "plugin." if the class extends the java plugin
        //Save the config whenever you manipulate it
        saveConfig();
    }

	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
        saveConfig();
	}

	public static int scheduleSyncRepeatingTask(Runnable run, long start, long delay) {
		return plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, run, start, delay);
	}
//	public static void cancelTask(int taskID) {
//		plugin.getServer().getScheduler().cancelTask(taskID);
//	}

    public static GlobalRanks getPlugin() {
        return plugin;
    }

    public RankManager getRankManager(){
        return rankManager;
    }

    public OfflinePlayer getPlayer(String name){
        OfflinePlayer p = plugin.getServer().getOfflinePlayer(name);
        if(p != null && p.hasPlayedBefore()) {
            plugin.getLogger().info("Found player " + p.getName());
            plugin.getLogger().info("uid " + p.getUniqueId().toString());
            plugin.getLogger().info("isOnline " + p.isOnline());
            plugin.getLogger().info("has played " + p.hasPlayedBefore());
            return p;
        }
        return null;

    }
//    public OfflinePlayer getPlayer(UUID uid){
//        OfflinePlayer p = plugin.getServer().getOfflinePlayer(uid);
//        if(p != null && p.hasPlayedBefore()) {
//            plugin.getLogger().info("Found player " + p.getName());
//            plugin.getLogger().info("uid " + p.getUniqueId().toString());
//            plugin.getLogger().info("isOnline " + p.isOnline());
//            plugin.getLogger().info("has played " + p.hasPlayedBefore());
//            return p;
//        }
//        return null;
//
//    }


}
