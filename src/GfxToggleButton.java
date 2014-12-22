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
import java.awt.image.*;

public class GfxToggleButton extends GfxButton {

	private Image m_oImgOn;
	private Image m_oImgOff;
	private boolean m_bIsOn;
	
	public GfxToggleButton(Object oOwner, String sCompName, Image oImgOn, Image oImgOff) {
		super(oOwner, sCompName, oImgOff);
		m_oImgOn = oImgOn;
		m_oImgOff = oImgOff;
		m_oImgOn.getWidth((ImageObserver)this);
		m_oImgOff.getWidth((ImageObserver)this);
	}

	public void setOn(boolean bIsOn) {
		m_bIsOn = bIsOn;
		if (bIsOn) {
			setImage(m_oImgOn);
		} else {
			setImage(m_oImgOff);
		}
		repaint();
	}

}