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

public class BattleShip extends Container implements MouseListener, MouseMotionListener {

	private int m_iCellWidth;
	private int m_iType;
	private boolean m_bVertical;
	private int m_iDirection;
	private Point m_oPntMousePos;
	private boolean m_bLocked;
	private ArrayList<Point> m_alShots;
	private ArrayList<Point> m_alHits;
	private ArrayList<Point> m_fieldHits;
	private Polygon m_oPolCurrentShape;
	private int m_iHitCount;
	private int m_iMaxHits;
	private boolean m_bSunk;
	private boolean m_bEnemyShip;
	private Point m_oPntPos;
	private boolean m_bPlaying;
	private Dimension m_oDimOriginal;
	private boolean m_bHighlight;
	private ArrayList<Point> m_alCheckPoint;
	private Color m_oColSunk;
	private Color m_oColHitMark;
	private Color m_oColShotMark;
	private Color m_oColHighlight;
	private boolean m_bForceVisibility;
	private BattleShipsField m_oPlParent;
	private Image m_oImgSegment;
	private Image m_oImgSegmentSunk;
	private int m_iMaxShipArea;
	private boolean m_bPainting;

	public BattleShip(int iCellWidth, int iType, boolean bEnemyShip) {
		m_iCellWidth = iCellWidth;
		m_iType = iType;
		m_iDirection = 1;
		m_oPntMousePos = new Point(0, 0);
		m_bLocked = false;
		m_bPlaying = false;
		m_alShots = new ArrayList<Point>();
		m_alHits = new ArrayList<Point>();
		m_alCheckPoint = new ArrayList<Point>();
		m_fieldHits = new ArrayList<Point>();
		m_iHitCount = 0;
		m_iMaxHits = 0;
		m_bSunk = false;
		m_bEnemyShip = bEnemyShip;
		m_oPntPos = new Point(0, 0);
		m_bHighlight = false;
		m_bForceVisibility = false;
		m_iMaxShipArea = 0;
		m_bPainting = false;
		int iWidth = 0;
		int iHeight = 0;
		switch (m_iType) {
			case 1:
				iWidth = m_iCellWidth;
				iHeight = m_iCellWidth * 2;
				break;
			case 2:
				iWidth = m_iCellWidth;
				iHeight = m_iCellWidth * 3;
				break;
			case 3:
				iWidth = m_iCellWidth * 2;
				iHeight = m_iCellWidth * 3;
				break;
			case 4:
				iWidth = m_iCellWidth;
				iHeight = m_iCellWidth * 5;
				break;
			case 5:
				iWidth = m_iCellWidth * 2;
				iHeight = m_iCellWidth * 4;
				break;
			case 6:
				iWidth = m_iCellWidth;
				iHeight = m_iCellWidth * 6;
				m_iMaxHits = 6;
				break;
		}
		m_oDimOriginal = new Dimension(iWidth, iHeight);
		setSize(m_oDimOriginal);
		setShape();
		//**addMouseListener(new TheMouseAdapter((Object)this, "BattleShip"));
		addMouseListener(this);
		//addMouseListener(new TheMouseAdapter((Object)this, "do"));
		//**addMouseMotionListener(new TheMouseMotionAdapter((Object)this, "BattleShip"));
		addMouseMotionListener(this);
		//addMouseMotionListener(new TheMouseMotionAdapter((Object)this, "for"));
	}
	
	public void setColorSunk(Color oColor) {
		m_oColSunk = oColor;
	}
	
	public void setColorHitMark(Color oColor) {
		m_oColHitMark = oColor;
	}
	
	public void setColorHighlight(Color oColor) {
		m_oColHighlight = oColor;
	}
	
	public void setColorShotMark(Color oColor) {
		m_oColShotMark = oColor;
	}

	public void setImageSegment(Image oImage) {
		m_oImgSegment = oImage;
	}

	public void setImageSegmentSunk(Image oImage) {
		m_oImgSegmentSunk = oImage;
	}

	public void setForceVisibility() {
		m_bForceVisibility = true;
		repaint();
	}
	
	public void setForeground(Color oColor) {
		m_oPlParent = (BattleShipsField)getParent();
		super.setForeground(oColor);
	}
	
