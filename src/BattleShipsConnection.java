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

public class BattleShipsConnection extends Thread {
	
	private Socket m_oSckClient;
	private ServerSocket m_oSckServer;
	private InputStream m_oInputStream;
	private OutputStream m_oOutputStream;
	private boolean m_bIsClient;
	private boolean m_bRunning;
	private String m_sHost;
	private int m_iPort;
	private BattleShipsConnectionListener m_oStlOwner;
	private String m_sObjectName;
	private String m_sMessageQualifier;
	private int m_iPingCounter;
	private int m_iIsAliveCounter;
	private String m_sLastMessage;
	private String m_sLastData;
	private boolean m_bSentData;
	private int m_iConfirmationCounter;
	private int m_iResendCounter;
	private String m_sSocketIdentifier;
	private String m_playerName;

	private final boolean DEBUG = false;
	private final int CHECK_INTERVAL = 300;
	private final int CONFIRMATION_INTERVAL = 300;
	private final int MAX_RESEND_COUNT = 3;
	
	public BattleShipsConnection(BattleShipsConnectionListener oStlOwner) {
		this(oStlOwner, null, "");
	}

	public BattleShipsConnection(BattleShipsConnectionListener oStlOwner, Socket oSckClient, String sSocketIdentifier) {
		m_oStlOwner = oStlOwner;
		m_oSckClient = oSckClient;
		m_sSocketIdentifier = sSocketIdentifier;
		m_sMessageQualifier = "msg";
		setPriority(Thread.MAX_PRIORITY);
		m_playerName = getRandomPlayerName();
	}
	
	public void start(int iPort) {
		m_bIsClient = false;
		m_iPort = iPort;
		start();
	}

	public void start(String sHost, int iPort) {
		m_bIsClient = true;
		m_sHost = sHost;
		m_iPort = iPort;
		start();
	}
	
	public void start() {
		if (!m_bRunning) {
			if (DEBUG) System.out.println("Socket thread started.");
			m_bRunning = true;
			super.start();
		}
	}
	
	public void interrupt() {
		if (m_bRunning) {
			if (DEBUG) System.out.println("Socket thread closed down.");
			super.interrupt();
			m_bRunning = false;
			closeSockets();
		}
	}
	
