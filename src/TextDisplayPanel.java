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
import java.util.*;

public class TextDisplayPanel extends Container {

	private String m_sText;
	private String m_sSplitText;
	private int m_iResizeFlagText;
	private int m_iPadding;
	private Color m_oColBackground;
	private boolean m_bWithGraphics2D;
	private boolean m_bWithBorder;
	
	public final static int MANUAL_RESIZE = 0;
	public final static int AUTO_RESIZE = 1;

	public TextDisplayPanel() {
		super();
		setLayout(null);
		m_sText = "";
		m_sSplitText = " ";
		m_iPadding = 1;
		m_bWithGraphics2D = true;
		m_bWithBorder = false;
	}

	public void paint(Graphics g) {
		paintContent(g);
		super.paint(g);
	}
	
	public void setBackground(Color oColBackground) {
		m_oColBackground = oColBackground;
	}
								 
	public void setBounds(int x, int y) {
		setBounds(x, y, 1, 1);
	}

	public String getSplitText() {
		return m_sSplitText;
	}

	public void setSplitText(String sSplitText) {
		m_sSplitText = sSplitText;
	}

	public void setPadding(int iPadding) {
		m_iPadding = iPadding;
	}

	public String getText() {
		return m_sText;
	}

	public void setText(String sText, int iResizeFlag) {
		m_sText = sText;
		m_iResizeFlagText = iResizeFlag;
		paintContent(getGraphics());
	}

	public void setText(String sText) {
		setText(sText, MANUAL_RESIZE);
	}
	
	public void append(String sText) {
		m_sText += sText;
		paintContent(getGraphics());
	}
	
	public void setWithBorder(boolean bWithBorder) {
		m_bWithBorder = bWithBorder;
	}
	
	public void setWithGraphics2D(boolean bWithGraphics2D) {
		m_bWithGraphics2D = bWithGraphics2D;
	}
	
	private void paintContent(Graphics g) {
		if (g == null) return;
		if (m_oColBackground != null) {
			g.setColor(m_oColBackground);
			g.fillRect(0, 0, getSize().width, getSize().height);
		}
		g.setColor(getForeground());
		if (m_bWithBorder) g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
		if (m_bWithGraphics2D) {
			try {
				Graphics2DFactory.setAntialiasing(g);
			} catch (Throwable t) {
				m_bWithGraphics2D = false;
			}
		}
		if (!m_sText.equals("")) {
			g.setColor(getForeground());
			g.setFont(getFont());
			FontMetrics oFntMetrics = g.getFontMetrics();
			int iYOffset = (int)Math.round(oFntMetrics.getHeight() / 1.5) + 1;
			int iPanelWidth = getSize().width;
			if (m_iResizeFlagText == AUTO_RESIZE) {
				m_iResizeFlagText = MANUAL_RESIZE;
				setSize(oFntMetrics.stringWidth(m_sText) + 4, oFntMetrics.getHeight() + 1);
				g.drawString(m_sText, 1 + m_iPadding, iYOffset + m_iPadding);
			} else if (oFntMetrics.stringWidth(m_sText) > iPanelWidth - m_iPadding * 2 || m_sText.indexOf("\n") > -1) {
				if (iPanelWidth == 0) return;
				String sLine;
				String sWord;
				int iYTextPos = iYOffset;
				StringBuffer oSbLine = new StringBuffer();
				StringTokenizer oStrTokWord;
				StringTokenizer oStrTokLine = new StringTokenizer(m_sText, "\n");
				while (oStrTokLine.hasMoreTokens()) {
					sLine = oStrTokLine.nextToken();
					oStrTokWord = new StringTokenizer(sLine, m_sSplitText);
					while (oStrTokWord.hasMoreTokens()) {
						sWord = oStrTokWord.nextToken();
						if (oFntMetrics.stringWidth(oSbLine.toString()) + oFntMetrics.stringWidth(m_sSplitText) + 
							oFntMetrics.stringWidth(sWord) > iPanelWidth - m_iPadding * 2) {
							g.drawString(oSbLine.toString(), 1 + m_iPadding, iYTextPos + m_iPadding);
							oSbLine.setLength(0);
							iYTextPos += iYOffset + 3;
						} else {
							if (oSbLine.length() > 0) oSbLine.append(m_sSplitText);
						}
						oSbLine.append(sWord);
					}
					g.drawString(oSbLine.toString(), 1 + m_iPadding, iYTextPos + m_iPadding);
					oSbLine.setLength(0);
					iYTextPos += iYOffset + 3;
				}
			} else {
				g.drawString(m_sText, 1 + m_iPadding, iYOffset + m_iPadding);
			}
		}
	}

}