	private void buildDiagonalShip() {
		int i;
		Integer oInt;
		ArrayList<Integer> alXCoordsEven = new ArrayList<Integer>();
		ArrayList<Integer> alXCoordsOdd = new ArrayList<Integer>();
		ArrayList<Integer> alXCoords = new ArrayList<Integer>();
		ArrayList<Integer> alYCoordsEven = new ArrayList<Integer>();
		ArrayList<Integer> alYCoordsOdd = new ArrayList<Integer>();
		ArrayList<Integer> alYCoords = new ArrayList<Integer>();
		ListIterator<Integer> list;
		Integer[] oObjYCoords;
		Integer[] oObjXCoords;
		Integer oTmp;

		// populate X coordinates
		int iHeight = Math.round(m_oDimOriginal.height / m_iCellWidth);
		for (i = 0; i <= iHeight; i++) {
			oInt = new Integer(i);
			if (Math.IEEEremainder(i, 2) == 0) {
				alXCoordsEven.add(0, oInt);
				alXCoordsEven.add(0, oInt);
			} else {
				alXCoordsOdd.add(oInt);
				alXCoordsOdd.add(oInt);
			}
		}

		// prepare Y coordinates
		list = alXCoordsEven.listIterator();
		while (list.hasNext()) {
			if (m_iDirection == 4 && Math.IEEEremainder(iHeight, 2) == 0) {
				alYCoordsEven.add(list.next());
			} else {
				alYCoordsEven.add(0, list.next());
			}
		}
		list = alXCoordsOdd.listIterator();
		while (list.hasNext()) {
			if (m_iDirection == 4 && Math.IEEEremainder(iHeight, 2) == 0) {
				alYCoordsOdd.add(list.next());
			} else {
				alYCoordsOdd.add(0, list.next());
			}
		}
		if (m_iDirection == 4 && Math.IEEEremainder(iHeight, 2) != 0) {
			list = alYCoordsOdd.listIterator();
			while (list.hasNext()) {
				alYCoords.add(list.next());
			}
			list = alYCoordsEven.listIterator();
			while (list.hasNext()) {
				alYCoords.add(list.next());
			}
		} else {
			list = alYCoordsEven.listIterator();
			while (list.hasNext()) {
				alYCoords.add(list.next());
			}
			list = alYCoordsOdd.listIterator();
			while (list.hasNext()) {
				alYCoords.add(list.next());
			}
		}
		
		// prepare X coordinates
		oTmp = alXCoordsEven.remove(alXCoordsEven.size() - 1);
		alXCoordsOdd.add(0, oTmp);
		
		list = alXCoordsOdd.listIterator();
		while (list.hasNext()) {
			alXCoords.add(list.next());
		}
		list = alXCoordsEven.listIterator();
		while (list.hasNext()) {
			alXCoords.add(list.next());
		}

		// copy coordinates into arrays
		oObjXCoords = new Integer[alXCoords.size()];
		oObjXCoords = alXCoords.toArray(oObjXCoords);
		oObjYCoords = new Integer[alYCoords.size()];
		oObjYCoords = alYCoords.toArray(oObjYCoords);

		// populate the polygon with points
		m_oPolCurrentShape = new Polygon();
		for (i = 0; i < oObjXCoords.length; i++) {
			m_oPolCurrentShape.addPoint((oObjXCoords[i]).intValue() * m_iCellWidth, (oObjYCoords[i]).intValue() * m_iCellWidth);
		}
	}
	
	private void setSize() {
		switch (m_iType) {
			case 3:
			case 5:
				switch (m_iDirection) {
					case 1:
					case 3:
						setSize(m_oDimOriginal.width, m_oDimOriginal.height);
						break;
					case 2:
					case 4:
						setSize(m_oDimOriginal.height, m_oDimOriginal.width);
						break;
				}
				break;
			default:
				switch (m_iDirection) {
					case 1:
						setSize(m_oDimOriginal.width, m_oDimOriginal.height);
						break;
					case 2:
					case 4:
						setSize(m_oDimOriginal.height, m_oDimOriginal.height);
						break;
					case 3:
						setSize(m_oDimOriginal.height, m_oDimOriginal.width);
						break;
				}
				break;
		}
	}

