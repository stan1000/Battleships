Battleships
===========

Schiffe Versenken (Battleships) V2.0  
© 2005-2022 Stan Holoubek  
http://www.stans-world.de/battleships.html  
http://www.stans-world.de/schiffe_versenken.html  
Please send comments to battleships@stans-world.de  

Short description:
The popular game as client/server edition for playing over the Internet or on the LAN.

Minimum system requirements:
Java 8 Runtime Environment

Features
====================
- client/server application
- sound for hit, countersunk, etc. 
- configurable language, field size, colors, # of ships, etc.; see server.cfg and config.htm for options
- ships can be positioned freely via mouse drag 
- supports diagonal ships 
- client configuration changes to the server's when connecting 
- chat 
- dedicated server
- bot

Installation
====================
Unzip BattleShips.zip to the folder BattleShips, rename config-xx.htm of the preferred language to config.htm (default is en) and double-click BattleShipsApplication.bat to start.  
Linux: *.sh; see comments for settings and usage

Configuration files
====================
config.htm		Text strings for all output in the application and dedicated server, and further settings for the application  
server.cfg		- general game configuration for the application and the dedicated server  
				- further server specific setting, like e.g. use of webserver and remote commands  

Main Application: BattleShipsApplication
========================================
After starting the application you can choose between running your PC as client or as server. The two radio buttons at the bottom left corner of the window change the mode accordingly. To position the ships, drag them with the mouse to the desired place. Change the ship orientation by right-clicking. On establishing the connection the "Ready" button is activated. When it's pressed the ships are locked, when the opponent has done the same the game starts. The one who has clicked first begins. The players are taking turns except if the last shot was a hit, in which case the active player has another shot, i.e. you can shoot as often as you hit. Both the own and the opponent's shots are marked, the last shot is highlighted. Hits are shown in a similar way: on the opponent's field the according segment of the hit ship is made visible. For the rest, see UI (Status, hide ships, score, etc.).

Command-line parameters:  
-action <name>  
possible actions
- startserver: Runs the application in "Server mode" and starts the server, i.e. starts listening for a client connection  
- startbot: Runs the application in "Computer mode", starts the server and launches an instance of BattleShipsBot in invisible mode that'll connect to the application. By this it's possible to play locally against the computer without having to launch the BattleShipsBot application separately

Dedicated Server: BattleShipsServerApplication
===============================================
As an alternative to the peer-to-peer connection with one client running the main application in server mode, there is the dedicated server console application. The amount of possible connections is only limited by the hardware resources and network connection of the server machine.  
When a client connects to the server it is assigned a random player name and placed in a virtual waiting room. The player name can then be changed using the according text box and "Set" button on the game UI. If a client is currently not connected to another one, a popup is displayed in the application where the user can select who to play with. An existing connection with another user can be disconnected anytime by clicking the red icon in the right bottom border of the game UI.
The server also runs the web server if configured to. It's possible to issue commands in the local server console.

Supported commands:  
c - Print client list  
k - Kick player; usage: k <player name>  
h - Help  
x - Exit  

It's also possible to issue commands remotely from the chat input box of each client, provided this feature has been enabled in server.cfg and the user has logged in with the correct remote console password. Each command has to be prefixed with a /.
 
Supported commands:  
c - Print client list  
k - Kick player; usage: k <player name>  
l - Login; usage: l <Rcon Password>  
h - Help  
x - Exit/Logout  
Example: /l password

Bot: BattleShipsBot
====================
The BattleShipsBot application enables you to play against the computer. It can connect to the main application running in server mode or to the dedicated server, showing up in the virtual waiting room just like a human player, by the name assigned on startup via a command line parameter (complete list below). After selecting the bot as your adversary in the application popup, you position your ships as ever and click the "Ready" button. This will prompt the bot to also send a ready-signal and the game is on!

Command-line parameters:  
-name <botname>  
	The name of the bot when connecting to the dedicated server; it can also be set in the player name field if the bot is running in visible mode  
-server <hostname>  
	The server to connect to, like stan1000.dynv6.net; default: localhost (see config.htm for port setting)  
-invisible  
	hide the bot UI; Note: in difference to the dedicated server, the bot is still a UI application and only works when a GUI is present - something to consider on small footprint Linux-based servers, like e.g. the Raspberry Pi  
-timeout <seconds>  
	Time of inactivity after which the bot will disconnect when connected to a player on a dedicated server; default: 600  

Localization
====================
The config.htm files can be saved in any of the common Unicode encodings (UTF-8, UTF-16 or even UTF-32) to enable the use of extended character sets like e.g. Chinese.
