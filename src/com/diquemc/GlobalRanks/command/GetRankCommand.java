package com.diquemc.GlobalRanks.command;

import com.diquemc.GlobalRanks.GlobalRanks;
import com.diquemc.GlobalRanks.PlayerRank;
import com.diquemc.GlobalRanks.Rank;
import com.diquemc.GlobalRanks.manager.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class GetRankCommand implements CommandExecutor {
    final GlobalRanks plugin;

    public GetRankCommand(GlobalRanks plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }

    void rankInfo(CommandSender sender, OfflinePlayer p) {
        try {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                RankManager rm = plugin.getRankManager();
                Rank lr = rm.getLocalRank(p);
                PlayerRank gr = rm.getGlobalPlayerRank(p);
                String currentRank = null,
                        globalRank = null,
                        remainingTime = null,
                        rankUntil = null,
                        nextRank = null;

                if (gr != null) {
                    Rank global = gr.getTargetRank();
                    if (lr != null && !lr.isEqualTo(global.getName())) {
                        currentRank = lr.getDisplayName();
                        remainingTime = "0 segundos";
                        nextRank = global.getDisplayName();
                    } else {
                        globalRank = global.getDisplayName();
                        if (gr.hasExpirationDate()) {
                            rankUntil = gr.getExpirationDateString();
                            remainingTime = gr.remainingTime();
                        }
                    }
                } else if (lr != null) {
                    currentRank = lr.getDisplayName();
                    remainingTime = "0 segundos";
                    if (lr.getNextRank() != null) {
                        nextRank = lr.getNextRank().getDisplayName();
                    }
                } else {
                    currentRank = rm.getPlayerGroup(p);
                    if (currentRank == null || currentRank.length() == 0) {
                        currentRank = "Sin rango";
                    }
                }

                List<String> messages = new ArrayList<>();
                messages.add(ChatColor.GOLD + "=================================");
                if (sender instanceof OfflinePlayer && sender == p) {
                    messages.add(ChatColor.GOLD + "Informacion de Rango:");
                } else {
                    messages.add(ChatColor.GREEN + "Jugador: " + ChatColor.YELLOW + p.getName());
                }
                if (currentRank != null) {
                    messages.add(ChatColor.GREEN + "Rango actual: " + ChatColor.YELLOW + currentRank);
                }
                if (globalRank != null) {
                    messages.add(ChatColor.GREEN + "Rango global: " + ChatColor.YELLOW + globalRank);
                }
                if (rankUntil != null) {
                    messages.add(ChatColor.GREEN + "Rango hasta: " + ChatColor.YELLOW + rankUntil);
                }
                if (remainingTime != null) {
                    messages.add(ChatColor.GREEN + "Tiempo restante: " + remainingTime);
                }
                if (nextRank != null) {
                    messages.add(ChatColor.GREEN + "Proximo rango: " + ChatColor.YELLOW + lr.getNextRank().getDisplayName());
                }
                messages.add(ChatColor.GREEN + "Hora del servidor: " + ChatColor.YELLOW + DateFormat.getDateTimeInstance().format(new Date()));

                messages.add(ChatColor.GOLD + "=================================");
                for (String message : messages) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                }

            });
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
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

//            RankManager rm = GlobalRanks.getPlugin().getRankManager();

//            Rank lr = rm.getLocalRank(p);
//            PlayerRank gr = rm.getGlobalPlayerRank(p);

//            List<String> messages = new ArrayList<String>();

//            sender.sendMessage(ChatColor.GOLD + "=================================");
//            sender.sendMessage(ChatColor.GREEN + "Jugador: " + ChatColor.YELLOW + p.getName());
            rankInfo(sender, p);

//            if (gr != null) {
//                Rank global = gr.getTargetRank();
//                if (lr != null && !lr.isEqualTo(global.getName())) {
//                    messages.add(ChatColor.GREEN + "Rango actual: " + ChatColor.YELLOW + lr.getDisplayName());
//                    messages.add(ChatColor.GREEN + "Tiempo restante: " + ChatColor.YELLOW + "0 segundos");
//                    messages.add(ChatColor.GREEN + "Proximo rango: " + ChatColor.YELLOW + global.getDisplayName());
//                } else {
//                    messages.add(ChatColor.GREEN + "Rango global: " + ChatColor.YELLOW + global.getDisplayName());
//                    if (gr.expirationDate != null) {
//                        messages.add(ChatColor.GREEN + "Rango hasta: " + ChatColor.YELLOW + gr.expirationDate.toLocaleString());
//                        messages.add(ChatColor.GREEN + "Tiempo restante: " + ChatColor.YELLOW +
//                                DateUtil.formatDateDiff(gr.expirationDate.getTime()));
//                    }
//
//
//                }
//            } else if (lr != null) {
//                messages.add(ChatColor.GREEN + "Rango actual: " + ChatColor.YELLOW + lr.getDisplayName());
//                messages.add(ChatColor.GREEN + "Tiempo restante: " + ChatColor.YELLOW + "0 segundos");
//                if (lr.getNextRank() != null) {
//                    messages.add(ChatColor.GREEN + "Proximo rango: " + ChatColor.YELLOW + lr.getNextRank().getDisplayName());
//                }
//            } else {
//                String group = rm.getPlayerGroup(p);
//                if (group == null || group.length() == 0) {
//                    group = "Sin rango";
//                }
//                messages.add(ChatColor.GREEN + "Rango actual: " + ChatColor.YELLOW + group);
//            }
//
//            messages.add(ChatColor.GREEN + "Hora del servidor: " + ChatColor.YELLOW + new Date().toLocaleString());

//            sender.sendMessage(ChatColor.GOLD + "=================================");
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

}
