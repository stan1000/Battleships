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

public class BattleShipsServerApplication extends Thread {
	
	private BattleShipsServer m_oServer;
	private String m_sOutput;
	private boolean m_bContinue;
	private PrintWriter m_bwLog;
	
	public static void main(String[] args) throws IOException {
		CliArgs cliArgs = new CliArgs(args);
		boolean runAsDaemon = cliArgs.switchPresent("-daemon");
		BattleShipsServerApplication oServerCon = new BattleShipsServerApplication(runAsDaemon);
	}

	public BattleShipsServerApplication(boolean runAsDaemon) {
		m_bContinue = true;
		String sMessage = "";
		char[] chMessage;
		BufferedReader oConRead = new BufferedReader(new InputStreamReader(System.in));
		try {
			openLogWriter();
			m_oServer = new BattleShipsServer(m_bwLog);
			printHelpMsg();
			m_oServer.start();
			while (m_bContinue) {
				if (runAsDaemon) {
					try {
						sleep(5000);
					} catch (InterruptedException e) {}
				} else {
					try {
						sMessage = oConRead.readLine();
					} catch (Exception e) {
						System.err.println(e.getMessage());
						System.exit(1);
					}
					if (sMessage != null) {
						if (sMessage.length() > 0) {
							chMessage = sMessage.toCharArray();
							switch (chMessage[0]) {
							case 'h':
								printHelpMsg();
								break;
							case 'c':
								m_oServer.printClientList();
								break;
							case 'k':
								if (chMessage.length < 3 || chMessage[1] != ' ') {
									m_bwLog.println(m_oServer.getString("EnterPlayerName"));
								} else {
									m_oServer.kickPlayer(sMessage.substring(2));
								}
								break;
							case 'x':
								m_oServer.interrupt();
								m_bContinue = false;
								break;
							default:
								m_bwLog.println(m_oServer.getString("UnknownCommand"));
							}
						}
					}
				}
			}
			m_bwLog.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	private void openLogWriter() {
		String osName = System.getProperty("os.name");
		String codePage = "";
		if (osName.startsWith("Windows")) codePage = "CP850";
		else codePage = "UTF-8";
		try {
			m_bwLog = new PrintWriter(new OutputStreamWriter(System.out, codePage), true);
		} catch(UnsupportedEncodingException e){
			m_bwLog = new PrintWriter(new OutputStreamWriter(System.out), true);
		}
	}
	
	private void printHelpMsg() {
		m_bwLog.println(m_oServer.getHelpMsg());
	}

}