	private void setMaxShipArea(int iWidth, int iHeight, boolean bForDiagonal) {
		if (bForDiagonal) {
			m_iMaxShipArea = m_iCellWidth * (iWidth * 5 + m_iCellWidth * 4);
		} else {
			m_iMaxShipArea = (iWidth + m_iCellWidth) * (iHeight + m_iCellWidth);
		}
		//System.out.println("Ship " + m_iType + " max area: " + m_iMaxShipArea + " (diagonal: " + bForDiagonal + ")");
	}
	
	private void setShape() {
		Rectangle oReBounds = getBounds();
		switch (m_iType) {
			case 3:
				if (m_iMaxShipArea == 0) setMaxShipArea(m_oDimOriginal.width, m_oDimOriginal.height, false);
				switch (m_iDirection) {
					case 1:
						m_oPolCurrentShape = new Polygon(new int[]{0, Math.round(oReBounds.width / 2), Math.round(oReBounds.width / 2), oReBounds.width, oReBounds.width, Math.round(oReBounds.width / 2), Math.round(oReBounds.width / 2), 0},
											   new int[]{0, 0, Math.round(oReBounds.height / 3), Math.round(oReBounds.height / 3), Math.round(oReBounds.height / 3) * 2, Math.round(oReBounds.height / 3) * 2, oReBounds.height, oReBounds.height},
											   8);
						break;
					case 2:
						m_oPolCurrentShape = new Polygon(new int[]{0, oReBounds.width, oReBounds.width, Math.round(oReBounds.width / 3) * 2, Math.round(oReBounds.width / 3) * 2, Math.round(oReBounds.width / 3), Math.round(oReBounds.width / 3), 0},
											   new int[]{0, 0, Math.round(oReBounds.height / 2), Math.round(oReBounds.height / 2), oReBounds.height, oReBounds.height, Math.round(oReBounds.height / 2), Math.round(oReBounds.height / 2)},
											   8);
						break;
					case 3:
						m_oPolCurrentShape = new Polygon(new int[]{0, Math.round(oReBounds.width / 2), Math.round(oReBounds.width / 2), oReBounds.width, oReBounds.width, Math.round(oReBounds.width / 2), Math.round(oReBounds.width / 2), 0},
											   new int[]{Math.round(oReBounds.height / 3), Math.round(oReBounds.height / 3), 0, 0, oReBounds.height, oReBounds.height, Math.round(oReBounds.height / 3) * 2, Math.round(oReBounds.height / 3) * 2},
											   8);
						break;
					case 4:
						m_oPolCurrentShape = new Polygon(new int[]{0, Math.round(oReBounds.width / 3), Math.round(oReBounds.width / 3), Math.round(oReBounds.width / 3) * 2, Math.round(oReBounds.width / 3) * 2, oReBounds.width, oReBounds.width, 0},
											   new int[]{Math.round(oReBounds.height / 2), Math.round(oReBounds.height / 2), 0, 0, Math.round(oReBounds.height / 2), Math.round(oReBounds.height / 2), oReBounds.height, oReBounds.height},
											   8);
						break;
				}
				break;
			case 5:
				if (m_iMaxShipArea == 0) setMaxShipArea(m_oDimOriginal.width, m_oDimOriginal.height, false);
				switch (m_iDirection) {
					case 1:
					case 3:
						m_oPolCurrentShape = new Polygon(new int[]{0, Math.round(oReBounds.width / 2), Math.round(oReBounds.width / 2), oReBounds.width, oReBounds.width, Math.round(oReBounds.width / 2), Math.round(oReBounds.width / 2), 0},
												new int[]{0, 0, Math.round(oReBounds.height / 4), Math.round(oReBounds.height / 4), oReBounds.height, oReBounds.height, Math.round(oReBounds.height / 4) * 3, Math.round(oReBounds.height / 4) * 3},
												8);
						break;
					case 2:
					case 4:
						m_oPolCurrentShape = new Polygon(new int[]{0, Math.round(oReBounds.width / 4), Math.round(oReBounds.width / 4), oReBounds.width, oReBounds.width, Math.round(oReBounds.width / 4) * 3, Math.round(oReBounds.width / 4) * 3, 0},
												new int[]{Math.round(oReBounds.height / 2), Math.round(oReBounds.height / 2), 0, 0, Math.round(oReBounds.height / 2), Math.round(oReBounds.height / 2), oReBounds.height, oReBounds.height},
												8);
						break;
				}
				break;
			default:
				if (m_iMaxShipArea == 0) setMaxShipArea(m_oDimOriginal.height, m_oDimOriginal.height, true);
				switch (m_iDirection) {
					case 1:
					case 3:
						m_oPolCurrentShape = new Polygon(new int[]{0, oReBounds.width, oReBounds.width, 0},
														 new int[]{0, 0, oReBounds.height, oReBounds.height},
														4);
						break;
					case 2:
					case 4:
						buildDiagonalShip();
						break;
				}
				break;
		}
		// desist from removing the checkpoints while paint() is using them		
		waitForPaint();
		m_alCheckPoint.clear();
		int iXCoord;
		int iYCoord;
		int iWidth = oReBounds.width;
		int iHeight = oReBounds.height;
		int iStart = Math.round(m_iCellWidth / 2);
		Point oPoint;
		// add the checkpoints
		for (iXCoord = iStart; iXCoord < iWidth; iXCoord+=m_iCellWidth) {
			for (iYCoord = iStart; iYCoord < iHeight; iYCoord+=m_iCellWidth) {
				oPoint = new Point(iXCoord, iYCoord);
				if (m_oPolCurrentShape.contains(oPoint)) {
					m_alCheckPoint.add(oPoint);
				}
			}
		}
		if (m_iMaxHits == 0) m_iMaxHits = m_alCheckPoint.size();
	}
	
