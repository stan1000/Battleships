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
import java.util.*;

public class BattleShipsUtility {
	
	public final static String VERSION = "2.0";
	public final static int MAX_SHIP_TYPE = 6;
	public final static int MODE_CLIENT = 1;
	public final static int MODE_SERVER = 2;
	public final static int MODE_COMPUTER = 3;
	
	private final static String CONFIG_FILE = "config.htm";
	private final static String SERVER_CONFIG_FILE = "server.cfg";
	private String m_sErrorMessage;
	private Hashtable<String, String> m_oHtParameters;

	public BattleShipsUtility() {
		m_oHtParameters = new Hashtable<String, String>();
	}
		
	public boolean readParameters() {
		boolean bRet;
		bRet = readParameters(CONFIG_FILE);
		if (bRet) {
			bRet = readParameters(SERVER_CONFIG_FILE);
		}
		return bRet;
	}
		
	private boolean readParameters(String sConfigFile) {
		boolean bRet = true;
		try {
			String sLine = "";
			BufferedReader oBrHTMLFile = new BufferedReader(new UnicodeReader(new FileInputStream(sConfigFile), null));
			int i = -1;
			String sName = "";
			String sValue = "";
			while (sLine != null) {
				sLine = oBrHTMLFile.readLine();
				if (sLine != null) {
					i = sLine.toLowerCase().indexOf("<param");
					if (i > -1) {
						i = sLine.toLowerCase().indexOf("name=\"", i + 6);
						sName = sLine.substring(i + 6, sLine.indexOf("\"", i + 6));
						i = sLine.toLowerCase().indexOf("value=\"", i + 6);
						sValue = sLine.substring(i + 7, sLine.indexOf("\"", i + 7));
						m_oHtParameters.put(sName, sValue);
					}
				}
			}
			oBrHTMLFile.close();
		} catch (FileNotFoundException e) {
			m_sErrorMessage = "Error: File '" + sConfigFile + "' not found. It must be in the same folder like the application files.";
			bRet = false;
		} catch (Exception e) {
			m_sErrorMessage = "Error: " + e.toString();
			e.printStackTrace();
			bRet = false;
		}
		return bRet;
	}

	public String getParameter(String sName, String sDefaultValue) {
		String sTmp = getParameter(sName);
		if (sTmp == null) sTmp = sDefaultValue;
		return sTmp;
	}
	
	public String getParameter(String sName) {
		return m_oHtParameters.get(sName);
	}

	public int parseIntParm(String sParm, int iDefault) {
		sParm = getParameter(sParm);
		int iTmp = iDefault;
		if (sParm != null) {
			try {
				iTmp = Integer.parseInt(sParm);
			} catch (NumberFormatException e) {}
		}
		return iTmp;
	}
	
	public String getErrorMessage() {
		return m_sErrorMessage;	
	}
	
	public static String getZeroed(int iValue) {
		String sTmp = "00" + iValue;
		sTmp = sTmp.substring(sTmp.length() - 2, sTmp.length());
		return sTmp;
	}
	
	public static String getCopyright() {
		return "Schiffe Versenken (Battleships) V" + VERSION + "\n\u00a9 2005-2022 Stan Holoubek\nhttp://www.stans-world.de/battleships.html";
	}
	
	public static String getTimeStamp() {
		String sTimeStamp;
		Calendar cal = Calendar.getInstance();
		
		sTimeStamp = getZeroed(cal.get(Calendar.HOUR_OF_DAY)) + ":" + getZeroed(cal.get(Calendar.MINUTE)) + ":" +
						   getZeroed(cal.get(Calendar.SECOND));
		return sTimeStamp;
	}
	
}