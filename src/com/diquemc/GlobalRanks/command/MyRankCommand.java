package com.diquemc.GlobalRanks.command;

import com.diquemc.GlobalRanks.GlobalRanks;
import com.diquemc.GlobalRanks.PlayerRank;
import com.diquemc.GlobalRanks.Rank;
import com.diquemc.GlobalRanks.manager.RankManager;
import com.diquemc.GlobalRanks.utils.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyRankCommand implements CommandExecutor {
    private final GlobalRanks plugin;

    public MyRankCommand(GlobalRanks plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }


    @Override
    public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por jugadores");
            return true;
        }
        Player p = (Player) sender;
        try {
            if (args.length == 0) {


                RankManager rm = plugin.getRankManager();

                Rank lr = rm.getLocalRank(p);
                PlayerRank gr = rm.getGlobalPlayerRank(p);

                List<String> messages = new ArrayList<String>();

                messages.add(ChatColor.GOLD + "=================================");
                messages.add(ChatColor.GOLD + "Informacion de Rango:");

                if (gr != null) {
                    Rank global = gr.getTargetRank();
                    if (lr != null && !lr.isEqualTo(global.getName())) {
                        messages.add(ChatColor.GREEN + "Rango actual: " + ChatColor.YELLOW + lr.getDisplayName());
                        messages.add(ChatColor.GREEN + "Tiempo restante: " + ChatColor.YELLOW + "0 segundos");
                        messages.add(ChatColor.GREEN + "Proximo rango: " + ChatColor.YELLOW + global.getDisplayName());
                    } else {
                        messages.add(ChatColor.GREEN + "Rango global: " + ChatColor.YELLOW + global.getDisplayName());
                        if (gr.expirationDate != null) {
                            messages.add(ChatColor.GREEN + "Rango hasta: " + ChatColor.YELLOW + gr.expirationDate.toLocaleString());
                            messages.add(ChatColor.GREEN + "Tiempo restante: " + ChatColor.YELLOW +
                                    DateUtil.formatDateDiff(gr.expirationDate.getTime()));
                        }


                    }
                } else if (lr != null) {
                    messages.add(ChatColor.GREEN + "Rango actual: " + ChatColor.YELLOW + lr.getDisplayName());
                    messages.add(ChatColor.GREEN + "Tiempo restante: " + ChatColor.YELLOW + "0 segundos");
                    if (lr.getNextRank() != null) {
                        messages.add(ChatColor.GREEN + "Proximo rango: " + ChatColor.YELLOW + lr.getNextRank().getDisplayName());
                    }
                } else {
                    String group = rm.getPlayerGroup(p);
                    if (group == null || group.length() == 0) {
                        group = "Sin rango";
                    }
                    messages.add(ChatColor.GREEN + "Rango actual: " + ChatColor.YELLOW + group);
                }


                messages.add(ChatColor.GREEN + "Hora del servidor: " + ChatColor.YELLOW + new Date().toLocaleString());

                messages.add(ChatColor.GOLD + "=================================");
                for (String message : messages) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }


            } else if ("check".equalsIgnoreCase(args[0])) {
                plugin.getDB().removeFromCache(p);
                sender.sendMessage(ChatColor.GREEN + "Hecho.");
            } else {
                sender.sendMessage(ChatColor.RED + "Opcion desconocida < " + args[0] + ">");
                sender.sendMessage(ChatColor.RED + "Uso: /rank [check]");

            }


        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return true;

    }
}
