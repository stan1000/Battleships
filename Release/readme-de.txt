Schiffe Versenken (Battleships) V2.0
� 2005-2022 Stan Holoubek
http://www.stans-world.de/battleships.html
Kommentare bitte an stan07@online.de

Kurzbeschreibung:
Das beliebte Spiel als Client/Server Variante zum Spielen �ber das Internet oder im LAN.

Mindestsystemvoraussetzungen:
Applikation u. Dedizierter Server: Java 12 Runtime Environment

Features:
- Client/Server Applikation
- Sound bei Treffer, versenkt, usw. 
- frei konfigurierbare Sprache, Feldgr��e, Farben, etc.; s. server.cfg und config.htm f�r Optionen
- Schiffe per Maus-Drag frei positionierbar 
- Unterst�tzung von diagonalen Schiffen 
- Anpassung der Client-Konfiguration an die des Servers beim Verbindungsaufbau 
- Chat 
- Dedizierter Server 

Installation:
BattleShips.zip ins Verzeichnis BattleShips entpacken, config-xx.htm der bevorzugten Sprache umbenennen nach config.htm (Voreinstellung ist en) und zum Starten Doppelklick auf BattleShipsApplication.bat.
Linux: *.sh; siehe Kommentare f�r Einstellungen und Benutzung

Einstellungsdateien:
config.htm		Texte f�r alle Ausgaben in Applikation und Dediziertem Server, und einige weitere Einstellungen f�r die Applikation
server.cfg		- Generelle Spielkonfiguration f�r Applikation und Dedizierten Server
		- weitere Server-spezifische Einstellungen, wie z.B. Webserver Benutzung und Remote-Befehle
Applikation:
Nach dem Starten hat man die Wahl ob der eigene PC als Client oder als Server fungieren soll. Die zwei Radio-Buttons am unteren linken Rand des Fensters �ndern den Modus entsprechend. Die Schiffe verden positioniert indem man sie mit der Maus an die gew�nschte Stelle zieht. Die Richtung �ndert man per Rechtsklick. Nach dem Verbindungsaufbau wird der "Bereit"-Knopf aktiv. Wird dieser gedr�ckt, kann man seine Schiffe nicht mehr bewegen, hat der Gegner dies auch getan beginnt das Spiel. Es beginnt derjenige, der zuerst den "Bereit"-Knopf angeklickt hat. Jeder ist abwechselnd dran, es sei denn der letzte Schuss war ein Treffer. In diesem Fall hat man noch einen Schuss, d.h. man darf solange schie�en wie man trifft. Sowohl die eigenen als auch die gegnerischen Sch�sse werden markiert, der letzte Schuss ist farblich hervorgehoben. Treffer werden entsprechend angezeigt, auf dem gegnerischen Feld erscheint das entsprechende Segment des getroffenen Schiffes. Rest, siehe Benutzeroberfl�che (Status, Schiffe Verstecken, Punktestand, etc.).

Dedizierter Server:
Als Alternative zur Peer-to-Peer Verbindung - bei der einer der Mitspieler die Applikation im Server-Modus laufen l�sst - gibt es den dedizierten Server als Konsolen-Applikation. Die Anzahl der m�glichen Verbindungen ist hierbei nur durch die Hardware-Resourcen und Netzwerkanbindung des Server-Rechners limitiert.
Wenn sich ein Client mit dem Server verbindet wird ihm ein zufallsgenerierter Spielername zugewiesen und befindet sich zun�chst in einem virtuellen Wartezimmer. Der Spielername kann mithilfe des entsprechenden Eingabefeldes und "Einstellen" Knopfes auf der Benutzeroberfl�ches der Spiels ge�ndert werden. Wenn ein Client gerade nicht mit einem anderen verbunden ist erscheint ein Popup in dem der Benutzer einen Gegner ausw�hlen kann. 
Eine bestehende Verbindung mit einem anderen Client kann jederzeit getrennt werden indem man das rote Symbol in der rechten unteren Ecke der Spiel-UI anklickt.
Der Server startet ebenfalls den mitgelieferten Webserver sofern dies eingestellt wurde. Es ist m�glich Befehle in der lokalen Server-Konsole abzusetzen.

Unterst�tzte Befehle:
c - Client-Liste ausgeben
k - Spieler entfernen; Benutzung: k <Spielername>
h - Hilfe
x - Beenden

Es ist ebenfalls m�glich Remote-Befehle im Chateingabefeld eines jeden Clients abzusetzen, vorausgesetzt dieses Merkmal wurde in server.cfg aktiviert und der Benutzer hat sich mit dem korrekten Passwort eingeloggt. Jedem Befehl muss ein / vorangestellt werden:
 
Unterst�tzte Befehle:
c - Client-Liste ausgeben
k - Spieler entfernen; Benutzung: k <Spielername>
l - Login; Benutzung: l <Passwort>
h - Hilfe
x - Ausloggen
Beispiel: /l Passwort

Lokalisierung:
Die config(-xx).htm Dateien k�nnen in einer der g�ngigen Unicode Kodierungen (UTF-8, UTF-16 oder auch UTF-32) gespeichert werden, um die Benutzung von erweiterten Zeichens�tzen wie z.B. Chinesisch zu erm�glichen.
