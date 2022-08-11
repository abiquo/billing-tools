/**
 * Copyright (C) 2008 - Abiquo Holdings S.L. All rights reserved.
 *
 * Please see /opt/abiquo/tomcat/webapps/legal/ on Abiquo server
 * or contact contact@abiquo.com for licensing information.
 */
package com.abiquo.billing.backup;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(mixinStandardHelpOptions = true, version = "1.0", subcommands = {
BillingBackupResults.class})
public class Run
{

    public static void main(final String[] args)
    {
        CommandLine cl = new CommandLine(new Run());
        int exitCode = cl.execute(args);
        System.exit(exitCode);
    }

}
