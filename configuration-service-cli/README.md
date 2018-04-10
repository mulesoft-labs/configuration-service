# Command Line Interface for Configuration Service

This package provides administrative tools for the configuration service API. Among the allowed tasks, the main purpose is to create backups and restore those backups into a working instance of the configuration service.

# Build the Tool

The tool currently is built for JDK 8. (no 9, no 7)

The tool is built on maven and it depends on `configuration-service-common`, thus this needs to be built first and installed into the local maven repository.

After this library has been built, you can proceed building the tool by running:

` $ mvn clean package appasembler:assemble `

This will create a `target/appasembler` directory, the binary distribution for the tool is located there.

The distribution already ships with a default configuration file in the conf/directory.

By running `bin/caas` or `bin/caas -h` you get the following output, describing all possible options:

```
$ bin/caas -h      
The execution of this tool will time out in 1 HOURS
usage: cmd1 {aruments} cmd2 {arguments} cmd3...
Commands are:
 -backup <location>                       Optional, the location where the
                                          backup will be placed
 -d,--create-default                      Create a default configuration
                                          file with dummy values in the
                                          working directory.
 -h,--help                                Prints the help
 -k,--generate-keys <keystore-password>   Generate keystores usable both
                                          for client and service.
 -restore <location>                      Restore a backup to the service.
                                          Optional param, the location
                                          where the backup will be read
Successfully completed all the tasks.
```

Commands can be chained or run at once, the order where the commands are specified is the order in which the commands will run.

Typical tasks to perform:

### Backup a Repo
1) Make sure your repo configuration is correct in `conf/settings.yaml`
2) Execute `bin/caas -backup` To backup the directory.

The backup will create a folder under the defined backups directory with the date and the time, and a file representation of the directory. Aside from this a keystore may be created to save the service encryption key (if the service supports encryption), and if so, a password will be prompted to protect the keystore. This encryption key will be used to re-encrypt the data, if necessary when restoring a backup.

### Restore a Backup
1) Make sure your repo configuration is correct in `conf/settings.yaml`.
2) Execute `bin/caas -restore` to restore the latest backup.

NOTE: the latest backup means the folder which was last modified in the backups folder and **not paying attention to the folder name**. If a specific backup needs to be restored, this can be specified through the command's argument: `bin/caas -restore backups/<backup-name>`

## Encrypt and Re-encrypt a Repo

This task is performed by backup and restore.

**To encrypt a repo:**

1) Backup the unenctypted repo.
2) Make sure the `decryptionEnabled` setting is configured to `false`.
3) Configure encryption in the CaaS Service.
4) Restore the backup. It will encrypt both properties and documents for the entire backup using the service's encryption key.


**To re-encrypt a repo:**
1) Backup the current repo. Making sure the CaaS Service encryption key is the one that works with the data.
2) Update the encryption key in CaaS Service.
3) Restore the backup.

All the data will be decrypted using the old encryption key and re-encrypted using the new encryption key.

## Generate new Client and Server Keystores

Initially, both client and service require 2 keys to be present in a keystore:

* Wrapping Key, allows secure transmission of the encryption key. The service mandates it to be a 'Blowfish' key.
* MAC Key, allows verification of the encyption key. The service mandates it to be an HMac-SHA256 key.

Additionally, the server requires the encryption key, this key may be of any of the supported keys by JCE, however we recommend to use AES, as its performance is boosted by hardware processing.

To generate the keys, simply run `bin/caas -k` and complete the interactive information.
