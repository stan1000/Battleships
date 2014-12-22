#!/bin/sh
# used to run on init e.g. from /etc/rc.local
# cd & java: set path relevant to your system
cd /var/www/BattleShips
/usr/local/java/bin/java -cp BattleShips.jar BattleShipsServerApplication Daemon >> BattleShipsServerApplication.log 2>&1 &