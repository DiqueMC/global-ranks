package com.diquemc.GlobalRanks.command;

import com.diquemc.GlobalRanks.GlobalRanks;
import com.diquemc.GlobalRanks.PlayerRank;
import com.diquemc.GlobalRanks.Rank;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class SetRankCommand implements CommandExecutor {
    private final GlobalRanks plugin;

    public SetRankCommand(GlobalRanks plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }

    @Override
    public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            return false;
        }

        try {
            String playerName = args[0];
            OfflinePlayer p = plugin.getPlayer(playerName);
            if (p == null) {
                sender.sendMessage(ChatColor.RED + "No se pudo encontrar el jugador " + ChatColor.YELLOW + playerName);
                return false;
            }

            Rank rank = Rank.getRankByName(args[1]);
            if (!rank.isValid()) {
                sender.sendMessage(ChatColor.RED + "El rango especificado no es valido " + ChatColor.YELLOW + rank);
                return false;
            }
            PlayerRank pr = plugin.getRankManager().setGlobalRank(p, rank);
            if (pr != null) {

                String message = ChatColor.GREEN + "Se ha asignado el rango " + ChatColor.YELLOW + pr.getTargetRank().getDisplayName();
                message = message + ChatColor.GREEN + " al usuario " + ChatColor.YELLOW + pr.playerName;
                if (pr.expirationDate != null) {
                    message = message + ChatColor.GREEN + " hasta " + ChatColor.YELLOW + pr.expirationDate.toLocaleString();
                }
                sender.sendMessage(message);
            } else {
                sender.sendMessage(ChatColor.RED + "Ha ocurrido un error al asignar el rango");
            }

        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

}
