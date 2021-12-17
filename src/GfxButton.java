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
import java.awt.image.*;

public class GfxButton extends Container {

	private Image m_oImgImage;
	private int m_iResizeFlag;
	
	public final static int MANUAL_RESIZE = 0;
	public final static int AUTO_RESIZE = 1;

	public GfxButton(Object oOwner, String sCompName, Image oImg) {
		m_oImgImage = oImg;
		m_oImgImage.getWidth((ImageObserver)this);
		m_iResizeFlag = AUTO_RESIZE;
		addMouseListener(new TheMouseAdapter(oOwner, sCompName));
		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	public void paint(Graphics g) {
		if (m_oImgImage != null) {
			if (m_iResizeFlag == AUTO_RESIZE) {
				int iImageWidth = m_oImgImage.getWidth((ImageObserver)this);
				if (iImageWidth > -1) {
					setSize(iImageWidth, m_oImgImage.getHeight((ImageObserver)this));
					m_iResizeFlag = MANUAL_RESIZE;
				}
			}
			g.drawImage(m_oImgImage, 0, 0, (ImageObserver)this);
		}
		super.paint(g);
	}
	
	public void setBounds(int x, int y) {
		setBounds(x, y, 1, 1);
	}
	
	protected void setImage(Image oImg) {
		m_oImgImage = oImg;
	}

}