/**
 * Copyright (C) 2008 - Abiquo Holdings S.L. All rights reserved.
 *
 * Please see /opt/abiquo/tomcat/webapps/legal/ on Abiquo server
 * or contact contact@abiquo.com for licensing information.
 */
package com.abiquo.billing.backup.database;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.yaml.snakeyaml.Yaml;

public class BillingBackupResultsDatabase
{
    private static final String SYSTEM_ZONE_ID = ZoneId.systemDefault().getId();

    private static final String TABLE_NAME = "billing_backup_results";

    private static String tableUrl;

    private static String database;

    private static String databaseHost;

    private static String databasePort;

    private static String databaseUrl;

    private static String databaseUser;

    private static String databasePassword;

    public static void createDatabaseAndTable(final String configPath)
        throws IOException, SQLException
    {
        setDatabase(configPath);
        try (
            Connection c = DriverManager.getConnection(
                databaseUrl + "?serverTimezone=" + SYSTEM_ZONE_ID, databaseUser, databasePassword);
            Reader reader = new BufferedReader(new FileReader("schema.sql")))
        {
            ScriptRunner sr = new ScriptRunner(c);
            sr.runScript(reader);
        }
    }

    public static void copyBackupResults(final Integer date) throws SQLException
    {
        String kintonUrl = "jdbc:mariadb://" + databaseHost + ":" + databasePort
            + "/kinton?serverTimezone=" + SYSTEM_ZONE_ID;
        String query =
            "SELECT b.id, b.provider_id, b.name, b.size, b.creation_date, b.expiration_date, b.status, b.type, b.virtual_machine_id, b.storage, b.replica, v.idEnterprise FROM backup_result b LEFT JOIN virtualmachine v ON b.virtual_machine_id = v.idVM";

        try (Connection c = DriverManager.getConnection(kintonUrl, databaseUser, databasePassword);
            PreparedStatement s = c.prepareStatement(query);
            ResultSet rs = s.executeQuery();)
        {
            while (rs.next())
            {
                if (isNewResult(rs.getTimestamp(5), date))
                {
                    insertBackupResult(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4),
                        rs.getTimestamp(5), rs.getTimestamp(6), rs.getString(7), rs.getString(8),
                        rs.getInt(9), rs.getString(10), rs.getBoolean(11), rs.getInt(12));
                }
            }
        }
    }

    public static void insertBackupResult(final Integer id, final String providerId,
        final String name, final Integer size, final Timestamp creationDate,
        final Timestamp expirationDate, final String status, final String type,
        final Integer virtualMachineId, final String storage, final boolean replica,
        final Integer enterpriseId) throws SQLException
    {
        String query = "INSERT INTO " + TABLE_NAME + "(id, provider_id, name, size, creation_date, "
            + "expiration_date, status, type, virtual_machine_id, storage, replica, enterprise_id)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE status = ?";
        try (Connection c = DriverManager.getConnection(tableUrl, databaseUser, databasePassword);
            PreparedStatement s = c.prepareStatement(query))
        {
            s.setInt(1, id);
            s.setString(2, providerId);
            s.setString(3, name);
            s.setInt(4, size);
            s.setTimestamp(5, creationDate);
            s.setTimestamp(6, expirationDate);
            s.setString(7, status);
            s.setString(8, type);
            s.setInt(9, virtualMachineId);
            s.setString(10, storage);
            s.setBoolean(11, replica);
            s.setInt(12, enterpriseId);
            s.setString(13, status);

            s.execute();
        }
    }

    public static void removeBackupResults(final Integer date) throws SQLException
    {
        String querySelect = "SELECT id, creation_date FROM " + TABLE_NAME;

        try (Connection c = DriverManager.getConnection(tableUrl, databaseUser, databasePassword);
            PreparedStatement s = c.prepareStatement(querySelect);
            ResultSet rs = s.executeQuery();)
        {
            while (rs.next())
            {
                if (isOlderMonth(rs.getTimestamp(2), date))
                {
                    deleteResult(rs.getInt(1));
                }
            }
        }
    }

    private static void deleteResult(final Integer id) throws SQLException
    {
        String queryRemove = "DELETE from " + TABLE_NAME + " WHERE id = ?";

        try (Connection c = DriverManager.getConnection(tableUrl, databaseUser, databasePassword);
            PreparedStatement s = c.prepareStatement(queryRemove))
        {
            s.setInt(1, id);

            s.execute();
        }
    }

    private static boolean isNewResult(final Timestamp creationDate, final Integer date)
    {
        Date dayBorder = Date.valueOf(LocalDate.now().minusDays(date));

        return creationDate.compareTo(dayBorder) > 0;
    }

    private static boolean isOlderMonth(final Timestamp creationDate, final Integer date)
    {
        Date monthBorder = Date.valueOf(LocalDate.now().minusMonths(date));

        return creationDate.compareTo(monthBorder) < 0;
    }

    private static void setDatabase(final String configPath) throws IOException
    {
        Yaml yaml = new Yaml();
        try (InputStream in = new FileInputStream(configPath))
        {
            Map<String, Map<String, String>> yml = yaml.load(in);
            Map<String, String> creds = yml.get("billing_database");
            databaseHost = creds.get("host");
            databaseUser = creds.get("user");
            databasePassword = creds.get("password");
            databasePort = String.valueOf(creds.get("port"));
            database = creds.get("database");
            databaseUrl = "jdbc:mariadb://" + databaseHost + ":" + databasePort + "/";
            tableUrl = databaseUrl + database + "?serverTimezone=" + SYSTEM_ZONE_ID;
        }
    }
}
