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
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class BattleShipsBot extends Frame implements BattleShipsParentContainer {

	private BattleShipsPanel m_oBtlShips;
	private BattleShipsUtility m_oUtil;
	private String m_serverName;
	private ClassLoader m_cl;
	
	// Gotta declare "static" due to bug in obfuscator. Ah, well ...
	private final static String DEFAULT_SERVER = "localhost";
	private final static String DEFAULT_PLAYER_NAME = "BattleshipsBot";
	private final static int DEFAULT_BOT_TIMEOUT_SECONDS = 600;

	public static void main(String[] args) {
		BattleShipsBot oApp = new BattleShipsBot(args);
	}
	
	public BattleShipsBot(String[] args) {
		String playerName;
		boolean isVisible;
		boolean autoBot;
		CliArgs cliArgs = new CliArgs(args);
		boolean debug;
		int timeOutSeconds;
		int port;
		
		isVisible = !cliArgs.switchPresent("-invisible");
		playerName = cliArgs.switchValue("-name", DEFAULT_PLAYER_NAME);
		m_serverName =  cliArgs.switchValue("-server", DEFAULT_SERVER);
		port = cliArgs.switchIntValue("-port", BattleShipsUtility.DEFAULT_PORT);
		autoBot = cliArgs.switchPresent("-autobot");
		debug = cliArgs.switchPresent("-debug");
		try {
			timeOutSeconds =  cliArgs.switchIntValue("-timeout", DEFAULT_BOT_TIMEOUT_SECONDS);
		} catch (NumberFormatException e) {
			timeOutSeconds = DEFAULT_BOT_TIMEOUT_SECONDS;
		}
		m_oUtil = new BattleShipsUtility();
		m_cl = this.getClass().getClassLoader();
		if (m_oUtil.readParameters()) {
			setLayout(null);
			setResizable(false);
			setTitle(playerName);
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
			m_oBtlShips = (BattleShipsPanel)add(new BattleShipsPanel(true, debug));
			if (isVisible) {
				setVisible(true);
				setSize(640, 480);
			}
			m_oBtlShips.setIsBot(true, autoBot, port);
			m_oBtlShips.init();
			m_oBtlShips.setPlayerName(playerName);
			m_oBtlShips.setTimeOutSeconds(timeOutSeconds);
		} else {
			System.err.println(m_oUtil.getErrorMessage());
			System.exit(1);
		}
	}

	public String getParameter(String sName, String sDefaultValue) {
		return m_oUtil.getParameter(sName, sDefaultValue);
	}

	public String getParameter(String sName) {
		return m_oUtil.getParameter(sName);
	}

	public String getHost() {
		return (m_serverName == null ? DEFAULT_SERVER : m_serverName);
	}
	
	public URL getAudioClipUrl(String sFileName) {
		return null;
	}
	
	public Image getImage(String sFileName) {
		Image oImg = null;
		URL oURL = getResource(sFileName);
		if (oURL != null) {
			oImg = Toolkit.getDefaultToolkit().getImage(oURL);
		}
		if (oImg == null) {
			System.err.println("Error: Missing file " + sFileName);
			System.exit(1);
		}
		return oImg;
	}
	
	private URL getResource(String fileName) {
		return m_cl.getResource(fileName);
	}
	
	public void setNormalState() {
		setState(Frame.NORMAL);
		toFront();
	}

	public String getCookie(String cookieName) {
		// keep for compabilility
		return "";
	}
							
	public String getCookie(String cookieName, String defaultValue) {
		// keep for compabilility
		return "";
	}

	public void setCookie(String cookieName, String value, int expDays) {
		// keep for compabilility
	}

	public void setWindowTitle(String title) {
		if (title.equals(""))
			title = DEFAULT_PLAYER_NAME;
		setTitle(title);
	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			m_oBtlShips.stop();
			System.exit(0);
		}
	}

}