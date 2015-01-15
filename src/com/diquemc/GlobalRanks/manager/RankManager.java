package com.diquemc.GlobalRanks.manager;

import com.diquemc.GlobalRanks.GlobalRanks;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.util.org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class RankManager {
    public static RankManager instance;
    private static GlobalRanks plugin;
    private static Permission permission = null;

    public RankManager(GlobalRanks plugin) {
            plugin = plugin; // Store the plugin in situations where you need it.
            setupPermissions();
            instance = this;
    }

    private boolean setupPermissions() {
            final RegisteredServiceProvider<Permission> permissionProvider = plugin
                    .getServer()
                    .getServicesManager()
                    .getRegistration(net.milkbowl.vault.permission.Permission.class);

            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }

            return permission != null;
        }

    @SuppressWarnings("deprecated")
    public static boolean setRank(String userName, String rank){

        if(permission.playerInGroup((String)null,userName,rank)){
            plugin.getLogger().warning("Player already has the group!!");
            return false;
        }
        permission.playerAddGroup((String)null, userName, rank);
        plugin.getLogger().warning("Set group to player");



        return true;
    }



}
