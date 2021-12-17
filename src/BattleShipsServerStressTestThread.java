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
import java.util.*;

public class BattleShipsServerStressTestThread extends Thread implements BattleShipsConnectionListener {

    private boolean m_bRunning;
	private boolean m_bSendChat;
	private String m_sServer;
	private int m_iPort;
	private BattleShipsConnection m_oConn;
	
	private final int NUMBER_OF_MESSAGES =  10;

	public BattleShipsServerStressTestThread(String sServer, int iPort) {
		m_bSendChat = false;
		m_sServer = sServer;
		m_iPort = iPort;
		m_oConn = new BattleShipsConnection((BattleShipsConnectionListener)this);
		m_oConn.setMessageQualifier("msg");
		m_oConn.start(m_sServer, m_iPort);
	}

	public void start() {
		if (!m_bRunning) {
			m_bRunning = true;
			super.start();
		}
	}
	
	public void interrupt() {
		BattleShipsConnection oSchSocket = null;
		if (m_bRunning) {
			super.interrupt();
			m_oConn.interrupt();
		}
	}

	public void run() {
        while (m_bRunning) {
			int i;
			for (i = 0; i <= NUMBER_OF_MESSAGES; i++) {
				if (m_bSendChat) m_oConn.sendMessage("chat", "A nice and not too long message (" + i + ") from our stress tester " + this.toString());
				try {
					sleep(100);
				} catch (InterruptedException e) {}
			}
			m_oConn.sendMessage("disconnectenemy", "");
			m_bSendChat = false;
			try {
				sleep(1000);
			} catch (InterruptedException e) {}
        }
    }
	
	public synchronized void socketDataArrived(String sMessage, String sData) {
		if (sMessage.equals("disconnected")) {
			m_oConn.interrupt();
		} else if (sMessage.equals("clientlistupdate")) {
			StringTokenizer stk = new StringTokenizer(sData, "\n");
			if (stk.hasMoreTokens()) {
				m_oConn.sendMessage("requestgame", stk.nextToken());
			}
			try {
				sleep(500);
			} catch (InterruptedException e) {}
		} else if (sMessage.equals("enemyfound")) {
			m_bSendChat = true;
		} else if (sMessage.equals("status")) {
			if (sData.equals("EnemyFound") || sData.equals("EnemyChanged")) {
				m_bSendChat = true;
			} else if (sData.equals("EnemyLeft")) {
				m_bSendChat = false;
			}
		}
		System.out.println("TestThread " + this.toString() + ": " + sMessage + " - " + sData);
	}

	public synchronized void socketDataArrived(String sMessage, String sData, String sSocketIdentifier) {};

	public void socketError(String sType, String sData) {};

	public void socketError(String sType, String sData, String sSocketIdentifier) {};
	
}