# billing-backup-results

This repository contains the script for retrieving and storing in a database the backup results.

## Configuration

To use properly this tool it is necessary to fill the configuration file in a folder named config in the directory where it's going to be used

The config.yml file contains the configuration for the use of the configuration for the database that's going to be used

```
billing_database:
   host:
   user:
   password:
   port:
   database: BILLING_BACKUP_RESULTS
```

## Usage

Once the jar file is generated (``mvn clean install``) in the target folder, you can use it with:
``java -jar billing-backup-results-1.0.0-SNAPSHOT.jar backupresults -cf config.yml``
The jar will process backup results created in last 7 days by default.
You can specify number of days you want to obtain by adding the parameter ``-d ``.
The jar also remove old results created more than 6 months ago by default.
You can specify months you want to remove by adding the parameter ``-r ``.
Example: ``java -jar billing-backup-results-1.0.0-SNAPSHOT.jar backupresults -cf config.yml -d 5 -r 9``
There will be also a generated tar.gz with all the necessary files.
