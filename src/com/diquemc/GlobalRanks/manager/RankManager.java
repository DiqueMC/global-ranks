package com.diquemc.GlobalRanks.manager;

import com.diquemc.GlobalRanks.GlobalRanks;
import com.diquemc.GlobalRanks.PlayerRank;
import com.diquemc.GlobalRanks.Rank;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.LinkedHashMap;

public class RankManager {
    public static RankManager instance;
    private static GlobalRanks plugin;
    private static Permission permission = null;

    public RankManager(GlobalRanks p) {
        plugin = p; // Store the plugin in situations where you need it.
        setupPermissions();
        instance = this;
    }

    private boolean setupPermissions() {
        try {
            final RegisteredServiceProvider<Permission> permissionProvider = plugin
                    .getServer()
                    .getServicesManager()
                    .getRegistration(net.milkbowl.vault.permission.Permission.class);

            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("No permission support");
        }
        return permission != null;
    }

    public static boolean hasRank(OfflinePlayer player, Rank rank) {
//        return permission.playerInGroup(null,player,rank.getName());
        String[] groups = permission.getPlayerGroups(null, player);
        for (String group : groups) {
            if (rank.isEqualTo(group)) {
                return true;
            }
        }
        return false;
    }

    private boolean setRank(OfflinePlayer player, Rank rank) {
        if (hasRank(player, rank)) {
            plugin.getLogger().warning("Player already have the rank: " + rank);
            return false;
        }
        permission.playerAddGroup(null, player, rank.getName());
        plugin.getLogger().warning("Added player " + player.getName() + " to group " + rank.getDisplayName());
        if (player.isOnline()) {
            rank.sendJoinMessage(player.getPlayer());
        }
        return true;
    }

    public String getPlayerGroup(OfflinePlayer player) {
        String[] groups = permission.getPlayerGroups(null, player);
        if (groups.length > 0) {
            return groups[0];
        } else {
            return null;
        }

    }

    public static boolean removeRank(OfflinePlayer player, Rank rank) {

        if (!hasRank(player, rank)) {
            plugin.getLogger().warning("Player doesn't have the rank: " + rank.getDisplayName());
            return false;
        }
        permission.playerRemoveGroup(null, player, rank.getName());
        plugin.getLogger().warning("Removed player " + player.getName() + " from group " + rank);

        return true;
    }


    public void checkRanks(OfflinePlayer player) {
        PlayerRank gr = getGlobalPlayerRank(player);
        Rank localRank = getLocalRank(player);
        if (gr == null) {
            if (localRank != null) {
                removeRank(player, localRank);
                saveLocalRank(player, null);
                if (player.isOnline()) {
                    localRank.sendLeaveMessage(player.getPlayer());
                }
                Rank nextRank = localRank.getNextRank();
                if (nextRank != null) {
                    plugin.getLogger().info("Rank expired " + localRank.getName() + " new rank " + nextRank.getName());
                    plugin.getRankManager().setGlobalRank(player, nextRank);

                }
            }
        } else {
            if (localRank == null || !gr.getTargetRank().isEqualTo(localRank.getName())) {
                if (localRank != null) {
                    removeRank(player, localRank);
                }
                setRank(player, gr.getTargetRank());
                saveLocalRank(player, gr);

            }
        }

    }

    public PlayerRank getGlobalPlayerRank(OfflinePlayer player) {
        return plugin.global.getRankForPlayer(player);

    }

    public PlayerRank setGlobalRank(OfflinePlayer p, Rank rank) {

        if (!rank.isValid()) {
            plugin.getLogger().warning("Attempt to set an invalid rank");
            return null;
        }

        long expireDate = rank.getExpirationFromNow();
        PlayerRank pr = new PlayerRank(p, rank, expireDate, 0);
        plugin.global.addRank(p, pr);
        checkRanks(p);
        return pr;
    }

    private void saveLocalRank(OfflinePlayer player, PlayerRank playerRank) {
        if (playerRank != null) {
            plugin.getConfig().set("users." + player.getUniqueId().toString(), playerRank.toHash());
        } else {
            plugin.getConfig().set("users." + player.getUniqueId().toString(), null);
        }
        plugin.saveConfig();

    }

    @SuppressWarnings("unchecked")
    public Rank getLocalRank(OfflinePlayer player) {
        FileConfiguration config = plugin.getConfig();
        String uuid = player.getUniqueId().toString();

        LinkedHashMap<String, Object> playerConfig;
        if (config.get("users." + uuid) == null) {
            return null;
        }
        //HOOORRIBLEEE
        if (config.get("users." + uuid).getClass() == MemorySection.class) {
            playerConfig = (LinkedHashMap<String, Object>) ((MemorySection) config.get("users." + uuid)).getValues(false);
        } else if (config.get("users." + uuid).getClass() == LinkedHashMap.class) {
            playerConfig = (LinkedHashMap<String, Object>) config.get("users." + uuid);
        } else {
            plugin.getLogger().severe("UNRECOGNIZED CLASS " + config.get("users." + uuid).getClass());
            return null;

        }

        PlayerRank localPlayerRank = new PlayerRank(playerConfig);
        return localPlayerRank.getTargetRank();

    }


}

