package com.diquemc.GlobalRanks.databases;

import com.diquemc.GlobalRanks.GlobalRanks;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite extends SQL {
    private final GlobalRanks plugin;

    public SQLite(GlobalRanks plugin) {
        super(plugin);

        this.plugin = plugin;
    }

    protected Connection getNewConnection() {
        try {
            Class.forName("org.sqlite.JDBC");

            return DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "globalranks.db").getAbsolutePath());
        } catch (Exception e) {
            return null;
        }
    }

    public String getName() {
        return "SQLite";
    }
}
