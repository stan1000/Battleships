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

public class BattleShipsBotAI {
		
	private int m_fieldWidth;
	private Vector<Point> m_totalShotPoints;
	private Vector<Point> m_hitPoints;
	private Vector<Point> m_nextShotPoints;
	private Vector<Point> m_rowShotPoints;
	private Point m_firstPoint;
	private Point m_lastHitPoint;
		
	public BattleShipsBotAI(int fieldWidth) {
		m_totalShotPoints = new Vector<Point>();
		m_hitPoints = new Vector<Point>();
		m_nextShotPoints = new Vector<Point>();
		m_rowShotPoints = new Vector<Point>();
		m_fieldWidth = fieldWidth;
		m_firstPoint = new Point(4, 6);
		fillTotalShotPoints();
	}
	
	public Point getNextShot() {
		Point pnt;
		int index;
		int size1 = m_rowShotPoints.size();
		int size2 = m_nextShotPoints.size();
		if (size1 > 0) {
			pnt = m_rowShotPoints.elementAt(size1 - 1);
			m_rowShotPoints.removeElementAt(size1 - 1);
			m_nextShotPoints.removeElement(pnt);
			m_totalShotPoints.removeElement(pnt);
			System.out.println("El count after 1: (" + pnt.toString() + ")" + m_totalShotPoints.size());
		} else if (size2 > 0) {
			index = (int)Math.round(Math.random() * (m_nextShotPoints.size() - 1));
			pnt = m_nextShotPoints.elementAt(index);
			m_nextShotPoints.removeElementAt(index);
			m_totalShotPoints.removeElement(pnt);
			System.out.println("El count after 2: (" + pnt.toString() + ")" + m_totalShotPoints.size());
		} else {
			if (m_firstPoint != null) {
				pnt = m_firstPoint;
				m_totalShotPoints.removeElement(pnt);
				m_firstPoint = null;
			} else {
				index = (int)Math.round(Math.random() * (m_totalShotPoints.size() - 1));
				pnt = m_totalShotPoints.elementAt(index);
				m_totalShotPoints.removeElementAt(index);
			}
			System.out.println("El count after 4: (" + pnt.toString() + ")" + m_totalShotPoints.size());
		}
		return pnt;
	}

	public void reportLastShot(Point shot, boolean hit, boolean sunk, Vector<Point> fieldHits) {
		int size;
		int i, j;
		Point pnt, pnt1, pnt2;
		if (sunk) {
			System.out.println("fieldHits.size(): " + fieldHits.size());
			for (i = 0; i < fieldHits.size(); i++) {
				pnt = fieldHits.elementAt(i);
				System.out.println("removing point: " + pnt.toString());
				m_hitPoints.removeElement(pnt);
			}
			System.out.println("m_hitPoints.size(): " + m_hitPoints.size());
			for (i = 0; i < m_hitPoints.size(); i++) {
				pnt = m_hitPoints.elementAt(i);
				System.out.println("hit point: " + pnt.toString());
			}
			size = m_hitPoints.size();
			if (size > 0) {
				m_rowShotPoints.removeAllElements();
				m_nextShotPoints.removeAllElements();
				for (i = 0; i < size; i++) {
					pnt = m_hitPoints.elementAt(i);
					System.out.println("next shot point: " + pnt.toString());
					fillNextShotPoints(pnt, m_nextShotPoints);
				}
			//System.out.println("size > 1 (Surr full): " + m_nextShotPointsFull.size());
			}
		}
		if (sunk && m_hitPoints.size() == 0) {
			m_rowShotPoints.removeAllElements();
			m_nextShotPoints.removeAllElements();
			m_lastHitPoint = null;
		} else {
			if (hit || m_lastHitPoint != null) {
				if (hit) {
					if (!sunk) {
						m_lastHitPoint = shot;
						m_hitPoints.addElement(shot);
						for (i = 0; i < m_hitPoints.size(); i++) {
							pnt = m_hitPoints.elementAt(i);
							System.out.println("hit point after add: " + pnt.toString());
						}
					}
				} else {
					shot = m_lastHitPoint;
					m_lastHitPoint = null;
				}
				if (m_rowShotPoints.size() == 0) {
					//m_nextShotPoints.removeAllElements();
					//if (m_nextShotPoints.size() == 0)
					if (!sunk) {
						fillNextShotPoints(shot, m_nextShotPoints);
						System.out.println("individual next shot point: " + shot.toString());
						//System.out.println("Shot surr: " + m_nextShotPoints.size());
					}
					size = m_hitPoints.size();
					if (size > 1) {
						System.out.println("size > 1");
						//m_rowShotPoints.removeAllElements();
						pnt1 = m_hitPoints.elementAt(0);
						pnt2 = m_hitPoints.elementAt(size - 1);
						System.out.println("size > 1: pass1 - " + pnt1.toString() + " - " + pnt2.toString());
						if (pnt1.y > pnt2.y) {
							pnt = pnt1;
							pnt1 = pnt2;
							pnt2 = pnt;
						}
						pnt1 = getLowerPoint(pnt1);
						pnt2 = getHigherPoint(pnt2);
						if (pnt1.x == pnt2.x) {
							if (pnt1.y > 0) {
								pnt1 = new Point(pnt1.x, pnt1.y - 1);
							}
							if (pnt2.y < m_fieldWidth - 1) {
								pnt2 = new Point(pnt2.x, pnt2.y + 1);
							}
							for (i = pnt1.y; i <= pnt2.y; i++) {
								pnt = new Point(pnt1.x, i);
								addRowShotPoint(pnt);
							}
							System.out.println("size > 1 (x): pass2 - " + pnt1.toString() + " - " + pnt2.toString());
						} else if (pnt1.y == pnt2.y) {
							if (pnt1.x > 0) {
								pnt1 = new Point(pnt1.x - 1, pnt1.y);
							}
							if (pnt2.x < m_fieldWidth - 1) {
								pnt2 = new Point(pnt2.x + 1, pnt2.y);
							}
							for (i = pnt1.x; i <= pnt2.x; i++) {
								pnt = new Point(i, pnt1.y);
								addRowShotPoint(pnt);
							}
							System.out.println("size > 1 (y): pass2 - " + pnt1.toString() + " - " + pnt2.toString());
						} else if (pnt1.y - pnt2.y == pnt1.x - pnt2.x) {
							if (pnt1.x > 0 && pnt1.y > 0) {
								pnt1 = new Point(pnt1.x - 1, pnt1.y - 1);
							}
							if (pnt2.x < m_fieldWidth - 1 && pnt2.y < m_fieldWidth - 1) {
								pnt2 = new Point(pnt2.x + 1, pnt2.y + 1);
							}
							j = 0;
							for (i = pnt1.x; i <= pnt2.x; i++) {
								pnt = new Point(i, pnt1.y + j);
								addRowShotPoint(pnt);
								j++;
							}
							System.out.println("size > 1 (diag): pass2 - " + pnt1.toString() + " - " + pnt2.toString());
						} else if (pnt1.x + pnt1.y == pnt2.x + pnt2.y) {
							if (pnt1.x > 0 && pnt1.y < m_fieldWidth - 1) {
								pnt1 = new Point(pnt1.x - 1, pnt1.y + 1);
							}
							if (pnt2.x < m_fieldWidth - 1 && pnt2.y > 0) {
								pnt2 = new Point(pnt2.x + 1, pnt2.y - 1);
							}
							j = 0;
							for (i = pnt1.x; i <= pnt2.x; i++) {
								pnt = new Point(i, pnt1.y - j);
								addRowShotPoint(pnt);
								j++;
							}
							System.out.println("size > 1 (diag2): pass2 - " + pnt1.toString() + " - " + pnt2.toString());
						}
					}
				}
			} else {
				//m_lastHitPoint = null;
				//m_rowShotPoints.removeAllElements();
			}
				
		}
		//System.out.println("Shot result: " + shot.x + " - " + shot.y + " - " + hit + " - " + sunk);
	}
	
