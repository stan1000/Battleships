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

public class ServerSocketThread extends Thread {

    private ServerSocket m_oServerSocket;
	private BattleShipsServer m_oBsServer;
    private boolean m_bRunning;

	private final boolean DEBUG = false;

	public ServerSocketThread(BattleShipsServer oBsServer, int iPort) throws IOException {
		m_oBsServer = oBsServer;
		m_oServerSocket = new ServerSocket(iPort);
		start();
	}

	public void start() {
		if (!m_bRunning) {
			m_bRunning = true;
			super.start();
		}
	}
	
	public void interrupt() {
		if (m_bRunning) {
			super.interrupt();
			m_bRunning = false;
			try {
				m_oServerSocket.close();
			} catch (IOException e) {
				if (DEBUG) System.out.println(e.getMessage());
			}
		}
	}

	public void run() {
        while (m_bRunning) {
            try {
                Socket socket = m_oServerSocket.accept();
				m_oBsServer.clientConnected(socket);
            } catch (Exception e) {
				if (DEBUG) System.out.println(e.getMessage());
            }
        }
    }
	
}