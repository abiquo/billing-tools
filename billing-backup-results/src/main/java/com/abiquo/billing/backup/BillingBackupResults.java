/**
 * Copyright (C) 2008 - Abiquo Holdings S.L. All rights reserved.
 *
 * Please see /opt/abiquo/tomcat/webapps/legal/ on Abiquo server
 * or contact contact@abiquo.com for licensing information.
 */
package com.abiquo.billing.backup;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.billing.backup.database.BillingBackupResultsDatabase;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "backupresults", mixinStandardHelpOptions = false, version = "1.0", description = "Store in DB the backup results for billing purpose")
public class BillingBackupResults implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(BillingBackupResults.class);

    private static final Integer DEFAULT_DAYS_TO_OBTAIN = 7;

    private static final Integer DEFAULT_MONTHS_TO_REMOVE = 6;

    @Option(names = {"-cf",
    "--config-file"}, description = "Path for the configuration file used", required = true)
    private String configPath;

    @Option(names = {"-d",
    "--date"}, description = "Days of backup results to obtain", required = false)
    private Integer daysToObtain;

    @Option(names = {"-r",
    "--remove"}, description = "Months of backup results to remove", required = false)
    private Integer monthsToRemove;

    @Override
    public void run()
    {
        if (daysToObtain == null)
        {
            daysToObtain = DEFAULT_DAYS_TO_OBTAIN;
        }
        if (monthsToRemove == null)
        {
            monthsToRemove = DEFAULT_MONTHS_TO_REMOVE;
        }

        try
        {
            BillingBackupResultsDatabase.createDatabaseAndTable(configPath);

            logger.info("Database created");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Cannot create Database", e);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Cannot load configuration", e);
        }

        try
        {
            BillingBackupResultsDatabase.copyBackupResults(daysToObtain);

            logger.info("Backup results inserted into database");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Cannot insert Backup results to database", e);
        }

        try
        {
            BillingBackupResultsDatabase.removeBackupResults(monthsToRemove);

            logger.info("Old backup results removed from database");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Removal of old backup results from database failed", e);
        }

    }

}
