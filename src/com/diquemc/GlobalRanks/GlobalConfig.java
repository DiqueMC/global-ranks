package com.diquemc.GlobalRanks;

import org.bukkit.OfflinePlayer;

public class GlobalConfig {

    private final GlobalRanks plugin;

    GlobalConfig(GlobalRanks p) {
        plugin = p;
    }

    public void removePlayer(OfflinePlayer player) {
        plugin.getDB().deletePlayer(player);
    }

    public PlayerRank getRankForPlayer(OfflinePlayer p) {
        return plugin.getDB().getRank(p);
    }

    public void addRank(OfflinePlayer player, PlayerRank playerRank) {
        plugin.getDB().uploadPlayerRank(player, playerRank);
    }
}