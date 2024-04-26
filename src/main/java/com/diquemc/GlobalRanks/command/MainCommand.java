package com.diquemc.GlobalRanks.command;

import com.diquemc.GlobalRanks.GlobalRanks;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;


public class MainCommand implements CommandExecutor {
    private final GlobalRanks plugin;

    public MainCommand(GlobalRanks plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }


    @Override
    public final boolean onCommand(@Nonnull final CommandSender sender, @Nonnull final Command command, @Nonnull final String label, @Nonnull final String[] args) {
        String howToUse = "Uso: /globalranks <reload|refresh>";
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + howToUse);
            return true;
        }

        try {
            if ("reload".equalsIgnoreCase(args[0])) {
                plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "GlobalRanks ha sido recargado.");
            } else if ("refresh".equalsIgnoreCase(args[0])) {
                plugin.getDB().clearCache();
                sender.sendMessage(ChatColor.GREEN + "Cache actualizada.");
            } else if ("player".equalsIgnoreCase(args[0]) && args.length > 1) {
                OfflinePlayer p = plugin.getPlayer(args[1]);
                if (p != null) {
                    sender.sendMessage(ChatColor.GREEN + "Jugador: " + ChatColor.YELLOW + p.getName());
                    sender.sendMessage(ChatColor.GREEN + "UUID: " + ChatColor.YELLOW + p.getUniqueId().toString());
                } else {
                    sender.sendMessage(ChatColor.RED + "Jugador no encontrado.");
                }


            } else {
                sender.sendMessage(ChatColor.RED + "Comando desconocido < " + args[0] + ">");
                sender.sendMessage(ChatColor.RED + howToUse);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

}
