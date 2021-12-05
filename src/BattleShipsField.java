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
import java.util.*;

public class BattleShipsField extends Container {

	private int m_iCellWidth;
	private int m_iFieldWidth;
	private ArrayList<Point> m_alShots;
	private ArrayList<BattleShip> m_alBattleShips;
	private boolean m_bEnemyField;
	private boolean m_bPlaying;
	private int m_iSunkCount;
	private int[] m_iShipTypeCount;
	private int m_iMaxShipType;
	private int m_iFieldType;
	private Color m_oColShip;
	private Color m_oColShipSunk;
	private Color m_oColShipHitMark;
	private Color m_oColShipHighlight;
	private Color m_oColShotMark;
	private Color m_oColLastShotMark;
	private Image m_oImgShipSegment;
	private Image m_oImgShipSegmentSunk;
	private boolean m_bPainting;
	private Cursor m_bShootCursor;
	private BattleShip[] m_bsShipPattern;
	private int[] m_activeShips;
	
	public static final int FIELD_TYPE_ME = 1;
	public static final int FIELD_TYPE_ENEMY = 2;
	public static final int FIELD_TYPE_SCORE = 3;
	private static final boolean DEBUG = true;

	public BattleShipsField(int iMaxShipType, int iFieldType) {
		m_iMaxShipType = iMaxShipType;
		m_iFieldType = iFieldType;
		if (m_iFieldType == FIELD_TYPE_ENEMY) {
			m_bEnemyField = true;
		} else {
			m_bEnemyField = false;
		}
		m_bPlaying = false;
		m_iSunkCount = 0;
		m_alShots = new ArrayList<Point>();
		m_alBattleShips = new ArrayList<BattleShip>();
		m_iShipTypeCount = new int[m_iMaxShipType];
		m_activeShips = new int[m_iMaxShipType];
		m_bPainting = false;
		//**addMouseListener(new TheMouseAdapter((Object)this, "BattleShipsField"));
		addMouseListener(new TheMouseAdapter((Object)this, "i"));
	}
	
	public void init(int iCellWidth, int iFieldWidth) {
		m_iCellWidth = iCellWidth;
		m_iFieldWidth = iFieldWidth;
		int iFieldDim = m_iCellWidth * m_iFieldWidth + 1;
		setSize(iFieldDim, iFieldDim);
	}
	
	private void paintShot(Graphics g, Point oPoint, Color oColor) {
		if (g == null) return;
		g.setColor(oColor);
		g.fillRect(oPoint.x * m_iCellWidth + 1, oPoint.y * m_iCellWidth + 1, m_iCellWidth - 1, m_iCellWidth - 1);
		if (DEBUG) {
			if (oPoint.x == 0) {
				g.setColor(getForeground());
				g.drawString(String.valueOf(oPoint.y), 2, oPoint.y * m_iCellWidth + Math.round(m_iCellWidth / 2) + 5);
			}
			if (oPoint.y == 0 && oPoint.x > 0) {
				g.setColor(getForeground());
				g.drawString(String.valueOf(oPoint.x), oPoint.x * m_iCellWidth + Math.round(m_iCellWidth / 2) - 6, m_iCellWidth - 3);
			}
		}
	}

	public void setColorShip(Color oColor) {
		m_oColShip = oColor;
	}
	
	public void setColorShipSunk(Color oColor) {
		m_oColShipSunk = oColor;
	}
	
	public void setColorShipHitMark(Color oColor) {
		m_oColShipHitMark = oColor;
	}
	
	public void setColorShipHighlight(Color oColor) {
		m_oColShipHighlight = oColor;
	}
	
	public void setColorShotMark(Color oColor) {
		m_oColShotMark = oColor;
	}

	public void setColorLastShotMark(Color oColor) {
		m_oColLastShotMark = oColor;
	}
	
	public void setImageShipSegment(Image oImage) {
		m_oImgShipSegment = oImage;
	}

	public void setImageShipSegmentSunk(Image oImage) {
		m_oImgShipSegmentSunk = oImage;
	}

	public void setShootCursor(Cursor bShootCursor) {
		m_bShootCursor = bShootCursor;
	}
	
