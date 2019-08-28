package com.diquemc.GlobalRanks;

import com.diquemc.GlobalRanks.utils.DateUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
//import java.util.UUID;

public class PlayerRank {
    private String playerName;
    private String rankName;
    private Rank rank;
    private Date expirationDate = null;
    private Date creationDate = null;
    private String uuid;
    private OfflinePlayer _player;

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

    public boolean hasExpirationDate () {
        return expirationDate != null;
    }

    public boolean isExpired() {
        return hasExpirationDate() && new Date().after(expirationDate);
    }

    public String getExpirationDateString () {
        if(!hasExpirationDate()) {
            return "";
        }
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        return dateFormat.format(expirationDate);
    }

    public long getExpirationTime () {
        if(expirationDate != null) {
            return  expirationDate.getTime();
        }
        return 0;
    }

    public long getCreationTime () {
        if(creationDate != null) {
            return creationDate.getTime();
        }
        return 0;
    }

    public String remainingTime () {
        if(!hasExpirationDate()) {
            return  "";
        }
        return DateUtil.formatDateDiff(expirationDate.getTime());
    }

    public String getPlayerName() {
        return playerName;
    }
}
