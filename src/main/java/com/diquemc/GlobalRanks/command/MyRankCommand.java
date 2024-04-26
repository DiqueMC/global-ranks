package com.diquemc.GlobalRanks.command;

import com.diquemc.GlobalRanks.GlobalRanks;
import com.diquemc.GlobalRanks.PlayerRank;
import com.diquemc.GlobalRanks.Rank;
import com.diquemc.GlobalRanks.manager.RankManager;
import com.diquemc.utils.DateUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyRankCommand extends GetRankCommand {
//    private final GlobalRanks plugin;

    public MyRankCommand(GlobalRanks plugin) {
        super(plugin);
//        this.plugin = plugin; // Store the plugin in situations where you need it.
    }


    @Override
    public final boolean onCommand(@Nonnull final CommandSender sender, @Nonnull final Command command, @Nonnull final String label, @Nonnull final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Este comando solo puede ser ejecutado por jugadores");
            return true;
        }
        Player p = (Player) sender;
        try {
            if (args.length == 0) {

                rankInfo(sender, p);
//                RankManager rm = plugin.getRankManager();
//
//                Rank lr = rm.getLocalRank(p);
//                PlayerRank gr = rm.getGlobalPlayerRank(p);



//                String currentRank = null,
//                    globalRank = null,
//                    remainingTime = null,
//                    rankUntil = null,
//                    nextRank = null;
////
//                if (gr != null) {
//                    Rank global = gr.getTargetRank();
//                    if (lr != null && !lr.isEqualTo(global.getName())) {
//                        currentRank = lr.getDisplayName();
//                        remainingTime = "0 segundos";
//                        nextRank = global.getDisplayName();
//                    } else {
//                        globalRank = global.getDisplayName();
//                        if (gr.hasExpirationDate()) {
//                            rankUntil = gr.getExpirationDateString();
//                            remainingTime = gr.remainingTime();
//                        }
//                    }
//                } else if (lr != null) {
//                    currentRank = lr.getDisplayName();
//                    remainingTime = "0 segundos";
//                    if (lr.getNextRank() != null) {
//                        nextRank = lr.getNextRank().getDisplayName();
//                    }
//                } else {
//                    currentRank = rm.getPlayerGroup(p);
//                    if (currentRank == null || currentRank.length() == 0) {
//                        currentRank = "Sin rango";
//                    }
//                }

//                List<String> messages = new ArrayList<String>();
//                messages.add(ChatColor.GOLD + "=================================");
//                messages.add(ChatColor.GOLD + "Informacion de Rango:");
//
//                if(currentRank != null) {
//                    messages.add(ChatColor.GREEN + "Rango actual: " + ChatColor.YELLOW + currentRank);
//                }
//                if(globalRank != null) {
//                    messages.add(ChatColor.GREEN + "Rango global: " + ChatColor.YELLOW + globalRank);
//                }
//                if(rankUntil != null) {
//                    messages.add(ChatColor.GREEN + "Rango hasta: " + ChatColor.YELLOW + rankUntil);
//                }
//                if(remainingTime != null) {
//                    messages.add(ChatColor.GREEN + "Tiempo restante: " + remainingTime);
//                }
//                if(nextRank != null) {
//                    messages.add(ChatColor.GREEN + "Proximo rango: " + ChatColor.YELLOW + lr.getNextRank().getDisplayName());
//                }
//                messages.add(ChatColor.GREEN + "Hora del servidor: " + ChatColor.YELLOW + DateFormat.getDateTimeInstance().format(new Date()));
//
//                messages.add(ChatColor.GOLD + "=================================");
//                for (String message : messages) {
//                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
//                }
//

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