	public void paint(Graphics g) {
		if (g == null) return;
		Dimension oReDim = getSize();
		int iNumLines;
		int i;
		int iLength;
		Point oPoint = null;
		Color oColShotMark = null;
		
		m_bPainting = true;
		//System.out.println("Starting painting of BattleShipsField");
		g.setColor(getBackground());
		g.fillRect(0, 0, oReDim.width - 1, oReDim.height - 1);
		super.paint(g);
		g.setColor(getForeground());
		g.drawRect(0, 0, oReDim.width - 1, oReDim.height - 1);
		if (m_iFieldType != FIELD_TYPE_SCORE) {
			iNumLines = m_iFieldWidth - 1;
			iLength = getBounds().height;
			for (i = 1; i <= iNumLines; i++) {
				g.drawLine(i * m_iCellWidth, 0, i * m_iCellWidth, iLength - 1);
			}
			iLength = getBounds().width;
			for (i = 1; i <= iNumLines; i++) {
				g.drawLine(0, i * m_iCellWidth, iLength - 1, i * m_iCellWidth);
			}
			ListIterator<Point> list = m_alShots.listIterator();
			while (list.hasNext()) {
				oPoint = list.next();
				if (list.hasNext()) {
					oColShotMark = m_oColShotMark;
				} else {
					oColShotMark = m_oColLastShotMark;
				}
				paintShot(g, oPoint, oColShotMark);
			}
			if (DEBUG) {
				g.setColor(getForeground());
				for (i = 1; i <= iNumLines; i++) {
					g.drawString(String.valueOf(i - 1), 2, i * m_iCellWidth - Math.round(m_iCellWidth / 2) + 5);
				}
				g.drawString(String.valueOf(i - 1), 2, i * m_iCellWidth - Math.round(m_iCellWidth / 2) + 5);
				iLength = getBounds().width;
				for (i = 1; i <= iNumLines; i++) {
					if (i > 1) {
						g.drawString(String.valueOf(i - 1), i * m_iCellWidth - Math.round(m_iCellWidth / 2) - 6, m_iCellWidth - 3);
					}
				}
				g.drawString(String.valueOf(i - 1), i * m_iCellWidth - Math.round(m_iCellWidth / 2) - 6, m_iCellWidth - 3);
			}
		}
		m_bPainting = false;
		//System.out.println("Finished painting of BattleShipsField");
	}

	private BattleShip addShip(BattleShip oBattleShip) {
		m_alBattleShips.add(oBattleShip);
		return (BattleShip)add(oBattleShip);
	}
	
