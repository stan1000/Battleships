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

public class BattleShipsApplication extends Frame implements BattleShipsParentContainer {

	private BattleShipsPanel m_oBtlShips;
	private BattleShipsUtility m_oUtil;
	private Properties m_propCookies;
	private String m_serverName;
	private ClassLoader m_cl;
	
	// Gotta declare "static" due to bug in obfuscator. Ah, well ...
	private final static String DEFAULT_SERVER = "battleships.dynv6.net";
	private final static String COOKIE_FILE = "cookies.txt";

	public static void main(String[] args) {
		BattleShipsApplication oApp = new BattleShipsApplication(args);
	}
	
	public BattleShipsApplication(String[] args) {
		CliArgs cliArgs = new CliArgs(args);
		boolean debug = cliArgs.switchPresent("-debug");
		String action = cliArgs.switchValue("-action", "");
		String serverConfigFile = cliArgs.switchValue("-configfile", "");
		boolean startBot = action.equals("startbot");
		boolean startServer = action.equals("startserver");
		m_oUtil = new BattleShipsUtility();
		if (!serverConfigFile.equals("")) {
			m_oUtil.setServerConfigFile(serverConfigFile);
		}
		m_propCookies = new Properties();
		m_cl = this.getClass().getClassLoader();
		readCookieFile();
		m_serverName = getCookie("ServerName", DEFAULT_SERVER);
		if (m_oUtil.readParameters()) {
			setLayout(null);
			setResizable(false);
			setTitle(getParameter("String_Title"));
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
			m_oBtlShips = (BattleShipsPanel)add(new BattleShipsPanel(false, debug));
			setVisible(true);
			setSize(640, 480);
			setIcons();
			m_oBtlShips.setStartBot(startBot);
			m_oBtlShips.setStartServer(startServer);
			m_oBtlShips.init();
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
		return m_serverName;
	}
	
	public URL getAudioClipUrl(String sFileName) {
		URL url = null;
		File file = new File(sFileName);
		try {
			url = file.toURI().toURL();
		} catch (MalformedURLException e) {
			System.err.println("Error: Problem with sound file: " + file.toString());
		}
		return url;
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
	
	private void setIcons() {
		ArrayList<Image> iconList = new ArrayList<Image>();
		iconList.add(getImage("res/icon16x16.png"));
		iconList.add(getImage("res/icon32x32.png"));
		setIconImages(iconList);
	}
	
	private URL getResource(String fileName) {
		return m_cl.getResource(fileName);
	}
	
	public void setNormalState() {
		setState(Frame.NORMAL);
		toFront();
	}

	private void readCookieFile() {
		try {
			InputStream in = new FileInputStream(new File(COOKIE_FILE));
			m_propCookies.load(in);
		} catch (Exception e ) { }
	}
							   
	private void writeCookieFile() {
		try {         
			OutputStream out = new FileOutputStream(new File(COOKIE_FILE));
			m_propCookies.store(out, BattleShipsUtility.getCopyright());
		} catch (Exception e ) { }
	}

	public String getCookie(String cookieName) {
		return getCookie(cookieName, "");
	}
							
	public String getCookie(String cookieName, String defaultValue) {
		return m_propCookies.getProperty(cookieName, defaultValue);
	}
							
	public void setCookie(String cookieName, String value, int expDays) {
		m_propCookies.setProperty(cookieName, value);
		writeCookieFile();
	}

	public void setWindowTitle(String title) {
		String windowTitle = getParameter("String_Title");
		if (!title.equals("")) {
			windowTitle += " - " + title;
		}
		setTitle(windowTitle);
	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			m_oBtlShips.stop();
			System.exit(0);
		}
	}

}