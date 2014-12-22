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
import java.awt.image.*;
import java.lang.reflect.*;

public class GfxAnimatedButton extends Container {

	private Object m_oOwner;
	private String m_sName;
	private boolean m_bIsRollover;
	private Image m_oImgOn;
	private Image m_oImgOff;
	private Image m_oImgImage;
	private TextDisplayPanel m_oPnlText;
	private boolean m_bTextPositioned;
	private boolean m_bPressed;
	private int m_iPressedOffsetX;
	private int m_iPressedOffsetY;
	private boolean m_bEnabled;
	private static int MIN_WIDTH = 50;
	private FontMetrics m_fntMetrics;

	public GfxAnimatedButton(Object oOwner, String sName, Image oImgOn, Image oImgOff) {
		m_oOwner = oOwner;
		m_sName = sName;
		m_bIsRollover = (oImgOff == null) ? false : true;
		m_oImgOn = oImgOn;
		m_oImgOn.getWidth((ImageObserver)this);
		m_oImgOff = oImgOff;
		m_oImgOff.getWidth((ImageObserver)this);
		m_oImgImage = m_oImgOff;
		m_bTextPositioned = true;
		m_bPressed = false;
		m_bEnabled = true;
		m_iPressedOffsetX = 1;
		m_iPressedOffsetY = 1;
		setForeground(new Color(0, 34, 136));
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		addMouseListener(new TheMouseAdapter((Object)this, "b"));
	}

	public void paint(Graphics g) {
		paintContent(g);
		super.paint(g);
	}
	
	private void paintContent(Graphics g) {
		if (g == null) return;
		if (m_oImgImage != null) {
			g.drawImage(m_oImgImage, 0, 0, (ImageObserver)this);
			g.setColor(new Color(187, 187, 187));
			g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
		}
		if (!m_bTextPositioned) {
			if (m_oPnlText.getBounds().width > 1) {
				int iPWidth = m_oPnlText.getBounds().width;
				int iPHeight = m_oPnlText.getBounds().height;
				int iXPos = Math.round((getBounds().width - iPWidth) / 2);
				int iYPos = Math.round((getBounds().height - iPHeight) / 2);
				iXPos += m_bPressed ? m_iPressedOffsetX : 0;
				iYPos += m_bPressed ? m_iPressedOffsetY : 0;
				m_oPnlText.setLocation(iXPos, iYPos);
				m_bTextPositioned = true;
			}
		}
	}
	
	public void setLabel(String sText) {
		if (m_oPnlText == null) {
			m_oPnlText = (TextDisplayPanel)add(new TextDisplayPanel());
			m_oPnlText.setFont(getFont());
			m_oPnlText.setForeground(getForeground());
		}
		m_oPnlText.setText(sText, TextDisplayPanel.AUTO_RESIZE);
		m_bTextPositioned = false;
		if (m_fntMetrics != null)
			setSize(m_fntMetrics.stringWidth(getLabel()) + 15, m_fntMetrics.getHeight() + 8);
	}
	
	public String getLabel() {
		return m_oPnlText.getText();
	}
	
	public void setSize(int iWidth, int iHeight) {
		if (iWidth < MIN_WIDTH) iWidth = MIN_WIDTH;
		super.setSize(iWidth, iHeight);
		m_bTextPositioned = false;
		paintContent(getGraphics());
	}
	
	public void setFont(Font font) {
		m_fntMetrics = getFontMetrics(font);
		super.setFont(font);
	}
	
	public void setEnabled(boolean bEnabled) {
		m_bEnabled = bEnabled;
		if (bEnabled) {
			setCursor(new Cursor(Cursor.HAND_CURSOR));
			m_oPnlText.setForeground(getForeground());
		} else {
			m_oImgImage = m_oImgOff;
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			m_oPnlText.setForeground(new Color(160, 160, 160));
		}
		repaint();
	}
	
	//public void Button_MousePressed() {
	public void b_oi(MouseEvent event) {
		if (m_bEnabled && event.getModifiers() == 16) {
			if (m_bIsRollover) {
				m_oImgImage = m_oImgOn;
				if (m_oPnlText != null) {
					m_bTextPositioned = false;
					m_bPressed = true;
				}
				paintContent(getGraphics());
			}
		}
	}

	//public void Button_Exited() {
	public void b_od(MouseEvent event) {
		if (m_bPressed) click(event, false, true);
	}

	//public void Button_MouseReleased() {
	public void b_ok(MouseEvent event) {
		if (m_bPressed) click(event, false, false);
	}
	
	//public void Button_MouseClicked() {
	public void b_ou(MouseEvent event) {
		click(event, true, false);
	}

	private void click(MouseEvent event, boolean bFireEvent, boolean bExited) {
		if (m_bEnabled && (event.getModifiers() == 16 || bExited)) {
			if (m_bIsRollover) {
				m_oImgImage = m_oImgOff;
				if (m_oPnlText != null) {
					m_bTextPositioned = false;
					m_bPressed = false;
				}
				paintContent(getGraphics());
			}
			if (bFireEvent) {
				Class oClass = m_oOwner.getClass();
				try {
					Method oMeth = oClass.getMethod(m_sName + "_ou", new Class[]{event.getClass()});
					try {
						oMeth.invoke((Object)m_oOwner, new Object[]{event});
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.getTargetException().printStackTrace();
					}
				} catch (NoSuchMethodException e) {
					//System.out.println(m_sName + "_MouseReleased not defined public.");
					// according method not defined, do nothing
				}
			}
		}
	}

}