	public void addShipsAndInfo() {
		BattleShip oBattleShip = null;
		TextDisplayPanel oLabel = null;
		BattleShipsPanel oBsp = getBattleShipsPanel();
		int iHeight;
		if (m_iCellWidth < 11) {
			iHeight = 11;
		} else {
			iHeight = m_iCellWidth;
		}
		Font oFntInfo = new Font("SansSerif", Font.BOLD, iHeight);
		FontMetrics oFntMetr = getFontMetrics(oFntInfo);
		String sTmp = "";
		Dimension oDimTmp = null;
		int iWidth = 0;
		int iMaxWidthShip = 0;
		int iMaxWidthLabel = 0;
		int iType;
		int iShipTypeCount;
		int iTotalShipCount = 0;
		int iDirection = 0;
		int iYPos = 0;
		for (iType = 1; iType <= m_iMaxShipType; iType++) {
			iShipTypeCount = m_iShipTypeCount[iType - 1];
			if (iShipTypeCount > 0) {
				switch (iType) {
					case 3:
					case 5:
						iDirection = 4;
						break;
					default:
						iDirection = 3;
						break;
				}
				oBattleShip = addShip(new BattleShip(m_iCellWidth, iType, false));
				oBattleShip.setForeground(m_oColShip);
				oBattleShip.setColorSunk(m_oColShipSunk);
				oBattleShip.setImageSegment(m_oImgShipSegment);
				oBattleShip.setImageSegmentSunk(m_oImgShipSegmentSunk);
				oBattleShip.setColorHitMark(m_oColShipHitMark);
				oBattleShip.setColorHighlight(m_oColShipHighlight);
				oBattleShip.setColorShotMark(m_oColShotMark);
				oBattleShip.setDirection(iDirection);
				oBattleShip.setPosition(1, iYPos + 1);
				oBattleShip.setLocked(true);
				sTmp = oBsp.getString("BattleShipType" + iType);
				oLabel = (TextDisplayPanel)add(new TextDisplayPanel());
				oLabel.setVisible(false);
				oLabel.setFont(oFntInfo);
				oLabel.setText(sTmp, TextDisplayPanel.AUTO_RESIZE);
				iWidth = oFntMetr.stringWidth(sTmp) + 10;
				//oLabel.setBounds((int)Math.round(m_iCellWidth * 7.5), iYPos * m_iCellWidth + m_iCellWidth - 3, iWidth, oFntMetr.getHeight());
				oLabel.setLocation((int)Math.round(m_iCellWidth * 7.5), iYPos * m_iCellWidth + m_iCellWidth - 3);
				iYPos += Math.round(oBattleShip.getSize().height / m_iCellWidth) + 1;
				oDimTmp = oBattleShip.getSize();
				if (iMaxWidthShip < oDimTmp.width) iMaxWidthShip = oDimTmp.width;
				if (iMaxWidthLabel < iWidth) iMaxWidthLabel = iWidth;
				iTotalShipCount++;
			}
		}
		if (iMaxWidthShip < (int)Math.round(m_iCellWidth * 6)) iMaxWidthShip = (int)Math.round(m_iCellWidth * 6);
		iWidth = iMaxWidthShip + iMaxWidthLabel + (int)Math.round(m_iCellWidth * 2.5);
		setBounds(Math.round((oBsp.getSize().width - iWidth) / 2), getLocation().y, iWidth, iYPos * m_iCellWidth + m_iCellWidth);
	}
	
	public void setVisible(boolean bVisible) {
		Component[] oaComp = getComponents();
		int i;
		for (i = 0; i < oaComp.length; i++) {
			oaComp[i].setVisible(bVisible);
		}
		super.setVisible(bVisible);
	}
	
	public boolean addAllMyShips() {
		BattleShip oBattleShip = null;
		int iType;
		int iShipTypeCount;
		int iTotalShipCount = 0;
		int j;
		int iMaxShipAreaSum = 0;
		boolean bRet = true;
		Cursor oCur = new Cursor(Cursor.HAND_CURSOR);
		for (iType = 1; iType <= m_iMaxShipType; iType++) {
			iShipTypeCount = m_iShipTypeCount[iType - 1];
			if (iShipTypeCount > 0) {
				for (j = 1; j <= iShipTypeCount; j++) {
					oBattleShip = addShip(new BattleShip(m_iCellWidth, iType, false));
					oBattleShip.setForeground(m_oColShip);
					oBattleShip.setColorSunk(m_oColShipSunk);
					oBattleShip.setImageSegment(m_oImgShipSegment);
					oBattleShip.setImageSegmentSunk(m_oImgShipSegmentSunk);
					oBattleShip.setColorHitMark(m_oColShipHitMark);
					oBattleShip.setColorHighlight(m_oColShipHighlight);
					oBattleShip.setColorShotMark(m_oColShotMark);
					oBattleShip.setPosition(iTotalShipCount * 3 + 1, 1);
					iMaxShipAreaSum += oBattleShip.getMaxShipArea();
					if (m_iFieldType == FIELD_TYPE_SCORE) {
						oBattleShip.setLocked(true);
					} else {
						oBattleShip.setCursor(oCur);
					}
						
					iTotalShipCount++;
				}
			}
		}
		if (m_iFieldType != FIELD_TYPE_SCORE) {
			if (iMaxShipAreaSum > Math.pow(m_iFieldWidth * m_iCellWidth, 2)) {
				bRet = false;
			} else {
				setShipsRandomPosition(true, true);
			}
		}
		return bRet;
	}
	
