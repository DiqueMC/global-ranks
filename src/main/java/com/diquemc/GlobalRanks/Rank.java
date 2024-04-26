package com.diquemc.GlobalRanks;

import com.diquemc.utils.ChatUtil;
import com.diquemc.utils.DateUtil;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class Rank {
    private static LinkedHashMap<String, Rank> loadedRanks;


    private static LinkedHashMap _getRankByName(String rankName) {
        GlobalRanks plugin = GlobalRanks.getPlugin();
        Collection<String> rankList = ((MemorySection) plugin.getConfig().get("ranks")).getKeys(false);
        for (String e : rankList)
            if (e.equalsIgnoreCase(rankName)) {
                return (LinkedHashMap) ((MemorySection) plugin.getConfig().get("ranks." + e)).getValues(false);
            }
        return null;
    }

//    public static boolean isValidRank(String rankName){
//        return getRankByName(rankName).isValid();
//    }
    public static Set<String> getManagedRanksAsPermissionGroup() {
        Set<String> result = new HashSet<>();
        FileConfiguration config = GlobalRanks.getPlugin().getConfig();
        ConfigurationSection configRanks = config.getConfigurationSection("ranks");
        if(configRanks == null) {
            return  result;
        }
        Set<String> definedRanks = configRanks.getKeys(false);
        for(String rank: definedRanks) {
            result.add("group." + rank.toLowerCase());
        }
        return result;
    }
    public static Set<String> getManagedRanks() {
        return loadedRanks.keySet();
    }

    public static void init() {
        loadedRanks = new LinkedHashMap<String, Rank>();
    }

    public static Rank getRankByName(String _rankName) {
        String rankName = _rankName.toLowerCase();
        if (loadedRanks.get(rankName) == null) {
            LinkedHashMap hash = _getRankByName(rankName);
            if (hash == null) {
                loadedRanks.put(rankName, new Rank(_rankName));
            } else {
                loadedRanks.put(rankName, new Rank(_rankName, hash));
            }
        }
        return loadedRanks.get(rankName);

    }

    private LinkedHashMap info;
    private String name;
    private String displayName;

    Rank(String rankName, LinkedHashMap rankInfo) {
        name = rankName;
        info = rankInfo;
        if (info != null) {
            displayName = (String) info.get("displayName");
            if (displayName == null) {
                displayName = name;
            }
        }
    }

    Rank(String rankName) {
        name = rankName;
    }

    public boolean isValid() {
        return info != null;
    }

    public boolean isEqualTo(String aRankName) {
        return name.equalsIgnoreCase(aRankName);
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getDisplayName() {
        return ChatUtil.translateColorCodes(displayName);
    }

    //TODO CHANGE THIS
    @SuppressWarnings("unchecked")
    public void sendJoinMessage(Player player) {
        List<String> messages = new ArrayList<String>();
        if (info != null && info.get("joinMessage") != null) {
            messages = (List) info.get("joinMessage");

        } else {
            messages.add(ChatColor.GREEN + "Felicitaciones! Ya tienes tu rango " + getDisplayName());
        }
        for (String message : messages) {
            player.sendMessage(ChatUtil.translateColorCodes(message));
        }

    }

    @SuppressWarnings("unchecked")
    public void sendLeaveMessage(Player player) {
        List<String> messages = new ArrayList<String>();
        if (info != null && info.get("leaveMessage") != null) {
            messages = (List) info.get("leaveMessage");
        } else {
            messages.add(ChatColor.YELLOW + "Se ha agotado tu rango " + getDisplayName());

        }
        for (String message : messages) {
            player.sendMessage(ChatUtil.translateColorCodes(message));
        }

    }

    public String getName() {
        return name;
    }

    public long getExpirationFromNow() {

        if (info.get("duration") != null) {
            try {
                return DateUtil.parseDateDiff((String) info.get("duration"), true).getTime();
            } catch (Exception e) {
                Bukkit.getLogger().severe("Invalid duration config");
            }

        }
        return 0;
    }

    public Rank getNextRank() {
        if (info != null && info.get("nextRank") != null) {
            Rank nextRank = Rank.getRankByName((String) info.get("nextRank"));
            if (nextRank.isValid()) {
                return nextRank;
            }

        }
        return null;
    }
}