	public void run() {
		int i;
		String sRawData = "";
		String sMessage = "";
		String sData = "";
		Integer iTmp = new Integer(0);
		int iNumBytes = 0;
		byte[] RawData;
		m_iPingCounter = 0;
		m_iIsAliveCounter = 0;
		m_iConfirmationCounter = 0;
		m_iResendCounter = 0;
		
		if (m_oSckClient == null) {
			if (m_bIsClient) {
				try {
					m_oSckClient = new Socket(InetAddress.getByName(m_sHost), m_iPort);
				} catch (UnknownHostException e) {
					setError("UnknownHost", e.getMessage());
					if (DEBUG) System.out.println(e.getMessage());
				} catch (Exception e) {
					setError("General", e.getMessage());
					if (DEBUG) System.out.println(e.getMessage());
				}
			} else {
				try {
					m_oSckServer = new ServerSocket(m_iPort);
					m_oSckClient = m_oSckServer.accept();
				} catch (IOException e) {
					setError("ServerStart", e.getMessage());
					if (DEBUG) System.out.println(e.getMessage());
				}
			}
		}
		if (m_bRunning) {
			try {
				m_oInputStream = m_oSckClient.getInputStream();
				m_oOutputStream = m_oSckClient.getOutputStream();
				setMessage("status", "Connected");
			} catch (Exception e) {
				setError("General", e.getMessage());
				if (DEBUG) e.printStackTrace();
			}
		}
		while (m_bRunning) {
			iNumBytes = 0;
			while (m_bRunning && iNumBytes == 0) {
				try {
					iNumBytes = m_oInputStream.available();
					m_iIsAliveCounter++;
					m_iPingCounter++;
					if (m_iPingCounter == CHECK_INTERVAL) {
						//if (DEBUG) System.out.println("Sending ping");
						sendMessage("ping", "");
					}
					if (m_iIsAliveCounter > CHECK_INTERVAL * 2) {
						if (DEBUG) System.out.println("Disconnecting ...");
						setMessage("disconnected", "");
						m_iIsAliveCounter = 0;
					}
					if (m_bSentData) m_iConfirmationCounter++;
					if (m_iConfirmationCounter == CONFIRMATION_INTERVAL) {
						if (DEBUG) System.out.println("Resending data ...");
						sendMessage(m_sLastMessage, m_sLastData);
						m_iConfirmationCounter = 0;
						m_iResendCounter++;
					}
					if (m_iResendCounter == MAX_RESEND_COUNT) {
						if (DEBUG) System.out.println("Resent data " + m_iResendCounter + "x without getting confirmation ... disconnecting");
						setMessage("disconnected", "");
					}
					try {
						sleep(10);
					} catch (InterruptedException e) {
						break;
					}
				} catch (Exception e) {
					setError("General", e.getMessage());
					if (DEBUG) System.out.println(e.getMessage());
				}
			}
			if (m_bRunning) {
				RawData = new byte[iNumBytes];
				try {
					m_oInputStream.read(RawData);
				} catch (Exception e) {
					setError("General", e.getMessage());
					if (DEBUG) System.out.println(e.getMessage());
				}
				try {
					sRawData = new String(RawData, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					sRawData = new String(RawData);
				}
				if (DEBUG && sRawData.indexOf("<ping>") == -1) System.out.println("received: " + sRawData);
				if (m_sMessageQualifier.equals("")) {
					setMessage("", sRawData);
				} else {
					int iLen = m_sMessageQualifier.length() + 2;
					int iStartPos = 0;
					int iEndPos = 0;
					int iNumberAttempt = 0;
					String sRawMessage = "";
					while (iStartPos >= 0 && iNumberAttempt < 64) {
						iNumberAttempt++;
						iStartPos = sRawData.indexOf("<" + m_sMessageQualifier + ">", iStartPos);
						if (iStartPos > -1) {
							iEndPos = sRawData.indexOf("</" + m_sMessageQualifier + ">", iStartPos);
							if (iStartPos < iEndPos) {
								iStartPos += iLen;
								sRawMessage = sRawData.substring(iStartPos, iEndPos);
								sMessage = sRawMessage.substring(1, sRawMessage.indexOf(">", 0));
								if (!sMessage.equals("ping")) {
									int iPos = sRawMessage.indexOf(">");
									sData = sRawMessage.substring(iPos + 1, sRawMessage.indexOf("<", iPos));
									if (sMessage.equals("confirmation")) {
										if (sData.equals(m_sLastMessage)) {
											m_bSentData = false;
											m_iConfirmationCounter = 0;
											m_iResendCounter = 0;
										}
									} else {
										setMessage(sMessage, sData);
										if (!sMessage.equals("disconnected")) sendMessage("confirmation", sMessage);
									}
								}
							}
						}
					}
				}
				m_iIsAliveCounter = 0;
			}
		}
    }
	
	public void setMessageQualifier(String sMessageQualifier) {
		m_sMessageQualifier = sMessageQualifier;
	}
	
	public void sendMessage(String sMessage, String sData) {
		if (m_oOutputStream == null) return;
		byte[] btOut;
		m_iPingCounter = 0;
		if (!sMessage.equals("ping") && !sMessage.equals("confirmation") && !sMessage.equals("disconnected")) {
			m_bSentData = true;
			m_sLastMessage = sMessage;
			m_sLastData = sData;
		}
		if (sMessage.equals("") || m_sMessageQualifier.equals("")) {
			sMessage = sData;
		} else {
			sData = StringTool.replace(sData, "<", "&lt;");
			sData = StringTool.replace(sData, ">", "&gt;");
			sMessage = "<" + m_sMessageQualifier + "><" + sMessage + ">" + sData + "</" + sMessage + "></" + m_sMessageQualifier + ">";
		}
		try {
			btOut = sMessage.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			btOut = sMessage.getBytes();
		}
		try {
			m_oOutputStream.write(btOut);
			m_oOutputStream.flush();
		} catch (Exception e) {
			if (DEBUG) System.out.println(e.getMessage());
		}
	}
	
	public void send(String sData) {
		sendMessage("", sData);
	}
	
	public void setPlayerName(String playerName) {
		m_playerName = playerName;	
	}
	
	public String getPlayerName() {
		return m_playerName;
	}
	
	private synchronized void setMessage(String sMessage, String sData) {
		sData = StringTool.replace(sData, "&lt;", "<");
		sData = StringTool.replace(sData, "&gt;", ">");
		if (m_sSocketIdentifier.equals("")) {
			m_oStlOwner.socketDataArrived(sMessage, sData);
		} else {
			m_oStlOwner.socketDataArrived(sMessage, sData, m_sSocketIdentifier);
		}
	}
	
	private void setError(String sType, String sData) {
		if (m_sSocketIdentifier.equals("")) {
			m_oStlOwner.socketError(sType, sData);
		} else {
			m_oStlOwner.socketError(sType, sData, m_sSocketIdentifier);
		}
	}
	
	private void closeSockets() {
		sendMessage("disconnected", "reset");
		if (m_oSckClient != null) {
			try {
				if (m_oInputStream != null) m_oInputStream.close();
			} catch (Exception e) {
				if (DEBUG) System.out.println("Error closing input stream: " + e.getMessage());
			}
			try {
				if (m_oOutputStream != null) {
					m_oOutputStream.flush();
					m_oOutputStream.close();
				}
			} catch (Exception e) {
				if (DEBUG) System.out.println("Error closing output stream: " + e.getMessage());
			}
			try {
				m_oSckClient.close();
			} catch (Exception e) {
				if (DEBUG) System.out.println("Error closing client socket: " + e.getMessage());
			}
			if (DEBUG) System.out.println("Socket: closing client socket");
		}
		if (!m_bIsClient && m_oSckServer != null) {
			try {
				m_oSckServer.close();
			} catch (Exception e) {
				if (DEBUG) System.out.println("Error closing server socket: " + e.getMessage());
			}
			if (DEBUG) System.out.println("Socket: closing server socket");
		}
	}
	
	private String getRandomPlayerName() {
		return "Anon" + Math.round(Math.random() * 100000000);
	}

}