	public void addAllEnemyShips(String sShipInfo) {
		String sTmp = "";
		BattleShip oBattleShip = null;
		StringTokenizer oStrTokenShip = null;
		StringTokenizer oStrToken;
		if (getComponentCount() > 0) return;
		oStrToken = new StringTokenizer(sShipInfo, "|");
		while (oStrToken.hasMoreTokens()) {
			sTmp = oStrToken.nextToken();
			oStrTokenShip = new StringTokenizer(sTmp, ";");
			int iType = Integer.parseInt(oStrTokenShip.nextToken());
			int iDirection = Integer.parseInt(oStrTokenShip.nextToken());
			int iXPos = Integer.parseInt(oStrTokenShip.nextToken());
			int iYPos = Integer.parseInt(oStrTokenShip.nextToken());
			oBattleShip = addShip(new BattleShip(m_iCellWidth, iType, true));
			oBattleShip.setForeground(m_oColShip);
			oBattleShip.setColorSunk(m_oColShipSunk);
			oBattleShip.setImageSegment(m_oImgShipSegment);
			oBattleShip.setImageSegmentSunk(m_oImgShipSegmentSunk);
			oBattleShip.setColorHitMark(m_oColShipHitMark);
			oBattleShip.setColorHighlight(m_oColShipHighlight);
			oBattleShip.setColorShotMark(m_oColShotMark);
			oBattleShip.setPosition(iXPos, iYPos);
			oBattleShip.setDirection(iDirection);
			oBattleShip.setLocked(true);
		}
	}
	
	public void addTestShips() {
		BattleShip ship;
		m_bsShipPattern = new BattleShip[m_iMaxShipType];
		ship = addShip(new BattleShip(4, 3, true));
		ship.setPosition(1, 1);
		ship.setDirection(1);
		m_bsShipPattern[2] = ship;
		
		ship = addShip(new BattleShip(4, 5, true));
		ship.setPosition(4, 1);
		ship.setDirection(1);
		m_bsShipPattern[4] = ship;
	}
	
	public void setShipTypeCount(int iType, int iCount) {
		m_iShipTypeCount[iType - 1] = iCount;
		m_activeShips[iType - 1] = iCount;
	}

	public void setShipsRandomPosition() {
		int rnd;
		boolean touchEdge;
		boolean noTouching;
		
		rnd = (int)Math.round(Math.random());
		touchEdge = (rnd == 0) ? false : true;
		noTouching = true;
		setShipsRandomPosition(touchEdge, noTouching);
	}
	
	public void setShipsRandomPosition(boolean touchEdge, boolean noTouching) {
		BattleShip oBattleShip = null;
		ListIterator<BattleShip> list = m_alBattleShips.listIterator();
		int iXPos = 0;
		int iYPos = 0;
		int iDirection;
		int width = 0;
		int height = 0;
		
		while (list.hasNext()) {
			oBattleShip = list.next();
			do {
				iDirection = (int)Math.round(Math.random() * 3) + 1;
				oBattleShip.setDirection(iDirection);
				width = oBattleShip.getSize().width / m_iCellWidth;
				height = oBattleShip.getSize().height / m_iCellWidth;
				iXPos = (int)Math.round(Math.random() * (m_iFieldWidth - width));
				iYPos = (int)Math.round(Math.random() * (m_iFieldWidth - height));
				oBattleShip.setPosition(iXPos, iYPos);
				/*try {
					oThr.sleep(1000);
				} catch (InterruptedException e) {}*/
				//System.out.println("width: " + width + " iXPos: " + iXPos + " cond: " + (m_iFieldWidth - width - 1));
			} while (shipsIntersect(noTouching) || !touchEdge && ((iXPos == 0 || iXPos > m_iFieldWidth - width - 1) || (iYPos == 0 || iYPos > m_iFieldWidth - height - 1)));
		}
	}
	
	public void removeAllShips() {
		m_alBattleShips.clear();
		removeAll();
	}
	
	private boolean shipsIntersect(boolean noTouching) {
		boolean bIntersects = false;
		Point oPnt = getShipsIntersectionPoint(false, noTouching);
		if (oPnt != null) bIntersects = true;
		return bIntersects;
	}
	
