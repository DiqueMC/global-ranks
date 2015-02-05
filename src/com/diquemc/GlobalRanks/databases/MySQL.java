package com.diquemc.GlobalRanks.databases;

import com.diquemc.GlobalRanks.GlobalRanks;
import org.bukkit.configuration.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQL extends SQL
{
    public MySQL( GlobalRanks plugin )
    {
        super(plugin);
    }

    protected Connection getNewConnection()
    {
        Configuration config = plugin.getConfig();

        try
        {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getString("database.port") + "/" + config.getString("database.database");

            return DriverManager.getConnection( url, config.getString( "database.user" ), config.getString( "database.password" ) );
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public String getName()
    {
        return "MySQL";
    }
}