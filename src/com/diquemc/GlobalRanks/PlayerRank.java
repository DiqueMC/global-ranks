package com.diquemc.GlobalRanks;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Date;
import java.util.LinkedHashMap;
//import java.util.UUID;

public class PlayerRank {
    public String playerName;
    public String rankName;
    private Rank rank;
    public Date expirationDate = null;
    public Date creationDate = null;
    public String uuid;
    public OfflinePlayer _player;

    public PlayerRank(LinkedHashMap<String, Object> playerInfo) {
        try {
            playerName = (String) playerInfo.get("name");
            uuid = (String) playerInfo.get("uuid");

            rankName = (String) playerInfo.get("rank");
            if (rankName != null) {
                rank = Rank.getRankByName(rankName);

                rankName = rank.getName();

            }
            if (playerInfo.get("expiration") != null && ((Long) playerInfo.get("expiration")) > 0) {
                expirationDate = new Date((Long) playerInfo.get("expiration"));
            }
            if (playerInfo.get("creation") != null && ((Long) playerInfo.get("creation")) > 0) {
                creationDate = new Date((Long) playerInfo.get("creation"));
            }

        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to load player rank " + e);
            e.printStackTrace();
        }
    }

    public PlayerRank(OfflinePlayer player, Rank rankObject, long expiration, long creation) {
        try {
            _player = player;
            playerName = player.getName();
            rankName = rankObject.getName();
            rank = rankObject;
            uuid = player.getUniqueId().toString();
            if (expiration > 0) {
                expirationDate = new Date(expiration);
            }
            if (creation > 0) {
                creationDate = new Date(creation);
            } else {
                creationDate = new Date();
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to load player rank " + e);
            e.printStackTrace();
        }

    }

//    public OfflinePlayer getTargetPlayer(){
//        if(_player == null){
//            if(uuid != null){
//                _player = GlobalRanks.getPlugin().getPlayer(UUID.fromString(uuid));
//            }else{
//                _player = GlobalRanks.getPlugin().getPlayer(playerName);
//            }
//        }
//        return _player;
//    }

    public Rank getTargetRank() {
        if (rank == null) {
            rank = Rank.getRankByName(rankName);
        }
        return rank;
    }

    public String getTargetRankName() {
        return getTargetRank().getName();
    }

    public LinkedHashMap toHash() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("name", playerName);
        result.put("uuid", uuid);
        result.put("rank", rank.getName());
        if (expirationDate != null) {
            result.put("expiration", expirationDate.getTime());
        }
        if (creationDate != null) {
            result.put("creation", creationDate.getTime());
        }
        return result;
    }

    public boolean isExpired() {
        return expirationDate != null && new Date().after(expirationDate);
    }
}