	public Point getShipsIntersectionPoint(boolean bHighlight, boolean bNoTouching) {
		BattleShip oBattleShip = null;
		BattleShip oBattleShipChk = null;
		Point oPntShips = null;
		boolean bIntersects = false;
		int i;
		int j;
		BattleShip[] oObjBattleShips = new BattleShip[m_alBattleShips.size()];
		oObjBattleShips = m_alBattleShips.toArray(oObjBattleShips);
		BattleShip[] oObjBattleShipsChk = oObjBattleShips;
		for (i = 0; i < oObjBattleShips.length; i++) {
			for (j = 0; j < oObjBattleShipsChk.length; j++) {
				if (!oObjBattleShips[i].equals(oObjBattleShipsChk[j])) {
					oBattleShip = oObjBattleShips[i];
					oBattleShipChk = oObjBattleShipsChk[j];
					if (oBattleShip.intersects(oBattleShipChk, bNoTouching)) {
						bIntersects = true;
						break;
					}
				}
			}
			if (bIntersects) break;
		}
		if (bIntersects) {
			if (bHighlight) {
				remove(oBattleShip);
				add(oBattleShip, 0);
				oBattleShip.setHighlight(true);
			}
			oPntShips = new Point(oBattleShip.getType(), oBattleShipChk.getType());
		}
		//return null;
		return oPntShips;
	}
	
	public String getShipInfo() {
		Point oPntPos = null;
		StringBuffer oStrBufShipInfo = new StringBuffer();
		boolean bRun = false;
		BattleShip oBattleShip = null;
		ListIterator<BattleShip> list = m_alBattleShips.listIterator();
		Cursor oCur = new Cursor(Cursor.DEFAULT_CURSOR);
		while (list.hasNext()) {
			if (bRun) oStrBufShipInfo.append("|");
			oBattleShip = list.next();

			// right, doesn't really belong here, but saves us another two iterations
			oBattleShip.setLocked(true);
			oBattleShip.setHighlight(false);
			oBattleShip.setCursor(oCur);

			oPntPos = oBattleShip.getPosition();
			oStrBufShipInfo.append(oBattleShip.getType());
			oStrBufShipInfo.append(";");
			oStrBufShipInfo.append(oBattleShip.getDirection());
			oStrBufShipInfo.append(";");
			oStrBufShipInfo.append(oPntPos.x);
			oStrBufShipInfo.append(";");
			oStrBufShipInfo.append(oPntPos.y);
			bRun = true;
		}
		return oStrBufShipInfo.toString();
	}

	public void sinkShip(int iShipType) {
		BattleShip oBattleShip = null;
		ListIterator<BattleShip> list = m_alBattleShips.listIterator();
		while (list.hasNext()) {
			oBattleShip = list.next();
			if (oBattleShip.getType() == iShipType && !oBattleShip.getSunk()) {
				oBattleShip.setSunk();
				m_activeShips[iShipType - 1]--;
				return;
			}
		}
	}
	
	public void showEnemyShips() {
		BattleShip oBattleShip = null;
		ListIterator<BattleShip> list = m_alBattleShips.listIterator();
		while (list.hasNext()) {
			oBattleShip = list.next();
			if (!oBattleShip.getSunk()) {
				oBattleShip.setForceVisibility();
			}
		}
	}

	public void passShot(boolean bEnemyShip, Point oPoint, boolean bHit, boolean bSunk, int iType, ArrayList<Point> fieldHits) {
		boolean bWon = false;
		if (bSunk) {
			m_iSunkCount += 1;
			//System.out.println("m_iSunkCount: " + m_iSunkCount + "	m_alBattleShips.size(): " +m_alBattleShips.size());
			if (m_iSunkCount == m_alBattleShips.size()) {
				bWon = true;
			}
		}
		// looking for other ships ...
		if (!bHit) {
			shoot(oPoint);
		} else {
			getBattleShipsPanel().passShot(bEnemyShip, oPoint, bHit, bSunk, iType, bWon, fieldHits);
		}
	}
	
	public boolean shoot(Point oPoint) {
		BattleShip oBattleShip = null;
		ListIterator<BattleShip> list = m_alBattleShips.listIterator();
		while (list.hasNext()) {
			oBattleShip = list.next();
			if (oBattleShip.shoot(oPoint)) {
				//System.out.println("finished shoot()");
				return true;
			}
		}
		addAndPaintShot(oPoint);
		return false;
	}
	
