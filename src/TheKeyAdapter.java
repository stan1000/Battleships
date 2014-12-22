/*
Battleships - The popular game as client/server edition for playing with a friend over the Internet (or on the LAN).
Copyright (C) 2006-2009 Stan's World
http://www.stans-world.de/

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
import java.awt.event.*;
import java.lang.reflect.*;

public class TheKeyAdapter extends KeyAdapter {
		
	private Object m_oOwner;
	private String m_sCompName;
		
	public TheKeyAdapter(Object oOwner, String sCompName) {
		m_oOwner = oOwner;
		m_sCompName = sCompName;
	}

	public void keyPressed(KeyEvent event) {
		//**handleKeyEvent("Pressed", event);
		handleKeyEvent("e", event);
	}

	private void handleKeyEvent(String sEventType, KeyEvent event) {
		Class oClass = m_oOwner.getClass();
		try {
			//**Method oMeth = oClass.getMethod(m_sCompName + "_Key" + sEventType, new Class[]{event.getClass()});
			Method oMeth = oClass.getMethod(m_sCompName + "_v" + sEventType, new Class[]{event.getClass()});
			try {
				oMeth.invoke(m_oOwner, new Object[]{event});
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.getTargetException().printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			//System.out.println(m_sCompName + "_v" + sEventType + " not defined public.");
			// according method not defined, do nothing
		}
	}

}
