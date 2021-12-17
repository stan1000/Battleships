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
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class BattleShipsApplet extends Applet implements BattleShipsParentContainer {
	
	private BattleShipsPanel m_oBtlShips;
	private ClassLoader m_cl;

	public void init() {
		m_cl = this.getClass().getClassLoader();
		setLayout(null);
		setBackground(Color.white);
		m_oBtlShips = (BattleShipsPanel)add(new BattleShipsPanel(true));
		m_oBtlShips.init();
	}

	public void stop() {
		m_oBtlShips.stop();
	}

	public String getParameter(String sName, String sDefaultValue) {
		String sTmp = getParameter(sName);
		if (sTmp == null) {
			sTmp = sDefaultValue;
		}
		return sTmp;
	}

	public String getHost() {
		return getDocumentBase().getHost();
	}
	
	public AudioClip getAudioClip(String sFileName) {
		AudioClip oAcClip = null;
		URL oURL = getResource(sFileName);
		if (oURL == null) {
			oURL = getResourceInDocBase(sFileName);
		}
		if (oURL != null) {
			oAcClip = getAudioClip(oURL);
		}
		return oAcClip;
	}

	public Image getImage(String sFileName) {
		Image oImage = null;
	    URL oURL = getResource(sFileName);
		if (oURL == null) {
			oURL = getResourceInDocBase(sFileName);
		}
		if (oURL != null) {
			oImage = getImage(oURL);
		}
		return oImage;
	}

	private URL getResource(String fileName) {
		return m_cl.getResource(fileName);
	}
	
	private URL getResourceInDocBase(String fileName) {
		URL oURL = null;
		try {
		    oURL = new URL(getDocumentBase(), "./" + fileName);
		} catch (MalformedURLException e) {
		    System.err.println(e.getMessage());
		}
		return oURL;
	}
	
	public void setNormalState() {}
	
	public void getCookie(String cookieName) {
		try {
			getAppletContext().showDocument(
					new URL("javascript:getCookie('" + cookieName + "')")
			);
		}
		catch (MalformedURLException me) { }
	}
							
	public void setCookie(String cookieName, String value, int expDays) {
		try {
			getAppletContext().showDocument(
					new URL("javascript:setCookie('" + cookieName + "', '" + value + "', " + expDays + ")")
			);
		}
		catch (MalformedURLException me) { }
	}

	// interface for JavaScript
	public boolean isConfigReady() {
		return m_oBtlShips.isConfigReady();
	}
	
	public String getSize(String sDimension) {
		String sTmp = "";
		if (sDimension.equals("width")) {
			sTmp = m_oBtlShips.getSize().width + "";
		} else if (sDimension.equals("height")) {
			sTmp = m_oBtlShips.getSize().height + "";
		}
		return sTmp;
	}
	
	public void setAppCookie(String cookieName, String value) {
		if (cookieName.equals("PlayerName")) {
			m_oBtlShips.setPlayerName(value);
		}
	}
}