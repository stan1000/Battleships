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
import java.net.*;

public interface BattleShipsParentContainer {
	
	public String getParameter(String sName, String sDefaultValue);

	public String getParameter(String sName);

	public String getHost();

	public URL getAudioClipUrl(String sFileName);

	public Image getImage(String sFileName);
	
	public void setNormalState();
	
	public String getCookie(String cookieName);
							
	public String getCookie(String cookieName, String defaultValue);
							
	public void setCookie(String cookieName, String value, int expDays);
	
	public void setWindowTitle(String title);

}