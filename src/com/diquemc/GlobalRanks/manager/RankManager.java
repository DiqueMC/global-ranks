package com.diquemc.GlobalRanks.manager;

import com.diquemc.GlobalRanks.GlobalRanks;
import com.diquemc.GlobalRanks.PlayerRank;
import com.diquemc.GlobalRanks.Rank;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

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
        }catch (Exception e){
            plugin.getLogger().severe("No permission support");
        }
        return permission != null;
    }

    public static boolean hasRank(OfflinePlayer player, Rank rank){
        return permission.playerInGroup(null,player,rank.getName());
    }

    public boolean setRank(OfflinePlayer player, Rank rank){
        if(hasRank(player,rank)){
            plugin.getLogger().warning("Player already have the rank: " + rank);
            return false;
        }
        permission.playerAddGroup(null, player, rank.getName());
        plugin.getConfig().set("users." + player.getName() + ".rank", rank.getName());
        plugin.getLogger().warning("Added player " + player.getName() + " to group " + rank.getDisplayName());
        if(player.isOnline()){
            rank.sendJoinMessage(player.getPlayer());
        }
        return true;
    }

    public String getPlayerGroup(OfflinePlayer player){
        String[] groups = permission.getPlayerGroups(null,player);
        if(groups.length > 0){
            return groups[0];
        }else{
            return null;
        }

    }

    public static boolean removeRank(OfflinePlayer player, Rank rank){

        if(!hasRank(player,rank)){
            plugin.getLogger().warning("Player doesn't have the rank: " + rank.getDisplayName());
            return false;
        }
        permission.playerRemoveGroup(null, player, rank.getName());
        plugin.getLogger().warning("Removed player " + player.getName() + " from group " + rank);

        return true;
    }


    public void checkRanks(OfflinePlayer player) {
        String playerName = player.getName();
        PlayerRank gr = getGlobalPlayerRank(player);
        Rank localRank = getLocalRank(player);
        if(gr == null){
            if(localRank != null){
                removeRank(player,localRank);
                plugin.getConfig().set("users."  + playerName + ".rank",null);
                if(player.isOnline()){
                    localRank.sendLeaveMessage(player.getPlayer());
                }
                Rank nextRank = localRank.getNextRank();
                if (nextRank != null){
                    plugin.getLogger().info("Rank expired " + localRank.getName() + " new rank " + nextRank.getName());
                    plugin.getRankManager().setGlobalRank(player, nextRank);

                }
            }
        }else {
            if(localRank == null || !gr.getTargetRank().isEqualTo(localRank.getName())){
                if(localRank != null){
                    removeRank(player,localRank);
                }
                setRank(player,gr.getTargetRank());
                plugin.getConfig().set("users."  + playerName + ".rank",gr.getTargetRankName());

            }
        }

    }

    public PlayerRank getGlobalPlayerRank(OfflinePlayer player){
        return plugin.global.getRankForPlayer(player);

    }

    public Rank getLocalRank(OfflinePlayer player){
        String localRankName = plugin.getConfig().getString("users."  + player.getName() + ".rank");
        Rank localRank = null;
        if(localRankName != null){
            localRank= Rank.getRankByName(localRankName);
        }
        return localRank;
    }

    public PlayerRank setGlobalRank(OfflinePlayer p, Rank rank){

        if(!rank.isValid()){
            plugin.getLogger().warning("Attempt to set an invalid rank");
            return null;
        }

        long expireDate = rank.getExpirationFromNow();
        PlayerRank pr = new PlayerRank(p,rank,expireDate,0);
        setRank(p,rank);
        plugin.global.addRank(p,pr);
        return pr;
    }

}

