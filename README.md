# Sprummlbot

Sprummlbot is a fully customizable Teamspeak 3 Server Moderator bot powered by https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API.

  - Webinterace Included
  - Comepletely free
  - AFK Mover
  - Support Reminder
  - Anti Record feature

Sprummlbot is a lightweight Teamspeak 3 Server Bot.

### Version
0.1.7

### Latest Changes
#### 0.1.7
 - Fix fix fix...

#### 0.1.6
 - Fixed even more bugs...

### Downloads

You will just need to download [this] zip archive. (Sorry for no .tar.gz)

### Installation | Linux

You need Java, screen and unzip installed globally:

```sh
$ apt-get update
$ apt-get install openjdk-7-jre screen unzip
```
or Java 8 (only on Ubuntu 14.10 or above)
```sh
$ apt-get update
$ apt-get install openjdk-8-jre screen unzip
```

Then you will have to download the archive
```sh
$ wget -O Sprummlbot.zip https://www.dropbox.com/s/bsg4yv0p6owal08/Sprummlbot.zip?dl=1
$ unzip Sprummlbot.zip  -d /opt/sprummlbot
```

Last but not least you will need to setup your config file.
```sh
$ cd /opt/sprummlbot/ && nano config.ini
```

To start the bot just use
```sh
$ cd /opt/sprummlbot/ && screen java -jar Sprummlbot.jar
```

### Todos

 - Custom Commands
 - Better Error Processing

### Known Bugs
 - Loses connection on lagging servers.

License
----
GPL Version 2

**Free Software, Hell Yeah!**

[//]: ##
   [this]: <https://www.dropbox.com/s/bsg4yv0p6owal08/Sprummlbot.zip>

