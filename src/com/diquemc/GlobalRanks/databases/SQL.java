package com.diquemc.GlobalRanks.databases;

import com.diquemc.GlobalRanks.GlobalRanks;
import com.diquemc.GlobalRanks.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class SQL {
    private Connection connection;
    private LinkedHashMap<String, PlayerRank> cache = new LinkedHashMap<String, PlayerRank>();

    protected GlobalRanks plugin;

    public SQL(GlobalRanks plugin) {
        this.plugin = plugin;

        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.createStatement().execute("/* ping */ SELECT 1");
                        updateTables();
                    }
                } catch (SQLException e) {
                    connection = getNewConnection();
                }
            }
        }, 60, 60 * 20);
    }

    protected abstract Connection getNewConnection();

    protected abstract String getName();

    public String getConfigName() {
        return getName().toLowerCase().replace(" ", "");
    }

    private ArrayList<LinkedHashMap<String, Object>> query(String sql, boolean hasReturn) {
        if (!checkConnection()) {
            plugin.getLogger().info("Error with database");
            return null;
        }

        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);

            if (!hasReturn) {
                statement.execute();
                return null;
            }

            ResultSet set = statement.executeQuery();

            ResultSetMetaData md = set.getMetaData();
            int columns = md.getColumnCount();

            ArrayList<LinkedHashMap<String, Object>> list = new ArrayList<LinkedHashMap<String, Object>>(50);

            while (set.next()) {
                LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>(columns);
                for (int i = 1; i <= columns; ++i) {
                    row.put(md.getColumnName(i), set.getObject(i));
                }
                list.add(row);
            }

            if (list.isEmpty()) {
                return null;
            }

            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean checkConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = getNewConnection();

                if (connection == null || connection.isClosed()) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    private void updateTables() {
        query("CREATE TABLE IF NOT EXISTS GlobalRanks (uuid varchar(36) NOT NULL, name varchar(32) NOT NULL, rank varchar(64) NOT NULL, creation BIGINT, expiration BIGINT, PRIMARY KEY (uuid))", false);

    }

    public void disconnect() {
        cache.clear();

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerRank getRank(OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();

        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }

        ArrayList<LinkedHashMap<String, Object>> data = query("SELECT * FROM GlobalRanks WHERE uuid = '" + uuid + "';", true);
        if (data == null) {
            // Store null to avoid spammy queries.
            cache.put(uuid, null);

            return null;
        }

        PlayerRank pr = new PlayerRank(data.get(0));

        cache.put(uuid, pr);

        return pr;
    }

    public void removeFromCache(OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        if (cache.containsKey(uuid)) {
            cache.remove(uuid);
        }
    }

    public void clearCache() {
        cache = new LinkedHashMap<String, PlayerRank>();
    }

    public void uploadPlayerRank(OfflinePlayer player, PlayerRank pr) {

        String uuid = player.getUniqueId().toString();
        String name = player.getName();
        String rank = pr.getTargetRankName();
        long creation = 0;
        if (pr.creationDate != null) {
            creation = pr.creationDate.getTime();
        }
        long expiration = 0;
        if (pr.expirationDate != null) {
            expiration = pr.expirationDate.getTime();
        }
        cache.put(uuid, pr);
        String q = "REPLACE INTO GlobalRanks (uuid, name,rank,creation,expiration) VALUES ('" + uuid + "','" + name + "','" + rank + "','" + creation + "','" + expiration + "');";
        Bukkit.getLogger().info(q);
        query(q, false);
    }

    public void deletePlayer(OfflinePlayer player) {
        String uuid = player.getUniqueId().toString();
        cache.put(uuid, null);
        query("DELETE FROM GlobalRanks WHERE uuid = '" + uuid + "';", false);
    }

}