	public void setPlaying(boolean bPlaying) {
		m_bPlaying = bPlaying;
	}
	
	public void setPosition(int iXPos, int iYPos) {
		m_oPntPos.x = iXPos;
		m_oPntPos.y = iYPos;
		setLocation(iXPos * m_iCellWidth, iYPos * m_iCellWidth);
	}
	
	public Point getPosition() {
		return m_oPntPos;
	}
	
	public void setLocked(boolean bLocked) {
		m_bLocked = bLocked;
	}
	
	public int getType() {
		return m_iType;
	}

	public int getDirection() {
		return m_iDirection;
	}
	
	public void setDirection(int iDirection) {
		m_iDirection = iDirection;
		setSize();
		setShape();
	}
	
	public void reset(int iFieldType) {
		if (iFieldType == BattleShipsField.FIELD_TYPE_SCORE) {
			m_bSunk = false;
		} else {
			m_bLocked = false;
			m_bPlaying = false;
			m_iHitCount = 0;
			m_bSunk = false;
			waitForPaint();
			m_alShots.clear();
			m_alHits.clear();
			m_fieldHits.clear();
		}
	}
	
	// for own field, getting from other player
	public boolean shoot(Point oPoint) {
		Rectangle oReBounds = getBounds();
		int iXPos = oPoint.x * m_iCellWidth - oReBounds.x;
		int iYPos = oPoint.y * m_iCellWidth - oReBounds.y;
		boolean bRet = m_oPolCurrentShape.contains(iXPos + Math.round(m_iCellWidth / 2), iYPos + Math.round(m_iCellWidth / 2));
		if (bRet) {
			Point oPntHit = new Point(iXPos, iYPos);
			if (!m_alHits.contains(oPntHit)) {
				addAndPaintHit(oPntHit);
				m_fieldHits.add(oPoint);
				m_oPlParent.passShot(m_bEnemyShip, oPoint, bRet, m_bSunk, m_iType, m_fieldHits);
			}
		}
		return bRet;
	}
	
