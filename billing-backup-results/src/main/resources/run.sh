#!/bin/bash

set -e

date +'%F %T'
echo "Executing Backup Billing: START"

cd /opt/abiquo-billing-backup-results/
java -jar billing-backup-results-${project.version}.jar backupresults -cf=config/config.yml -d 7 -r 4 | sudo tee --append /var/log/abiquo-billing-backup-results.log

date +'%F %T'
echo "Executing Backup Billing: END"
