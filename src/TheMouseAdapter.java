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
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

public class TheMouseAdapter extends MouseAdapter {
		
	private Object m_oOwner;
	private String m_sCompName;
	private MouseEvent m_oMePressed;
		
	public TheMouseAdapter(Object oOwner, String sCompName) {
		m_oOwner = oOwner;
		m_sCompName = sCompName;
	}

	public void mouseEntered(MouseEvent event) {
		//**handleMouseEvent("Entered", event);
		handleMouseEvent("h", event);
	}

	public void mouseExited(MouseEvent event) {
		//**handleMouseEvent("Exited", event);
		handleMouseEvent("d", event);
	}

	public void mousePressed(MouseEvent event) {
		m_oMePressed = event;
		//**handleMouseEvent("Pressed", event);
		handleMouseEvent("i", event);
	}

	public void mouseReleased(MouseEvent event) {
		// implemented own clicked event because of picky MS VM
		Point oPointOrg;
		Point oPoint = event.getPoint();
		if (m_oMePressed == null) {
			oPointOrg = oPoint;
		} else {
			oPointOrg = m_oMePressed.getPoint();
		}
		Dimension oDim = event.getComponent().getSize();
		if (oPoint.x >= 0 && oPoint.y >= 0 && oPoint.x < oDim.width && oPoint.y < oDim.height &&
			Math.abs(oPointOrg.x - oPoint.x) < 5 && Math.abs(oPointOrg.y - oPoint.y) < 5) {
			//**handleMouseEvent("Clicked", event);
			handleMouseEvent("u", m_oMePressed);
		} else {
			//**handleMouseEvent("Released", event);
			handleMouseEvent("k", event);
		}
	}
	
	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() == 2){
			handleMouseEvent("db", event);
		}
	}

	private void handleMouseEvent(String sEventType, MouseEvent event) {
		Class oClass = m_oOwner.getClass();
		try {
			//**Method oMeth = oClass.getMethod(m_sCompName + "_Mouse" + sEventType, new Class[]{event.getClass()});
			Method oMeth = oClass.getMethod(m_sCompName + "_o" + sEventType, new Class[]{event.getClass()});
			try {
				oMeth.invoke(m_oOwner, new Object[]{event});
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.getTargetException().printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			//System.out.println(m_sCompName + "_Mouse" + sEventType + " not defined public.");
			// according method not defined, do nothing
		}
	}

}
