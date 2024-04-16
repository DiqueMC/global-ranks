package com.diquemc.GlobalRanks.command;

import com.diquemc.GlobalRanks.GlobalRanks;
import com.diquemc.GlobalRanks.PlayerRank;
import com.diquemc.GlobalRanks.Rank;
import com.diquemc.helper.DiqueMCCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SetRankCommand extends DiqueMCCommand {
    private final GlobalRanks plugin;

    public SetRankCommand(GlobalRanks plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
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
                message = message + ChatColor.GREEN + " al usuario " + ChatColor.YELLOW + pr.getPlayerName();
                if (pr.hasExpirationDate()) {
                    message = message + ChatColor.GREEN + " hasta " + ChatColor.YELLOW + pr.getExpirationDateString();
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

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        if(args.length == 1) {
            return playersListAutoComplete(args);
        } else if (args.length == 2){
            return commandAutoComplete(args, Arrays.asList("vipZombie", "vipEnderman", "vipWither", "youtuber"));
        } else {
            return Collections.emptyList();
        }
    }

}
