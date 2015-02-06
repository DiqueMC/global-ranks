package com.diquemc.GlobalRanks;

import com.diquemc.GlobalRanks.command.*;
import com.diquemc.GlobalRanks.databases.MySQL;
import com.diquemc.GlobalRanks.databases.SQL;
import com.diquemc.GlobalRanks.databases.SQLite;
import com.diquemc.GlobalRanks.manager.RankManager;
import com.diquemc.GlobalRanks.manager.SyncRank;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class GlobalRanks extends JavaPlugin {
	private static GlobalRanks plugin = null;
	public static String chatPrefix = "&4[&bGR&4]&r ";
    public GlobalConfig global;
    public static RankManager rankManager;
    private Set<SQL> databases;
    private static SQL DATABASE;

    public SQL getDB(){
        return DATABASE;
    }

	public void onEnable() {
		plugin = this;
        databases = new HashSet<SQL>();
        databases.add( new MySQL( this ) );
        databases.add( new SQLite( this ) );

        loadConfiguration();
        setupDatabase();

        Rank.init();
        global = new GlobalConfig(this);
        rankManager = new RankManager(this);
        this.getCommand("setrank").setExecutor(new SetRankCommand(this));
        this.getCommand("delranks").setExecutor(new RemoveRankCommand(this));
        this.getCommand("getrank").setExecutor(new GetRankCommand(this));
        this.getCommand("rank").setExecutor(new MyRankCommand(this));
        this.getCommand("globalranks").setExecutor(new MainCommand(this));
        getLogger().info(chatPrefix + "GlobalRanks enabled");

        if( ! DATABASE.checkConnection() )
        {
            getLogger().severe("Error with DATABASE");
            getServer().getPluginManager().disablePlugin(this);
            return ;
        }
        SyncRank syncRank = new SyncRank(this);
        scheduleSyncRepeatingTask(syncRank,20L * 10, 20L * getConfig().getInt("frequencyCheck")); //EVERY 60 segs
        getServer().getPluginManager().registerEvents( new PlayerListener(this), this );

	}

    public void loadConfiguration() {
        //See "Creating you're defaults"
        getConfig().options().copyDefaults(true); // NOTE: You do not have to use "plugin." if the class extends the java plugin
        //Save the config whenever you manipulate it
        saveConfig();
    }

    private boolean setupDatabase()
    {
        String type = getConfig().getString("database.type");

        DATABASE = null;

        for ( SQL database : databases )
        {
            if ( type.equalsIgnoreCase( database.getConfigName() ) )
            {
                DATABASE = database;

                getLogger().info("Database set to " + database.getConfigName() + ".");

                break;
            }
        }

        if ( DATABASE == null)
        {
            getLogger().info("Database type does not exist!");

            return false;
        }

        return true;
    }

    public void reload(){
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

	public static int scheduleSyncRepeatingTask(Runnable run, long start, long delay) {
		return plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, run, start, delay);
	}

    public static GlobalRanks getPlugin() {
        return plugin;
    }

    public RankManager getRankManager(){
        return rankManager;
    }

    public OfflinePlayer getPlayer(String name){
        OfflinePlayer p = plugin.getServer().getOfflinePlayer(name);
        if(p != null && p.hasPlayedBefore()) {
            return p;
        }
        return null;

    }
//    public OfflinePlayer getPlayer(UUID uuid){
//        OfflinePlayer p = plugin.getServer().getOfflinePlayer(uuid);
//        if(p != null && p.hasPlayedBefore()) {
//            plugin.getLogger().info("Found player " + p.getName());
//            plugin.getLogger().info("uuid " + p.getUniqueId().toString());
//            plugin.getLogger().info("isOnline " + p.isOnline());
//            plugin.getLogger().info("has played " + p.hasPlayedBefore());
//            return p;
//        }
//        return null;
//
//    }


}