	public boolean hasActiveShips(int shipType) {
		boolean ret = false;
		if (m_activeShips[shipType - 1] > 0) {
			ret = true;
		}
		return ret;
	}
	
	private BattleShipsPanel getBattleShipsPanel() {
		return (BattleShipsPanel)(getParent().getParent());
	}
	
	public int getActiveShipsFieldPercent() {
        int percent;
		int activeShipFieldCount = 0;
		int inactiveShipFieldCount = 0;
		BattleShip oBattleShip = null;
		ListIterator<BattleShip> list = m_alBattleShips.listIterator();
		while (list.hasNext()) {
			oBattleShip = list.next();
			if (oBattleShip.getSunk()) {
				inactiveShipFieldCount++;
			} else {
				activeShipFieldCount++;
			}
		}
        percent = (int)Math.round((double)activeShipFieldCount / (double)(activeShipFieldCount + inactiveShipFieldCount) * 100d);
		return percent;
	}

	private void addAndPaintShot(Point oPoint) {
		if (!m_alShots.contains(oPoint)) {
			Graphics oGr = getGraphics();
			paintShot(oGr, oPoint, m_oColLastShotMark);
			if (!m_alShots.isEmpty()) {
				paintShot(oGr, m_alShots.get(m_alShots.size() - 1), m_oColShotMark);
			}
			m_alShots.add(oPoint);
			getBattleShipsPanel().passShot(m_bEnemyField, oPoint, false, false, 0, false, null);
		}
	}
	
	private void waitForPaint() {
		while (m_bPainting) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
	}
	
	public void setPlaying(boolean bPlaying) {
		Cursor oCur;
		if (bPlaying) {
			oCur = m_bShootCursor;
		} else {
			oCur = new Cursor(Cursor.DEFAULT_CURSOR);
		}
		setCursor(oCur);
		m_bPlaying = bPlaying;
		BattleShip oBattleShip = null;
		ListIterator<BattleShip> list = m_alBattleShips.listIterator();
		while (list.hasNext()) {
			oBattleShip = list.next();
			oBattleShip.setPlaying(bPlaying);
			oBattleShip.setCursor(oCur);
		}
	}
	
	public void reset() {
		m_bPlaying = false;
		m_iSunkCount = 0;
		waitForPaint();
		m_alShots.clear();
		BattleShip oBattleShip = null;
		Cursor oCur = new Cursor(Cursor.HAND_CURSOR);
		ListIterator<BattleShip> list = m_alBattleShips.listIterator();
		while (list.hasNext()) {
			oBattleShip = list.next();
			if (m_bEnemyField) {
				remove(oBattleShip);
			} else {
				oBattleShip.reset(m_iFieldType);
				if (m_iFieldType == FIELD_TYPE_ME) oBattleShip.setCursor(oCur);
			}
		}
		if (m_bEnemyField) {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			m_alBattleShips.clear();
		}
		m_activeShips = m_iShipTypeCount.clone();
	}
	
	public void setAllBattleShipsVisible(boolean bVisible) {
		BattleShip oBattleShip = null;
		ListIterator<BattleShip> list = m_alBattleShips.listIterator();
		while (list.hasNext()) {
			oBattleShip = list.next();
			if (!oBattleShip.getSunk()) oBattleShip.setVisible(bVisible);
		}
	}
	
	public BattleShip getShipPattern(int shipType) {
		return m_bsShipPattern[shipType - 1];
	}
	
	//**public void BattleShipsField_MouseClicked(MouseEvent event) {
	public void i_ou(MouseEvent event) {
		if (event.getModifiers() == 16) {
			if (m_bEnemyField && m_bPlaying) {
				int iXPos = Math.round(event.getX() / m_iCellWidth);
				int iYPos = Math.round(event.getY() / m_iCellWidth);
				if (iXPos < m_iFieldWidth && iYPos < m_iFieldWidth) {
					addAndPaintShot(new Point(iXPos, iYPos));
				}
			}
		}
	}
}