	private void addRowShotPoint(Point pnt) {
		if (m_totalShotPoints.contains(pnt)) {
			m_rowShotPoints.addElement(pnt);
		}
	}
	
	private Point getHigherPoint(Point pnt) {
		int i;
		Point tmp;
		for (i = 0; i < m_hitPoints.size(); i++) {
			tmp = m_hitPoints.elementAt(i);
			if (tmp.y == pnt.y && tmp.x > pnt.x ||
				tmp.x == pnt.x && tmp.y > pnt.y ||
				tmp.y - pnt.y == tmp.x - pnt.x && tmp.x > pnt.x ||
				tmp.x + tmp.y == pnt.x + pnt.y && tmp.x > pnt.x ) {				
				pnt = tmp;
			}
		}
		return pnt;
	}
	
	private Point getLowerPoint(Point pnt) {
		int i;
		Point tmp;
		for (i = 0; i < m_hitPoints.size(); i++) {
			tmp = m_hitPoints.elementAt(i);
			if (tmp.y == pnt.y && tmp.x < pnt.x ||
				tmp.x == pnt.x && tmp.y < pnt.y ||
				tmp.y - pnt.y == tmp.x - pnt.x && tmp.x < pnt.x ||
				tmp.x + tmp.y == pnt.x + pnt.y && tmp.x < pnt.x ) {				
				pnt = tmp;
			}
		}
		return pnt;
	}
	
	private void fillTotalShotPoints() {
		int x, y;
		for (y = 0; y < m_fieldWidth; y++) {
			for (x = 0; x < m_fieldWidth; x++) {
				m_totalShotPoints.addElement(new Point(x, y));
			}
		}
	}
	
	private void fillNextShotPoints(Point shot, Vector<Point> nextShotPoints) {
		int startX, startY, endX, endY;
		int x, y, i;
		Point pnt;
		if (shot.x == 0) startX = 0;
		else startX = shot.x - 1;
		if (shot.x == m_fieldWidth - 1) endX = m_fieldWidth - 1;
		else endX = shot.x + 1;
		if (shot.y == 0) startY = 0;
		else startY = shot.y - 1;
		if (shot.y == m_fieldWidth - 1) endY = m_fieldWidth - 1;
		else endY = shot.y + 1;
		for (x = startX; x <= endX; x++) {
			for (y = startY; y <= endY; y++) {
				pnt = new Point(x, y);
				if (m_totalShotPoints.contains(pnt) && !nextShotPoints.contains(pnt)) {
					nextShotPoints.addElement(pnt);
				}
			}
		}
		
	}
	
}
