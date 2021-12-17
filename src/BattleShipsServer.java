/*
Battleships - The popular game as client/server edition for playing with a friend over the Internet (or on the LAN).
Copyright (C) 2006-2022 Stan's World
http://www.stans-world.de/battleships.html

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details:
http://www.gnu.org/licenses/gpl.html

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class BattleShipsServer extends Thread implements BattleShipsConnectionListener {

    private ServerSocketThread m_oServerSocketThread;
    private boolean m_bRunning;
	private Hashtable<String, BattleShipsConnection> m_oHtBattleShipsConnection;
	private Hashtable<String, String> m_oHtClientMapping;
	private String m_sCurrentConfig;
	private String m_sFreeSocketIdentifier;
	private boolean m_bUseWebServer;
	private int m_iWebServerPort;
	private WebServer m_oWebServer;
	private int m_iPort;
	private boolean m_bAllowRemoteAdmin;
	private String m_sRconPassword;
	private ArrayList<String> m_oVcLoggedIn;
	private BattleShipsUtility m_oUtil;
	private PrintWriter m_bwLog;
	private Hashtable<String, String> m_htPlayerName;

	private final boolean DEBUG = false;

	public BattleShipsServer(PrintWriter bwLog) {
		int iShipType;
		int iShipTypeCount;

		m_bwLog = bwLog;
		m_oUtil = new BattleShipsUtility();
		if (m_oUtil.readParameters()) {
			m_bAllowRemoteAdmin = (m_oUtil.parseIntParm("AllowRemoteAdmin", 0) == 1 ? true : false);
			m_sRconPassword = m_oUtil.getParameter("RconPassword", "");
			m_iPort = m_oUtil.parseIntParm("ServerPort", 1000);
			if (m_iPort < 1 || m_iPort > 65535) m_iPort = 1000;
			int iCellWidth = m_oUtil.parseIntParm("CellWidth", 10);
			int iFieldWidth = m_oUtil.parseIntParm("FieldWidth", 29);
			m_sCurrentConfig = iCellWidth + ";" + iFieldWidth;
			for (iShipType = 1; iShipType <= BattleShipsUtility.MAX_SHIP_TYPE; iShipType++) {
				iShipTypeCount = m_oUtil.parseIntParm("ShipTypeCount" + iShipType, 1);
				m_sCurrentConfig += ";" + iShipTypeCount;
			}
		} else {
			log(m_oUtil.getErrorMessage());
			System.exit(1);
		}
	}
	
	public void start() {
		if (!m_bRunning) {
			log(new Date().toString());
			try {
				m_oServerSocketThread = new ServerSocketThread(this, m_iPort);
				m_oHtBattleShipsConnection = new Hashtable<String, BattleShipsConnection>();
				m_oHtClientMapping = new Hashtable<String, String>();
				m_oVcLoggedIn = new ArrayList<String>();
				m_htPlayerName = new Hashtable<String, String>();
				m_sFreeSocketIdentifier = "";
				log(MessageFormat.format(getString("ServerStarting"), 
										 new Object[]{BattleShipsUtility.VERSION, Integer.toString(m_iPort)}));

				boolean bUseWebServer = (m_oUtil.parseIntParm("UseWebServer", 0) == 1 ? true : false);
				boolean bWebServerLog = (m_oUtil.parseIntParm("WebServerLog", 0) == 1 ? true : false);
				int iWebServerPort = m_oUtil.parseIntParm("WebServerPort", 80);
				if (iWebServerPort < 1 || iWebServerPort > 65535 || iWebServerPort == m_iPort) iWebServerPort = 80;
				if (bUseWebServer) {
					startWebServer(iWebServerPort, bWebServerLog);
				}
			} catch (IOException e) {
				log(getString("Error") + " " + MessageFormat.format(getString("ServerPortInUse"),
										new Object[]{String.valueOf(m_iPort)}));
				if (DEBUG) e.printStackTrace();
				System.exit(1);
			}
			m_bRunning = true;
			super.start();
		}
	}
	
	public void interrupt() {
		BattleShipsConnection oSchSocket = null;
		if (m_bRunning) {
			super.interrupt();
			m_bRunning = false;
			m_oServerSocketThread.interrupt();
			Enumeration oEnum = m_oHtBattleShipsConnection.elements();
			while (oEnum.hasMoreElements()) {
				oSchSocket = (BattleShipsConnection)oEnum.nextElement();
				oSchSocket.interrupt();
			}
			log(getString("ServerStopping"));
			if (m_oWebServer != null) {
				m_oWebServer.interrupt();
			}
		}
	}

	public void run() {}
	
	public synchronized void clientConnected(Socket socket) {
		String sSocketIdentifier = socket.toString();
		log(getString("Connected2") + " " + sSocketIdentifier);
        BattleShipsConnection oSchSocket = new BattleShipsConnection((BattleShipsConnectionListener)this, socket, sSocketIdentifier);
		m_htPlayerName.put(oSchSocket.getPlayerName(), sSocketIdentifier);
		oSchSocket.start();
		m_oHtBattleShipsConnection.put(sSocketIdentifier, oSchSocket);
	}
	
	public void socketDataArrived(String sMessage, String sData) {};

	public synchronized void socketDataArrived(String sMessage, String sData, String sSocketIdentifier) {
		if (DEBUG) System.out.println("socketDataArrived: " + sMessage + " - " + sData + " ID: " + sSocketIdentifier);
		String sMappedSocketIdentifier;
		BattleShipsConnection oSchSocket;
		if (sMessage.equals("disconnected")) {
			handleDisconnect(sSocketIdentifier);
		} else if (sMessage.equals("status")) {
			if (sData.equals("Connected")) {
				sendMessage("config", m_sCurrentConfig, sSocketIdentifier);
				sendMessage("status", "Waiting", sSocketIdentifier);
				broadcastClientList();
			}
		} else if (sMessage.equals("servercommand")) {
			handleRemoteServerCommand(sData, sSocketIdentifier);
		} else if (sMessage.equals("setplayername")) {
			if (m_htPlayerName.containsKey(sData)) {
				sendMessage("playernameexists", sData, sSocketIdentifier);
			} else {
				oSchSocket = (BattleShipsConnection)m_oHtBattleShipsConnection.get(sSocketIdentifier);
				m_htPlayerName.remove(oSchSocket.getPlayerName());
				m_htPlayerName.put(sData, sSocketIdentifier);
				oSchSocket.setPlayerName(sData);
				sendMessage("playernameset", sData, sSocketIdentifier);
				broadcastClientList();
				log(getString("PlayerNameSet2") + " " + sSocketIdentifier + " - " + sData);
			}
		} else if (sMessage.equals("requestgame")) {
			sMappedSocketIdentifier = m_htPlayerName.get(sData);
			if (sMappedSocketIdentifier != null) {
				if (!m_oHtClientMapping.containsKey(sSocketIdentifier) &&
					!m_oHtClientMapping.containsKey(sMappedSocketIdentifier)) {
					m_oHtClientMapping.put(sSocketIdentifier, sMappedSocketIdentifier);
					m_oHtClientMapping.put(sMappedSocketIdentifier, sSocketIdentifier);
					sendMessage("enemyfound", sData, sSocketIdentifier);
					try {
						sleep(100);
					} catch (InterruptedException e) {}
					oSchSocket = (BattleShipsConnection)m_oHtBattleShipsConnection.get(sSocketIdentifier);
					sendMessage("enemyfound", oSchSocket.getPlayerName(), sMappedSocketIdentifier);
					broadcastClientList();
					log(getString("ClientsConnected") + " " + sSocketIdentifier + " - " + sMappedSocketIdentifier);
				}
			}
		} else if (sMessage.equals("disconnectenemy")) {
			sMappedSocketIdentifier = (String)m_oHtClientMapping.remove(sSocketIdentifier);
			if (sMappedSocketIdentifier != null) {
				m_oHtClientMapping.remove(sMappedSocketIdentifier);
				sendMessage("status", "EnemyLeft", sMappedSocketIdentifier);
				try {
					sleep(100);
				} catch (InterruptedException e) {}
				sendMessage("status", "EnemyLeft", sSocketIdentifier);
				log(getString("ClientsDisconnected") + " " + sSocketIdentifier + " - " + sMappedSocketIdentifier);
			}
			broadcastClientList();
		} else {
			//if (DEBUG) System.out.println("Server::socketDataArrived: " + sMessage + " - " + sData + "ID: " + sSocketIdentifier);
			if (sMessage.equals("chat")) {
				log(sSocketIdentifier + ": " + sData);
			}
			sMappedSocketIdentifier = m_oHtClientMapping.get(sSocketIdentifier);
			if (sMappedSocketIdentifier != null) {
				sendMessage(sMessage, sData, sMappedSocketIdentifier);
			}
		}
		if (DEBUG) System.out.println("Server::socketDataArrived: Current hashmap size: " + m_oHtBattleShipsConnection.size());
		if (DEBUG) System.out.println("Server::socketDataArrived: Current Client Mapping size: " + m_oHtClientMapping.size() + " m_sFreeSocketIdentifier: " + m_sFreeSocketIdentifier);
	}
	
	public void printClientList() {
		log(getString("ClientList") + "\n" + getClientList("", false));
	}
	
	public String getHelpMsg() {
		return ("================\n" + MessageFormat.format(getString("ServerHelpMsg"),
										new Object[]{"\n"}) + "\n================");
	}

	public String kickPlayer(String playerName) {
		String ret;
		String sSocketIdentifier;
		if (m_htPlayerName.containsKey(playerName)) {
			sSocketIdentifier = m_htPlayerName.get(playerName);
			ret = handleDisconnect(sSocketIdentifier);
		} else {
			ret = getString("NoSuchPlayer");
			log(ret);
		}
		return ret;
	}
	
	public synchronized void socketError(String sType, String sData, String sSocketIdentifier) {
		handleDisconnect(sSocketIdentifier);
	}

	public void socketError(String sType, String sData) {}

	private String handleDisconnect(String sSocketIdentifier) {
		String ret;
		String sMappedSocketIdentifier;
		BattleShipsConnection oSchSocket = (BattleShipsConnection)m_oHtBattleShipsConnection.remove(sSocketIdentifier);
		String playerName = oSchSocket.getPlayerName();
		if (m_htPlayerName.containsKey(playerName)) {
			m_htPlayerName.remove(playerName);
		}
		if (m_oVcLoggedIn.contains(sSocketIdentifier)) {
			m_oVcLoggedIn.remove(sSocketIdentifier);
		}
		if (sSocketIdentifier.equals(m_sFreeSocketIdentifier)) {
			m_sFreeSocketIdentifier = "";
		}
		sMappedSocketIdentifier = m_oHtClientMapping.remove(sSocketIdentifier);
		if (sMappedSocketIdentifier != null) {
			m_oHtClientMapping.remove(sMappedSocketIdentifier);
			sendMessage("status", "EnemyLeft", sMappedSocketIdentifier);
		}
		oSchSocket.interrupt();
		broadcastClientList();
		ret = getString("Disconnected2") + " " + sSocketIdentifier;
		log(ret);
		return ret;
	}
			
	private void handleRemoteServerCommand(String sData, String sSocketIdentifier) {
		// data format: /l password
		String sServerCommand = "";
		String sParameters = "";
		String sServerResponse = "";
		char[] chMessage;
		
		if (m_bAllowRemoteAdmin && sData.length() > 1 && sData.substring(0, 1).equals("/")) {
			sServerCommand = sData.substring(1);
			chMessage = sServerCommand.toCharArray();
			if (chMessage[0] == 'l' || chMessage[0] == 'x' || m_oVcLoggedIn.contains(sSocketIdentifier)) {
				if (sData.length() > 3) {
					sParameters = sData.substring(3);
				}
				switch (chMessage[0]) {
				case 'c':
					sServerResponse = getString("ClientList") + "\n" + getClientList(sSocketIdentifier, false);
					break;
				case 'k':
					if (sParameters.equals("") || chMessage[1] != ' ') {
						sServerResponse = getString("EnterPlayerName");
					} else {
						sServerResponse = kickPlayer(sParameters);
					}
					break;
				case 'h':
					sServerResponse = "================\n" + MessageFormat.format(getString("RemoteHelpMsg"),
										new Object[]{"\n"}) + "\n================";
					break;
				case 'l':
					if (m_oVcLoggedIn.contains(sSocketIdentifier)) {
						sServerResponse = getString("AlreadyLoggedIn");
					} else {
						if (sParameters.equals(m_sRconPassword)) {
							m_oVcLoggedIn.add(sSocketIdentifier);
							sServerResponse = getString("LoggedIn");
						} else {
							sServerResponse = getString("WrongPassword");
						}
					}
					break;
				case 'x':
					if (m_oVcLoggedIn.contains(sSocketIdentifier)) {
						m_oVcLoggedIn.remove(sSocketIdentifier);
						sServerResponse = getString("LoggedOut");
					} else {
						sServerResponse = getString("NotLoggedIn");
					}
					break;
				default:
					sServerResponse = getString("UnknownCommand");
				}
				sendMessage("serverresponse", sServerResponse, sSocketIdentifier);
				//System.out.println("Server Response: " + sServerResponse);
			}
		}
	}
	
	private String getClientList(String sSocketIdentifier, boolean forClient) {
		String sRet = "";
		String sSocketIdentifierList;
		String playerName;
		BattleShipsConnection oSchSocket;
		if (m_htPlayerName.isEmpty()) {
			if (!forClient)
				sRet = getString("NoClientsConnected");
		} else {
			StringBuffer oStr = new StringBuffer();
			Enumeration oEnum = m_htPlayerName.keys();
			while (oEnum.hasMoreElements()) {
				playerName = (String)oEnum.nextElement();
				sSocketIdentifierList = m_htPlayerName.get(playerName);
				if (forClient) {
					if (!m_oHtClientMapping.containsKey(sSocketIdentifierList) && !sSocketIdentifierList.equals(sSocketIdentifier)) {
						oStr.append(playerName);
						oStr.append("\n");
					}
				} else {
					oStr.append(sSocketIdentifierList);
					oStr.append(" (");
					oStr.append(playerName);
					oStr.append(")");
					if (sSocketIdentifierList.equals(sSocketIdentifier)) oStr.append(" (" + getString("You") + ")");
					oStr.append("\n");
				}
			}
			sRet = oStr.toString();
		}
		return sRet;
	}

	private void broadcastClientList() {
		String socketIdentifier;
		Enumeration oEnum = m_oHtBattleShipsConnection.keys();
		while (oEnum.hasMoreElements()) {
			socketIdentifier = (String)oEnum.nextElement();
			if (!m_oHtClientMapping.containsKey(socketIdentifier))
				sendMessage("clientlistupdate", getClientList(socketIdentifier, true), socketIdentifier);
		}
	}
	
	private void sendMessage(String sMessage, String sData, String sSocketIdentifier) {
		if (DEBUG) System.out.println("sendMessage: " + sMessage + " - " + sData + " ID: " + sSocketIdentifier);
		BattleShipsConnection oSchSocket = (BattleShipsConnection)m_oHtBattleShipsConnection.get(sSocketIdentifier);
		if (oSchSocket != null) oSchSocket.sendMessage(sMessage, sData);
	}
	
	private void startWebServer(int iWebServerPort, boolean bWebServerLog) {
		String sError = "";
		String sStatus = getString("Error") + " Webserver: ";
		try {
		    m_oWebServer = new WebServer(iWebServerPort, "BattleShips", BattleShipsUtility.VERSION, bWebServerLog);
			log(MessageFormat.format(getString("WebServerStarting"),
										new Object[]{String.valueOf(iWebServerPort)}));
		} catch (IOException e) {
			sError = e.getMessage();
			if (sError.indexOf("Address already in use") > -1) {
				sStatus += MessageFormat.format(getString("ServerPortInUse"),
										new Object[]{String.valueOf(iWebServerPort)});
			} else {
				sStatus += sError;
			}
			log(sStatus);
		}
	}
	
	private void log(String sMsg) {
		m_bwLog.println(BattleShipsUtility.getTimeStamp() + " - " + sMsg);
		//sendLogToAdmins(sTimeStamp + " - " + sMsg);
	}
	
	private void sendLogToAdmins(String sMsg) {
		ListIterator<String> list;
		String sSocketIdentifier;
		if (m_oVcLoggedIn != null && !m_oVcLoggedIn.isEmpty()) {
			list = m_oVcLoggedIn.listIterator();
			while (list.hasNext()) {
				sSocketIdentifier = list.next();
				sendMessage("serverresponse", sMsg, sSocketIdentifier);
				System.out.println("Sending msg to " + sSocketIdentifier);
			}
		}
	}
	
	public String getString(String sKey) {
		return m_oUtil.getParameter("String_" + sKey);
		//return "String_" + sKey;
	}

}