package com.diquemc.GlobalRanks.command;

import com.diquemc.GlobalRanks.GlobalRanks;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class RemoveRankCommand implements CommandExecutor {
    private final GlobalRanks plugin;

    public RemoveRankCommand(GlobalRanks plugin) {
            this.plugin = plugin; // Store the plugin in situations where you need it.
    }


    @Override
    public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 1) {
            return false;
        }
        try {
            String playerName = args[0];
            OfflinePlayer p = plugin.getPlayer(playerName);
            if(p == null ){
                sender.sendMessage(ChatColor.RED+ "No se pudo encontrar el jugador " + ChatColor.YELLOW + playerName );
                return false;
            }
            GlobalRanks.getPlugin().global.removePlayer(p);
            plugin.getRankManager().checkRanks(p);
            sender.sendMessage(ChatColor.GREEN + "Se han eliminado los rangos del jugador " + ChatColor.YELLOW + p.getName());


        }
        catch(final Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

}
