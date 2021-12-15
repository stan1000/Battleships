Battleships
===========

Goal: to find developers who would like to bring it to the next level. I can't continue investing any time since my focus in life has changed.

1. I've started developing a bot for being able to play even when no other human players are connected to the server, but didn't get too far. Would love to see it become really sophisticated. UPDATE: taken up developing this as a hobby, bot is coming along nicely >:->
2. A port to Android would be brilliant; I've started to investigate but had to find how incompatible the Android UI classes are with AWT. Certainly an ambitious project!

Note: this is the content of the readme file that comes with the normal deployment ZIP; should give some ideas about all the features, etc.

README:
===========

Schiffe Versenken (Battleships) V2.0
© 2005-2021 Stan Holoubek
http://www.stans-world.de/
Please send comments to stan07@online.de

Short description:
The popular game as client/server edition for playing over the Internet or on the LAN.

Minimum system requirements:
Application and Dedicated Server: Java 12 Runtime Environment

Features:
- client/server application
- sound for hit, countersunk, etc. 
- configurable language, field size, colors, # of ships, etc.; see server.cfg and config.htm for options
- ships can be positioned freely via mouse drag 
- supports diagonal ships 
- client configuration changes to the server's when connecting 
- chat 
- dedicated server 

Installation:
Unzip BattleShips.zip to the folder BattleShips, rename config-xx.htm of the preferred language to config.htm (default is en) and double-click BattleShipsApplication.bat to start.
Linux: *.sh; see comments for settings and usage

Configuration files:
config.htm		Text strings for all output in the application and dedicated server, and further settings for the application
server.cfg		- general game configuration for the application and the dedicated server
		- further server specific setting, like e.g. use of webserver and remote commands

Application:
After starting the application you can choose between running your PC as client or as server. The two radio buttons at the bottom left corner of the window change the mode accordingly. To position the ships, drag them with the mouse to the desired place. Change the ship orientation by right-clicking. On establishing the connection the "Ready" button is activated. When it's pressed the ships are locked, when the opponent has done the same the game starts. The one who has clicked first begins. The players are taking turns except if the last shot was a hit, in which case the active player has another shot, i.e. you can shoot as often as you hit. Both the own and the opponent's shots are marked, the last shot is highlighted. Hits are shown in a similar way: on the opponent's field the according segment of the hit ship is made visible. For the rest, see UI (Status, hide ships, score, etc.).

Dedicated Server:
As an alternative to the peer-to-peer connection with one client running the application in server mode, there is the dedicated server console application. The amount of possible connections is only limited by the hardware resources and network connection of the server machine.
When a client connects to the server it is assigned a random player name and placed in a virtual waiting room. The player name can be changed using the according text box and "Set" button on the game user interface. If a client is currently not connected to another one a popup is displayed where the user can select who to play with. An existing connection with another user can be disconnected anytime by clicking the red icon in the right bottom border of the game UI.
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

Localization:
The config.htm files can be saved in any of the common Unicode encodings (UTF-8, UTF-16 or even UTF-32) to enable the use of extended character sets like e.g. Chinese.
