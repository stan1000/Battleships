Schiffe Versenken (Battleships) V1.1
© 2005-2013 Stan Holoubek
http://www.stans-world.de/
Kommentare bitte an stan07@online.de

Kurzbeschreibung:
Das beliebte Spiel als Client/Server Variante zum Spielen über das Internet oder im LAN.

Mindestsystemvoraussetzungen:
Applikation u. Dedizierter Server: Java 1.3.1 Runtime Environment
Applet: Brauser mit Java 1.1 VM

Features:
- Client/Server Applikation, Client Applet 
- Sound bei Treffer, versenkt, usw. 
- frei konfigurierbare Sprache, Feldgröße, Farben, etc.; s. server.cfg und config.htm für Optionen
- Schiffe per Maus-Drag frei positionierbar 
- Unterstützung von diagonalen Schiffen 
- Anpassung der Client-Konfiguration an die des Servers beim Verbindungsaufbau 
- einfacher Webserver für eine komfortablere Benutzung des Applets 
- Chat 
- Dedizierter Server 

Installation:
BattleShips.zip ins Verzeichnis BattleShips entpacken, config-xx.htm der bevorzugten Sprache umbenennen nach config.htm (Voreinstellung ist en) und zum Starten Doppelklick auf BattleShipsApplication.bat.
Linux: *.sh; siehe Kommentare für Einstellungen und Benutzung

Einstellungsdateien:
config.htm		Texte für alle Ausgaben in Applet, Applikation und Dediziertem Server, Client Port für Applet und einige weitere Einstellungen für die Applikation
server.cfg		- Generelle Spielkonfiguration für Applikation und Dedizierten Server; das Applet bezieht diese beim Verbinden vom Server
		- weitere Server-spezifische Einstellungen, wie z.B. Webserver Benutzung und Remote-Befehle
Applikation:
Nach dem Starten hat man die Wahl ob der eigene PC als Client oder als Server fungieren soll. Die zwei Radio-Buttons am unteren linken Rand des Fensters ändern den Modus entsprechend. Die Schiffe verden positioniert indem man sie mit der Maus an die gewünschte Stelle zieht. Die Richtung ändert man per Rechtsklick. Nach dem Verbindungsaufbau wird der "Bereit"-Knopf aktiv. Wird dieser gedrückt, kann man seine Schiffe nicht mehr bewegen, hat der Gegner dies auch getan beginnt das Spiel. Es beginnt derjenige, der zuerst den "Bereit"-Knopf angeklickt hat. Jeder ist abwechselnd dran, es sei denn der letzte Schuss war ein Treffer. In diesem Fall hat man noch einen Schuss, d.h. man darf solange schießen wie man trifft. Sowohl die eigenen als auch die gegnerischen Schüsse werden markiert, der letzte Schuss ist farblich hervorgehoben. Treffer werden entsprechend angezeigt, auf dem gegnerischen Feld erscheint das entsprechende Segment des getroffenen Schiffes. Rest, siehe Benutzeroberfläche (Status, Schiffe Verstecken, Punktestand, etc.).

Applet:
Sehr praktisch wenn man mit jemandem spielen will der keine Java-Laufzeitumgebung installiert hat, aber einen Java-fähigen Browser besitzt. Zunächst einmal: ein unsigniertes Java-Applet kann nur zu dem Host eine Verbindung aufbauen von dem es geladen wurde. Dies ist kein Bug sondern ein Sicherheitsfeature aller korrekten JVM Implementierungen (welches manchmal durch eine entsprechende JVM Einstellung aufgehoben werden kann).
Es gibt zwei Möglichkeiten dies zu erreichen:
1. Den UseWebServer Parameter in server.cfg auf 1 setzen, dann startet die Applikation einen einfachen Webserver auf dem voreingestellten Port (ebenfalls in server.cfg) sobald man im Servermodus den "Start" Knopf anklickt. Der Spielpartner kann sich dann mittels eines Browsers mit deinem Rechner verbinden.
2. Man hat einen Webserver (wie z.B. Apache) auf demselben PC installiert, auf dem die Applikation im Servermodus läuft. Einfach das BattleShips-Verzeichnis als virtuelles Verzeichnis dem Web-Root hinzufügen, die Datei default.htm entsprechend deiner Webserver-Software umbenennen (z.B. nach index.html), fertig.

Dedizierter Server:
Als Alternative zur Peer-to-Peer Verbindung - bei der einer der Mitspieler die Applikation im Server-Modus laufen lässt - gibt es den dedizierten Server als Konsolen-Applikation. Die Anzahl der möglichen Verbindungen ist hierbei nur durch die Hardware-Resourcen und Netzwerkanbindung des Server-Rechners limitiert.
Wenn sich ein Client mit dem Server verbindet wird ihm ein zufallsgenerierter Spielername zugewiesen und befindet sich zunächst in einem virtuellen Wartezimmer. Der Spielername kann mithilfe des entsprechenden Eingabefeldes und "Einstellen" Knopfes auf der Benutzeroberfläches der Spiels geändert werden. Wenn ein Client gerade nicht mit einem anderen verbunden ist erscheint ein Popup in dem der Benutzer einen Gegner auswählen kann. 
Eine bestehende Verbindung mit einem anderen Client kann jederzeit getrennt werden indem man das rote Symbol in der rechten unteren Ecke der Spiel-UI anklickt.
Der Server startet ebenfalls den mitgelieferten Webserver sofern dies eingestellt wurde. Es ist möglich Befehle in der lokalen Server-Konsole abzusetzen.

Unterstützte Befehle:
c - Client-Liste ausgeben
k - Spieler entfernen; Benutzung: k <Spielername>
h - Hilfe
x - Beenden

Es ist ebenfalls möglich Remote-Befehle im Chateingabefeld eines jeden Clients abzusetzen, vorausgesetzt dieses Merkmal wurde in server.cfg aktiviert und der Benutzer hat sich mit dem korrekten Passwort eingeloggt. Jedem Befehl muss ein / vorangestellt werden:
 
Unterstützte Befehle:
c - Client-Liste ausgeben
k - Spieler entfernen; Benutzung: k <Spielername>
l - Login; Benutzung: l <Passwort>
h - Hilfe
x - Ausloggen
Beispiel: /l Passwort

Lokalisierung:
Die config(-xx).htm Dateien können in einer der gängigen Unicode Kodierungen (UTF-8, UTF-16 oder auch UTF-32) gespeichert werden, um die Benutzung von erweiterten Zeichensätzen wie z.B. Chinesisch zu ermöglichen.
