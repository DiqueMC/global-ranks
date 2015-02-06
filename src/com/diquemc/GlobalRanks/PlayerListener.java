package com.diquemc.GlobalRanks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private GlobalRanks plugin;

    public PlayerListener(GlobalRanks p) {
        plugin = p;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                if (event.getPlayer() != null) {
                    GlobalRanks.getPlugin().getRankManager().checkRanks(event.getPlayer());
                }
            }
        }, 20 * 10);

    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        GlobalRanks.getPlugin().getDB().removeFromCache(event.getPlayer());


    }
}