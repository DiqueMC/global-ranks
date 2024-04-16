package com.diquemc.GlobalRanks.command;

import com.diquemc.GlobalRanks.GlobalRanks;
import com.diquemc.helper.DiqueMCCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;


public class RemoveRankCommand extends DiqueMCCommand {
    private final GlobalRanks plugin;

    public RemoveRankCommand(GlobalRanks plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }


    @Override
    public final boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull final String[] args) {
        if (args.length != 1) {
            return false;
        }
        try {
            String playerName = args[0];
            OfflinePlayer p = plugin.getPlayer(playerName);
            if (p == null) {
                sender.sendMessage(ChatColor.RED + "No se pudo encontrar el jugador " + ChatColor.YELLOW + playerName);
                return false;
            }
            GlobalRanks.getPlugin().global.removePlayer(p);
            plugin.getRankManager().checkRanks(p.getPlayer());
            sender.sendMessage(ChatColor.GREEN + "Se han eliminado los rangos del jugador " + ChatColor.YELLOW + p.getName());


        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        return playersListAutoComplete(args);
    }
}
