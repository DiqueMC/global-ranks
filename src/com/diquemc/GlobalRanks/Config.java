package com.diquemc.GlobalRanks;

import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;

public class Config {
	public static boolean getBoolean(String property) {
		return GlobalRanks.getInstance().getConfig().getBoolean(property);
	}

	public static int getInt(String property) {
		return GlobalRanks.getInstance().getConfig().getInt(property);
	}
	
	public static String getString(String property) {
		return GlobalRanks.getInstance().getConfig().getString(property);
	}

	public static String getString(String property, Object... args) {
		return String.format(GlobalRanks.getInstance().getConfig().getString(property), args);
	}

	
	public static boolean contains(String property) {
		return GlobalRanks.getInstance().getConfig().contains(property);
	}
	
	public static  List<String> getStringList(String property) {
		return GlobalRanks.getInstance().getConfig().getStringList(property);
	}
	
	public static void checkForMissingProperties() throws IOException, InvalidConfigurationException {
		YML diskConfig = new YML(GlobalRanks.getInstance().getDataFolder(), "config.yml");
		YML defaultConfig = new YML(GlobalRanks.getInstance().getResource("config.yml"));

		for (String property : defaultConfig.getKeys(true)) {
			if (!diskConfig.contains(property))
				Logger.warning(GlobalRanks.chatPrefix + property + " is missing from your config.yml, using default.");
		}
	}
}
