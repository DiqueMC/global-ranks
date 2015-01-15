package com.diquemc.GlobalRanks.command;

import com.diquemc.GlobalRanks.GlobalRanks;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.util.org.apache.commons.lang3.ObjectUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;


public class SetRankCommand implements CommandExecutor {
    private final GlobalRanks plugin;
    private static Permission permission = null;

    public SetRankCommand(GlobalRanks plugin) {
    		this.plugin = plugin; // Store the plugin in situations where you need it.
            setupPermissions();
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
	@Override
	public final boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
//		if(args.length < 2) {
//			return false;
//		}
		try {
            permission.playerAddGroup((String)null,sender.getName(), "vipHierro");
            sender.sendMessage("DOneeeee");
		}
		catch(final Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

}
