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

public class StateDisplayPanel extends Container {

	private String m_sText;
	private boolean m_bResize;
	private TheTimerTask m_oTmrSwitch;
	private int m_iSwitchCount;
	
	private final int MAX_SWITCH_COUNT = 10;
	
	public StateDisplayPanel() {
		setLayout(null);
		m_sText = "";
		m_bResize = true;
		//**addMouseListener(new TheMouseAdapter((Object)this, "StateDisplayPanel"));
		addMouseListener(new TheMouseAdapter((Object)this, "c"));
	}

	public void paint(Graphics g) {
		if (g == null) return;
		Dimension oReDim = getSize();
		g.setColor(getBackground());
		g.fillRect(0, 0, oReDim.width - 1, oReDim.height - 1);
		try {
			Graphics2DFactory.setAntialiasing(g);
		} catch (Throwable t) {}
		if (!m_sText.equals("")) {
			g.setColor(getForeground());
			g.setFont(getFont());
			FontMetrics oFntMetrics = g.getFontMetrics();
			int iY = oFntMetrics.getAscent();
			if (m_bResize) {
				int iWidth = oFntMetrics.stringWidth(m_sText) + Math.round(oFntMetrics.getMaxAdvance() / 2);
				int iHeight = oFntMetrics.getHeight();
				Dimension oDimParent = getParent().getSize();
				setBounds(Math.round((oDimParent.width - iWidth) / 2), Math.round((oDimParent.height - iHeight) / 3), iWidth, iHeight);
			}
			g.drawString(m_sText, Math.round(oFntMetrics.getMaxAdvance() / 4), iY);
			g.drawRect(0, 0, getBounds().width - 1, getBounds().height - 1);
			g.drawRect(1, 1, getBounds().width - 3, getBounds().height - 3);
		}
		super.paint(g);
	}
	
	public void setText(String sText) {
		m_sText = sText;
		m_bResize = true;
		repaint();
	}
	
	public void refreshSize() {
		m_bResize = true;
		repaint();
	}
	
	public void start() {
		m_iSwitchCount = 0;
		setVisible(true);
		refreshSize();
		//**m_oTmrSwitch = new TheTimerTask((Object)this, "Switch");
		m_oTmrSwitch = new TheTimerTask((Object)this, "k");
		m_oTmrSwitch.start(300, 300);
	}

	public void stop() {
		if (m_oTmrSwitch != null) m_oTmrSwitch.interrupt();
		setVisible(false);
	}
	
	//**public void Switch_TimerEvent() {
	public void k_b() {
		m_iSwitchCount++;
		if (m_iSwitchCount == MAX_SWITCH_COUNT) {
			m_oTmrSwitch.interrupt();
			setVisible(true);
		} else {
			setVisible(!isVisible());
		}
	}

	//**public void StateDisplayPanel_MouseEntered(MouseEvent event) {
	public void c_oh(MouseEvent event) {
		if (m_iSwitchCount == MAX_SWITCH_COUNT) {
			setVisible(false);
		}
	}

}