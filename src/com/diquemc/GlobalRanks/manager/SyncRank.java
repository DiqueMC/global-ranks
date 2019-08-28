package com.diquemc.GlobalRanks.manager;

import com.diquemc.GlobalRanks.GlobalRanks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SyncRank implements Runnable {
    GlobalRanks plugin;

    public SyncRank(GlobalRanks p) {
        plugin = p;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            plugin.getRankManager().checkRanks(player);
        }
    }
}