	// for enemy field, clicking with the mouse
	private void shoot(int iXPos, int iYPos) {
		//System.out.println("Click at X " + iXPos + "  Y " + iYPos);
		Point oPntHit = new Point(Math.round(iXPos / m_iCellWidth) * m_iCellWidth, Math.round(iYPos / m_iCellWidth) * m_iCellWidth);
		boolean bRet = m_oPolCurrentShape.contains(oPntHit.x + Math.round(m_iCellWidth / 2), oPntHit.y + Math.round(m_iCellWidth / 2));
		Point oPntLoc = null;
		//System.out.println("Calculated X " + oPntHit.x + "  Y " + oPntHit.y + "\n" +
		//				   "Center X " + (oPntHit.x + Math.round(m_iCellWidth / 2)) + "  Y " + (oPntHit.y + Math.round(m_iCellWidth / 2)));
		if (bRet) {
			if (!m_bSunk) {
				if (!m_alHits.contains(oPntHit)) {
					addAndPaintHit(oPntHit);
					oPntLoc = getLocation();
					m_oPlParent.passShot(m_bEnemyShip, new Point(Math.round((iXPos + oPntLoc.x) / m_iCellWidth), Math.round((iYPos + oPntLoc.y) / m_iCellWidth)), true, m_bSunk, m_iType, m_fieldHits);
				}
			}
		} else {
			if (!m_alShots.contains(oPntHit)) {
				m_alShots.add(oPntHit);
				//System.out.println("Ship " + m_iType + ", shot at " + oPntHit.toString());
				oPntLoc = getLocation();
				m_oPlParent.passShot(m_bEnemyShip, new Point(Math.round((iXPos + oPntLoc.x) / m_iCellWidth), Math.round((iYPos + oPntLoc.y) / m_iCellWidth)), false, false, m_iType, m_fieldHits);
			}
		}
	}
	
	private void addAndPaintHit(Point oPoint) {
		Graphics oGr = getGraphics();

		m_iHitCount = m_iHitCount + 1;
		m_alHits.add(oPoint);
		if (m_iHitCount == m_iMaxHits) {
			if (!isVisible()) setVisible(true);
			m_bSunk = true;
			if (!m_bEnemyShip) paintShipSegments(oGr);
			paintHits(oGr);
			//System.out.println("Sunk the ship.");
		} else {
			if (isVisible()) paintHit(oGr, oPoint, m_oColHitMark);
		}
	}
	
	private void waitForPaint() {
		while (m_bPainting) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
	}
	
	public void setSunk() {
		m_bSunk = true;
		paintShipSegments(getGraphics());
	}
	
	public void setHighlight(boolean bHighlight) {
		m_bHighlight = bHighlight;
		repaint();
	}
	
	public boolean intersects(BattleShip oBattleShip, boolean bNoTouching) {
		Point oPntLoc = getLocation();
		Point oPntLocChk = oBattleShip.getLocation();
		int iXOffset = oPntLocChk.x - oPntLoc.x;
		int iYOffset = oPntLocChk.y - oPntLoc.y;
		boolean bIntersects = false;
		Point oPntCheckPoint;
		int i;
		Point[] oaPntCheckPoint = oBattleShip.getCheckPoints();
		for (i = 0; i < oaPntCheckPoint.length; i++) {
			oPntCheckPoint = new Point(oaPntCheckPoint[i].x + iXOffset, oaPntCheckPoint[i].y + iYOffset);
			if (m_oPolCurrentShape.contains(oPntCheckPoint)) {
				bIntersects = true;
				break;
			}
			if (bNoTouching) {
				// check surroundings too
				if (m_oPolCurrentShape.contains(new Point(oPntCheckPoint.x + m_iCellWidth, oPntCheckPoint.y)) ||
					m_oPolCurrentShape.contains(new Point(oPntCheckPoint.x + m_iCellWidth, oPntCheckPoint.y - m_iCellWidth)) ||
					m_oPolCurrentShape.contains(new Point(oPntCheckPoint.x, oPntCheckPoint.y - m_iCellWidth)) ||
					m_oPolCurrentShape.contains(new Point(oPntCheckPoint.x - m_iCellWidth, oPntCheckPoint.y - m_iCellWidth)) ||
					m_oPolCurrentShape.contains(new Point(oPntCheckPoint.x - m_iCellWidth, oPntCheckPoint.y)) ||
					m_oPolCurrentShape.contains(new Point(oPntCheckPoint.x - m_iCellWidth, oPntCheckPoint.y + m_iCellWidth)) ||
					m_oPolCurrentShape.contains(new Point(oPntCheckPoint.x, oPntCheckPoint.y + m_iCellWidth)) ||
					m_oPolCurrentShape.contains(new Point(oPntCheckPoint.x + m_iCellWidth, oPntCheckPoint.y + m_iCellWidth))) {
					bIntersects = true;
					break;
				}
			}
		}
		return bIntersects;
	}
	
