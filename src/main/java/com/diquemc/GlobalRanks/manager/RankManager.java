package com.diquemc.GlobalRanks.manager;

import com.diquemc.GlobalRanks.GlobalRanks;
import com.diquemc.GlobalRanks.PlayerRank;
import com.diquemc.GlobalRanks.Rank;
//import net.milkbowl.vault.permission.Permission;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ContextSetFactory;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class  RankManager {
    public static RankManager instance;
    private static GlobalRanks plugin;
    private static LuckPerms luckPerms;
    private static ImmutableContextSet CONTEXT_GLOBAL;

//    private static Permission permission = null;

    public RankManager(GlobalRanks p) {
        plugin = p; // Store the plugin in situations where you need it.
        setupPermissions();
        instance = this;


    }

    private boolean setupPermissions() {
        try {
            luckPerms = LuckPermsProvider.get();
            CONTEXT_GLOBAL = ImmutableContextSet.of(DefaultContextKeys.SERVER_KEY, "global");
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public static boolean hasRank(Player player, Rank rank) {
        return player.hasPermission("group." + rank.getName());
    }

    public static void loadLPUser(OfflinePlayer player, RankManager.Callback callback) {
        UserManager userManager = luckPerms.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(player.getUniqueId());
        userFuture.thenAcceptAsync(callback::result);
    }
//    public static boolean hasRank(String playerUUID, Rank rank, RankManager.Callback callback) {
//        UserManager userManager = luckPerms.getUserManager();
//        CompletableFuture<User> userFuture = userManager.loadUser(UUID.fromString(playerUUID));
//
//        userFuture.thenAcceptAsync(user -> {
//            String primaryGroup = user.getPrimaryGroup();
//            callback.result(user, primaryGroup, primaryGroup.equalsIgnoreCase(rank.getName()));
//        });
//        return false;
//    }

//    private boolean setRank(String playerUUID, Rank rank) {
//        if (hasRank(player, rank)) {
//            plugin.getLogger().warning("Player already have the rank: " + rank);
//            return false;
//        }
//        permission.playerAddGroup(null, player, rank.getName());
//        plugin.getLogger().warning("Added player " + player.getName() + " to group " + rank.getDisplayName());
//        if (player.isOnline()) {
//            rank.sendJoinMessage(player.getPlayer());
//        }
//        return true;
//    }

//    public String getPlayerGroup(OfflinePlayer player) {
//        String[] groups = permission.getPlayerGroups(null, player);
//        if (groups.length > 0) {
//            return groups[0];
//        } else {
//            return null;
//        }
//
//    }

    private static boolean removeAllRanks(User user, Rank targetRank) {
        String checkingFor = "";
        boolean hasRank = false;
        if(targetRank != null) {
            checkingFor = "group." + targetRank.getName().toLowerCase();
        }
        List<Node> nodes = (List<Node>) user.getNodes();
        Set<String> managedRanks = Rank.getManagedRanksAsPermissionGroup();
        for(Node ownNode: nodes) {
            String permission = ownNode.getKey().toLowerCase();
//            Bukkit.getConsoleSender().sendMessage("Checking node " + permission + " with " + managedRanks );
            if(managedRanks.contains(permission)) {
                Bukkit.getConsoleSender().sendMessage("Removing rank " + permission + " from " + user.getUsername());
//                user.remo(ownNode);
            };
            if(checkingFor.equalsIgnoreCase(permission)) {
                hasRank = true;
            }
        }
        return hasRank;
    }


    private static void removeRank(Player player, User user, Rank rank) {
        boolean hadRank = removeAllRanks(user, rank);
        luckPerms.getUserManager().saveUser(user);
        if(hadRank) {
            rank.sendLeaveMessage(player);
        }
        plugin.getLogger().warning("Removed player " + player.getName() + " from group " + rank);
    }

    private static void setRank(Player player, User user, Rank rank) {
        removeAllRanks(user, null);
//        Node nodeGlobal = luckPerms.getNodeMatcherFactory()..newBuilder("group." + rank.getName()).setServer("global").build();
//        user.setPermission(nodeGlobal);
        luckPerms.getUserManager().saveUser(user);
        rank.sendJoinMessage(player);
        plugin.getLogger().warning("Added player " + player.getName() + " to group " + rank);
    }

    public void checkRanks(Player player)  {
        if(player == null || !player.isOnline()) {
            return;
        }
        PlayerRank gr = getGlobalPlayerRank(player);

        loadLPUser(player, (user) -> {
            if(user == null) {
                return;
            }
            if(!player.isOnline()) {
                return;
            }
            if(gr == null) {
                Bukkit.getConsoleSender().sendMessage("Going to remove all ranks for " + user.getUsername());
                removeAllRanks(user, null);
                luckPerms.getUserManager().saveUser(user);
                return;
            }
//            Node nodeGlobal = luckPerms.getNodeFactory().newBuilder("group." + gr.getTargetRankName()).setServer("global").build();

            Node nodeGlobal = Node.builder("group." + gr.getTargetRankName()).withContext(CONTEXT_GLOBAL).build();
            boolean hasRank = user.data().contains(nodeGlobal, NodeEqualityPredicate.ONLY_KEY).asBoolean();
            if(gr.isExpired()) {
                removeRank(player, user, gr.getTargetRank());
                plugin.global.removePlayer(player);
            } else if (!hasRank) {
                setRank(player, user, gr.getTargetRank());
            }
        });
//        Rank localRank = getLocalRank(player);
//        if (gr == null) {
//            if (localRank != null) {
//                removeRank(player, localRank);
////                saveLocalRank(player, null);
//                if (player.isOnline()) {
//                    localRank.sendLeaveMessage(player.getPlayer());
//                }
//                Rank nextRank = localRank.getNextRank();
//                if (nextRank != null) {
//                    plugin.getLogger().info("Rank expired " + localRank.getName() + " new rank " + nextRank.getName());
//                    plugin.getRankManager().setGlobalRank(player, nextRank);
//
//                }
//            }
//        } else {
//            if (localRank == null || !gr.getTargetRank().isEqualTo(localRank.getName())) {
//                if (localRank != null) {
//                    removeRank(player, localRank);
//                }
//                setRank(player, gr.getTargetRank());
////                saveLocalRank(player, gr);
//
//            }
//        }

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
        checkRanks(p.getPlayer());
        return pr;
    }

    public interface Callback {
        void result(User user);
    }

//    public static class UserNotFoundException extends Exception {} ;

//    private void saveLocalRank(OfflinePlayer player, PlayerRank playerRank) {
//        if (playerRank != null) {
//            plugin.getConfig().set("users." + player.getUniqueId().toString(), playerRank.toHash());
//        } else {
//            plugin.getConfig().set("users." + player.getUniqueId().toString(), null);
//        }
//        plugin.saveConfig();
//
//    }

//    @SuppressWarnings("unchecked")
//    public Rank getLocalRank(OfflinePlayer player) {
//        FileConfiguration config = plugin.getConfig();
//        String uuid = player.getUniqueId().toString();
//
//        LinkedHashMap<String, Object> playerConfig;
//        if (config.get("users." + uuid) == null) {
//            return null;
//        }
//        //HOOORRIBLEEE
//        if (config.get("users." + uuid).getClass() == MemorySection.class) {
//            playerConfig = (LinkedHashMap<String, Object>) ((MemorySection) config.get("users." + uuid)).getValues(false);
//        } else if (config.get("users." + uuid).getClass() == LinkedHashMap.class) {
//            playerConfig = (LinkedHashMap<String, Object>) config.get("users." + uuid);
//        } else {
//            plugin.getLogger().severe("UNRECOGNIZED CLASS " + config.get("users." + uuid).getClass());
//            return null;
//
//        }
//
//        PlayerRank localPlayerRank = new PlayerRank(playerConfig);
//        if(hasRank(player,localPlayerRank.getTargetRank())){
//            return localPlayerRank.getTargetRank();
//        }
//        return null;
//
//    }


}

