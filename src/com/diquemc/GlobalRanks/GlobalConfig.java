package com.diquemc.GlobalRanks;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;

public class GlobalConfig {

    private ConfigAccessor globalConfig;
    private GlobalRanks plugin;

    public boolean manageGlobalConfig() {
        return plugin.getConfig().getBoolean("manageGlobal");
    }

    public GlobalConfig(GlobalRanks p) {
        globalConfig = new ConfigAccessor(p,"global.yml");
        globalConfig.reloadConfig();
        globalConfig.saveDefaultConfig();
        plugin = p;
    }

    public void removePlayer(OfflinePlayer player) {
        String name = player.getName();
        FileConfiguration config = globalConfig.getConfig();
        config.set("players." + name,null);
        globalConfig.saveConfig();
    }
    @SuppressWarnings("unchecked")
    public PlayerRank getRankForPlayer(OfflinePlayer p) {
        FileConfiguration config = globalConfig.getConfig();
        String name = p.getName();

        LinkedHashMap<String,Object> playerConfig;
        if(config.get("players." + name) == null){
            return null;
        }
        //HOOORRIBLEEE
        if(config.get("players." + name).getClass() == MemorySection.class){
            playerConfig = (LinkedHashMap<String,Object>)((MemorySection)config.get("players." + name)).getValues(false);
        }else if(config.get("players." + name).getClass() == LinkedHashMap.class){
            playerConfig = (LinkedHashMap<String,Object>) config.get("players." + name);
        }else{
            plugin.getLogger().severe("UNRECOGNIZED CLASS " + config.get("players." + name).getClass() );
            return null;

        }

        PlayerRank pr = new PlayerRank(playerConfig);
        if(pr.isExpired()){
            if(manageGlobalConfig()){
                plugin.getLogger().info("Rank expired going to remove player");
                removePlayer(p);
            }
            return null;
        }
        return pr;

    }

    public void addRank(OfflinePlayer player, PlayerRank playerRank){
        FileConfiguration config = globalConfig.getConfig();
        String name = player.getName();
        config.set("players." + name, playerRank.toHash());
        globalConfig.saveConfig();

    }
}