	public Point[] getCheckPoints() {
		Point[] oPnt = new Point[m_alCheckPoint.size()];
		oPnt = m_alCheckPoint.toArray(oPnt);
		return oPnt;
	}
	
	public boolean getSunk() {
		return m_bSunk;
	}
	
	public int getMaxShipArea() {
		return m_iMaxShipArea;
	}
	
	public ArrayList<Point> getPossibleHitpoints() {
		int x, y, i;
		Point tmp;
		int offset = Math.round(m_iCellWidth / 2);
		ArrayList<Point> posHitPoints = new ArrayList<Point>();
		for (i = 0; i < m_alCheckPoint.size(); i++) {
			tmp = m_alCheckPoint.get(i);
			x = (tmp.x - offset) / m_iCellWidth + m_oPntPos.x;
			y = (tmp.y - offset) / m_iCellWidth + m_oPntPos.y;
			posHitPoints.add(new Point(x, y));
		}
		return posHitPoints;
	}
	
	public void paint(Graphics g) {
		if (g == null) return;
		//System.out.println("Starting painting of ship " + m_iType);
		m_bPainting = true;
		if (!m_bEnemyShip || m_bForceVisibility) {
			paintShipSegments(g);
		}
		paintHits(g);
		if (!m_bEnemyShip && m_bHighlight) {
			Dimension oDim = getSize();
			g.setColor(m_oColHighlight);
			g.drawRect(1, 1, oDim.width - 2, oDim.height - 2);
			g.drawRect(2, 2, oDim.width - 4, oDim.height - 4);
		}
		super.paint(g);
		m_bPainting = false;
		//System.out.println("Finished painting of ship " + m_iType);
	}
	
	//**public void BattleShip_MouseEntered(MouseEvent event)
	//public void do_oh(MouseEvent event) {
	public void mouseEntered(MouseEvent event) {
		if (!m_bEnemyShip && !m_bLocked) {
			m_bHighlight = true;
			repaint();
		}
	}

	//**public void BattleShip_MouseExited(MouseEvent event) {
	//public void do_od(MouseEvent event) {
	public void mouseExited(MouseEvent event) {
		if (!m_bEnemyShip && !m_bLocked) {
			m_bHighlight = false;
			repaint();
		}
	}

	//**public void BattleShip_MouseClicked(MouseEvent event) {
	//public void do_ou(MouseEvent event) {
	public void mouseClicked(MouseEvent event) {
		if (event.getModifiers() == 4) {
			if (!m_bLocked) {
				Rectangle oReBounds = getBounds();
				Rectangle oReBoundsParent = m_oPlParent.getBounds();
				if (oReBounds.x + oReBounds.height <= oReBoundsParent.width && oReBounds.y + oReBounds.width <= oReBoundsParent.height) {
					if (m_iDirection == 4) {
						m_iDirection = 1;
					} else {
						m_iDirection++;
					}
					setSize();
					setShape();
					//m_oPolCurrentShape = Graphics2DFactory.rotateShip(m_oPolCurrentShape);
				}
			}
		} else if (event.getModifiers() == 16) {
			if (m_bEnemyShip && m_bPlaying) {
				shoot(event.getX(), event.getY());
			}
		}
	}

	//**public void BattleShip_MousePressed(MouseEvent event) {
	//public void do_oi(MouseEvent event) {
	public void mousePressed(MouseEvent event) {
		if (event.getModifiers() == 16) {
			if (!m_bLocked) {
				m_oPntMousePos.x = event.getX();
				m_oPntMousePos.y = event.getY();
				// Remark:
				// Had to disable remove/add ship due to bug: https://bugs.openjdk.java.net/browse/JDK-8061636
				// Ship drag/click behaviour slightly changes, but at least the game is playable again ...
				//m_oPlParent.remove(this);
				//m_oPlParent.add(this, 0);
				//repaint();
			}
		}
	}

	public void mouseReleased(MouseEvent event) {}
	
