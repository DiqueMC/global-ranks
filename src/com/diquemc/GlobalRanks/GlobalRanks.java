package com.diquemc.GlobalRanks;

import com.diquemc.GlobalRanks.command.*;
import com.diquemc.GlobalRanks.databases.MySQL;
import com.diquemc.GlobalRanks.databases.SQL;
import com.diquemc.GlobalRanks.databases.SQLite;
import com.diquemc.GlobalRanks.manager.RankManager;
import com.diquemc.GlobalRanks.manager.SyncRank;
import com.diquemc.helper.DiqueMCPlugin;
import com.diquemc.utils.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GlobalRanks extends DiqueMCPlugin {
    private static GlobalRanks plugin = null;
    public GlobalConfig global;
    private static RankManager rankManager;
    private Set<SQL> databases;
    private static SQL DATABASE;

    public SQL getDB() {
        return DATABASE;
    }

    public void onEnable() {
        plugin = this;
        databases = new HashSet<>();
        databases.add(new MySQL(this));
        databases.add(new SQLite(this));

        loadConfiguration();
        setupDatabase();

        Rank.init();
        global = new GlobalConfig(this);
        rankManager = new RankManager(this);
        initializeCommand("setrank", new SetRankCommand(this));
        initializeCommand("delranks", new RemoveRankCommand(this));
        initializeCommand("getrank", new GetRankCommand(this));
        initializeCommand("rank", new MyRankCommand(this));
        initializeCommand("globalranks", new MainCommand(this));
        String chatPrefix = "&4[&bGR&4]&r ";
        getLogger().info(chatPrefix + "GlobalRanks enabled");

        if (!DATABASE.checkConnection()) {
            getLogger().severe("Error with DATABASE");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        SyncRank syncRank = new SyncRank(this);
        scheduleSyncRepeatingTask(syncRank, 20L * 10, 20L * getConfig().getInt("frequencyCheck")); //EVERY 60 segs
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

    }

    private void loadConfiguration() {
        //See "Creating your defaults"
        getConfig().options().copyDefaults(true);
        //Save the config whenever you manipulate it
        saveConfig();
    }

    private void setupDatabase() {
        String type = getConfig().getString("database.type", "mysql");

        DATABASE = null;

        for (SQL database : databases) {
            if (type != null && type.equalsIgnoreCase(database.getConfigName())) {
                DATABASE = database;
                getLogger().info("Database set to " + database.getConfigName() + ".");
                break;
            }
        }

        if (DATABASE == null) {
            getLogger().info("Database type does not exist!");
        }

    }

    public void reload() {
        getServer().getScheduler().cancelTasks(this);
        DATABASE.disconnect();
        reloadConfig();
        onEnable();
    }

    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        saveConfig();
        DATABASE.disconnect();
    }

    private static void scheduleSyncRepeatingTask(Runnable run, long start, long delay) {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, run, start, delay);
    }

    public static GlobalRanks getPlugin() {
        return plugin;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public OfflinePlayer getPlayer(String name) {
        String uuidString = PlayerManager.getPlayerUUID(name);
        Bukkit.getConsoleSender().sendMessage("UUID for " + name + " " + uuidString);
        if(uuidString == null) {
            return null;
        }
        return Bukkit.getOfflinePlayer(UUID.fromString(uuidString));
    }

}
