#!/bin/sh
# cd: needed to be able to run from UI, set path relevant to your system
# sudo: only if you want to run in server mode, needed to create socket
# java: set path relevant to your system
cd /var/www/BattleShips
sudo /usr/local/java/bin/java -cp BattleShips.jar BattleShipsApplication