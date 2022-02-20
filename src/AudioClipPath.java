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
import java.net.*;

public class AudioClipPath {
	
	private URL m_url;
	private URL m_fallbackUrl;
	
	public void setUrl(URL url) {
		m_url = url;
	}
	
	public void setFallbackUrl(URL url) {
		m_fallbackUrl = url;
	}
	
	public URL getUrl() {
		return m_url;
	}

	public URL getFallbackUrl() {
		return m_fallbackUrl;
	}
	
}