	//**public void BattleShip_MouseDragged(MouseEvent event) {
	//public void for_ti(MouseEvent event) {
	public void mouseDragged(MouseEvent event) {
		if (event.getModifiers() == 16 || event.getModifiers() == 0) {
			if (!m_bLocked) {
				Rectangle oReBounds = getBounds();
				Rectangle oReBoundsParent = m_oPlParent.getBounds();
				int iMousePosX;
				int iMousePosY;
				int iNewPosX;
				int iNewPosY;
				iMousePosX = event.getX();
				iMousePosY = event.getY();
				iNewPosX = oReBounds.x + ((iMousePosX - m_oPntMousePos.x));
				if (iNewPosX < 0 || iNewPosX > oReBoundsParent.width - oReBounds.width + Math.round(m_iCellWidth / 2)) {
					iNewPosX = oReBounds.x;
				}
				iNewPosY = oReBounds.y + ((iMousePosY - m_oPntMousePos.y));
				if (iNewPosY < 0 || iNewPosY > oReBoundsParent.height - oReBounds.height + Math.round(m_iCellWidth / 2)) {
					iNewPosY = oReBounds.y;
				}
				setPosition(Math.round(iNewPosX / m_iCellWidth), Math.round(iNewPosY / m_iCellWidth));
			}
		}
	}
	
	public void mouseMoved(MouseEvent event) {}
	
	private void paintShipSegments(Graphics g) {
		Point oPoint = null;
		Color oColor;
		Image oImgSegment;

		if (g == null) return;
		if (m_bSunk) {
			oColor = m_oColSunk;
			oImgSegment = m_oImgSegmentSunk;
		} else {
			oColor = getForeground();
			oImgSegment = m_oImgSegment;
		}
		if (oImgSegment == null) {
			//System.out.println("paintShipSegments::oImgSegment == null");
			g.setColor(oColor);
			g.fillPolygon(m_oPolCurrentShape);
		} else {
			//System.out.println(this.toString() + ": paintShipSegments::painting segments");
			ListIterator<Point> list = m_alCheckPoint.listIterator();
			int i = 0;
			while (list.hasNext()) {
				i++;
				oPoint = list.next();
				g.drawImage(oImgSegment, oPoint.x - Math.round(m_iCellWidth / 2) + 1, oPoint.y - Math.round(m_iCellWidth / 2) + 1, this);
			}
		}
	}

	private void paintHits(Graphics g) {
		Point oPoint = null;

		ListIterator<Point> list = m_alHits.listIterator();
		while (list.hasNext()) {
			oPoint = list.next();
			//System.out.println("Ship " + m_iType + " (is enemy: " + m_bEnemyShip + ") hit at X: " + oPoint.x + "  Y: " + oPoint.y);
			paintHit(g, oPoint, m_oColHitMark);
		}
	}
		
	private void paintHit(Graphics g, Point oPoint, Color oColor) {
		Image oImgSegment;

		if (g == null) return;
		if (m_bSunk) {
			oImgSegment = m_oImgSegmentSunk;
		} else {
			oImgSegment = m_oImgSegment;
		}
		if (m_bEnemyShip && !m_bForceVisibility) {
			//System.out.println(this.toString() + ": paintHit::painting segment");
			g.drawImage(oImgSegment, oPoint.x + 1, oPoint.y + 1, this);
		}
		//System.out.println(this.toString() + ": paintHit::painting hit");
		g.setColor(oColor);
		// main lines
		g.drawLine(oPoint.x + 1, oPoint.y + 1, oPoint.x + m_iCellWidth - 1, oPoint.y + m_iCellWidth - 1);
		g.drawLine(oPoint.x + 1, oPoint.y + m_iCellWidth - 1, oPoint.x + m_iCellWidth - 1, oPoint.y + 1);

		// "\"
		g.drawLine(oPoint.x + 2, oPoint.y + 1, oPoint.x + m_iCellWidth - 1, oPoint.y + m_iCellWidth - 2);
		g.drawLine(oPoint.x + 1, oPoint.y + 2, oPoint.x + m_iCellWidth - 2, oPoint.y + m_iCellWidth - 1);
		// "/"
		g.drawLine(oPoint.x + 2, oPoint.y + m_iCellWidth - 1, oPoint.x + m_iCellWidth - 1, oPoint.y + 2);
		g.drawLine(oPoint.x + 1, oPoint.y + m_iCellWidth - 2, oPoint.x + m_iCellWidth - 2, oPoint.y + 1);
	}
	
}