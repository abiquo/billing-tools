CREATE DATABASE IF NOT EXISTS BILLING_BACKUP_RESULTS;

USE BILLING_BACKUP_RESULTS;

CREATE TABLE IF NOT EXISTS billing_backup_results (
    id int(10) DEFAULT 0,
    provider_id varchar(250) NOT NULL,
    name varchar(250) NOT NULL,
    size bigint(20) NOT NULL,
    creation_date datetime DEFAULT NULL,
    expiration_date datetime DEFAULT NULL,
    status varchar(20) NOT NULL,
    type varchar(50) NOT NULL,
    virtual_machine_id int(10) unsigned NOT NULL,
    version_c int(11) NOT NULL DEFAULT 0,
    storage varchar(255) DEFAULT NULL,
    replica tinyint(1) DEFAULT 0,
    enterprise_id int(11) DEFAULT NULL,
    PRIMARY KEY (id)    
);

