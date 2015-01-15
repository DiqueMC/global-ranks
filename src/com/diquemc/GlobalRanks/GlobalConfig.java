package com.diquemc.GlobalRanks;

public class GlobalConfig {

    private ConfigAccessor globalConfig;
    public GlobalConfig(GlobalRanks p) {
        globalConfig = new ConfigAccessor(p,"global.yml");
        globalConfig.reloadConfig();
        globalConfig.saveDefaultConfig();
    }
}