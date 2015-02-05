package com.diquemc.GlobalRanks;

import org.bukkit.OfflinePlayer;
public class GlobalConfig {

    private GlobalRanks plugin;

    public GlobalConfig(GlobalRanks p) {
        plugin = p;
    }

    public void removePlayer(OfflinePlayer player) {
        plugin.getDB().deletePlayer(player);
    }

    @SuppressWarnings("unchecked")
    public PlayerRank getRankForPlayer(OfflinePlayer p) {
        PlayerRank pr = plugin.getDB().getRank(p);
        if(pr == null){
            return null;
        }
        if(pr.isExpired()){
            plugin.getLogger().info("Rank expired going to remove player");
            removePlayer(p);
            return null;
        }
        return pr;
    }

    public void addRank(OfflinePlayer player, PlayerRank playerRank){
        plugin.getDB().uploadPlayerRank(player,playerRank);
    }
}