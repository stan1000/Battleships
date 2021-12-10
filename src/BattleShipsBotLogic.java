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

public class BattleShipsBotLogic {
		
	private int m_fieldWidth;
	private ArrayList<Point> m_totalShotPoints;
	private ArrayList<Point> m_hitPoints;
	private ArrayList<Point> m_nextShotPoints;
	private ArrayList<Point> m_rowShotPoints;
	private ArrayList<Point> m_totalHitPoints;
	private Point m_firstPoint;
	private Point m_lastHitPoint;
	private Point m_lastShot;
	private BattleShipsField m_plTestShips;
	private ArrayList<ArrayList<Point>> m_firingSolutions;
	private ArrayList<Point> m_lastFiringSolution;
	private BattleShipsField m_plEnemyScore;
	private boolean m_seekBorders;
	
	public static final int BOT_DELAY = 500;
		
	public BattleShipsBotLogic(int fieldWidth, BattleShipsField testShips, BattleShipsField enemyScore) {
		m_totalShotPoints = new ArrayList<Point>();
		m_hitPoints = new ArrayList<Point>();
		m_nextShotPoints = new ArrayList<Point>();
		m_rowShotPoints = new ArrayList<Point>();
		m_totalHitPoints = new ArrayList<Point>();
		m_fieldWidth = fieldWidth;
		m_firstPoint = new Point(14, 3);
		m_plTestShips = testShips;
		m_plEnemyScore = enemyScore;
		m_firingSolutions = new ArrayList<ArrayList<Point>>();
		m_lastFiringSolution = new ArrayList<Point>();
		m_seekBorders = false;
		fillTotalShotPoints();
		/*addRowShotPoint(new Point(12, 9));
		addRowShotPoint(new Point(10, 10));
		addRowShotPoint(new Point(9, 10));
		addRowShotPoint(new Point(13, 11));
		addRowShotPoint(new Point(9, 11));
		addRowShotPoint(new Point(11, 12));
		addRowShotPoint(new Point(12, 10));
		addRowShotPoint(new Point(11, 10));*/
	}
	
	public Point getNextShot() {
		Point pnt;
		int index;
		int size1 = m_rowShotPoints.size();
		int size2 = m_nextShotPoints.size();
		if (size1 > 0) {
			pnt = m_rowShotPoints.get(size1 - 1);
			m_rowShotPoints.remove(size1 - 1);
			m_nextShotPoints.remove(pnt);
			m_totalShotPoints.remove(pnt);
			System.out.println("El count after 1: (" + pnt.toString() + ")" + m_totalShotPoints.size());
		} else if (size2 > 0) {
			index = (int)Math.round(Math.random() * (size2 - 1));
			pnt = m_nextShotPoints.get(index);
			m_nextShotPoints.remove(index);
			m_totalShotPoints.remove(pnt);
			System.out.println("El count after 2: (" + pnt.toString() + ")" + m_totalShotPoints.size());
		} else {
			if (m_firstPoint != null) {
				pnt = m_firstPoint;
				m_totalShotPoints.remove(pnt);
				m_firstPoint = null;
			} else {
				pnt = getNextShotPoint();
			}
			System.out.println("El count after 4: (" + pnt.toString() + ")" + m_totalShotPoints.size());
		}
		return pnt;
	}

	public void reportLastShot(Point shot, boolean hit, boolean sunk, ArrayList<Point> fieldHits) {
		int size;
		int i, j;
		Point pnt, pnt1, pnt2;
		BattleShip shipPattern;
		boolean pointAdded = false;

		if (hit) {
			m_totalHitPoints.add(shot);
		}
		if (sunk) {
			//System.out.println("fieldHits.size(): " + fieldHits.size());
			for (i = 0; i < fieldHits.size(); i++) {
				pnt = fieldHits.get(i);
				//System.out.println("removing point: " + pnt.toString());
				m_hitPoints.remove(pnt);
			}
			//System.out.println("m_hitPoints.size(): " + m_hitPoints.size());
			/*for (i = 0; i < m_hitPoints.size(); i++) {
				pnt = m_hitPoints.get(i);
				System.out.println("hit point: " + pnt.toString());
			}*/
			size = m_hitPoints.size();
			if (size > 0) {
				m_rowShotPoints.clear();
				m_nextShotPoints.clear();
				m_firingSolutions.clear();
				m_lastFiringSolution.clear();
				for (i = 0; i < size; i++) {
					pnt = m_hitPoints.get(i);
					System.out.println("next shot point: " + pnt.toString());
					fillNextShotPoints(pnt, m_nextShotPoints);
				}
			//System.out.println("size > 1 (Surr full): " + m_nextShotPointsFull.size());
			}
		}
		if (sunk && m_hitPoints.size() == 0) {
			m_rowShotPoints.clear();
			m_nextShotPoints.clear();
			m_firingSolutions.clear();
			m_lastFiringSolution.clear();
			m_lastHitPoint = null;
		} else {
			//System.out.println("hit: " + (hit ? "true" : "false") + " - m_lastHitPoint: " + (m_lastHitPoint != null ? m_lastHitPoint.toString() : "null"));
			if (hit || m_lastHitPoint != null) {
				m_lastShot = shot;
				if (hit) {
					if (shot.x == 0 || shot.y == 0 || shot.x == m_fieldWidth - 1 || shot.y == m_fieldWidth - 1) {
						m_seekBorders = true;
					};
					if (!sunk) {
						m_lastHitPoint = shot;
						m_hitPoints.add(shot);
						for (i = 0; i < m_hitPoints.size(); i++) {
							pnt = m_hitPoints.get(i);
							System.out.println("hit point after add: " + pnt.toString());
						}
					}
				} else {
					shot = m_lastHitPoint;
					m_lastHitPoint = null;
				}
				if (!sunk) {
					if (m_hitPoints.size() == 3 && !m_lastFiringSolution.contains(m_lastShot) &&  
						!m_plEnemyScore.hasActiveShips(4) && !m_plEnemyScore.hasActiveShips(6)) {
							m_rowShotPoints.clear();
					}
					if (m_lastFiringSolution.contains(shot) && hit && m_rowShotPoints.size() == 0) {
						//System.out.println("Looking for special ships (1-after sub): Battleship");
						m_firingSolutions.clear();
						manageFiringSolutions(5);
					} else if (m_lastFiringSolution.contains(m_lastShot) && (!hit || m_rowShotPoints.size() == 0)) {
						if (!hit) {
							m_rowShotPoints.clear();
						}
						if (m_hitPoints.size() >= 3) {
							m_firingSolutions.clear();
						}
						applyFiringSolutionLogic("1");
					}
				}
				if (m_rowShotPoints.size() == 0) {
					if (!sunk) {
						fillNextShotPoints(shot, m_nextShotPoints);
						System.out.println("individual next shot point: " + shot.toString());
						//System.out.println("Shot surr: " + m_nextShotPoints.size());
					}
					size = m_hitPoints.size();
					if (size > 1) {
						//System.out.println("size > 1");
						pnt1 = m_hitPoints.get(0);
						pnt2 = m_hitPoints.get(size - 1);
						//System.out.println("size > 1: pass1 - " + pnt1.toString() + " - " + pnt2.toString());
						if (pnt1.y > pnt2.y) {
							pnt = pnt1;
							pnt1 = pnt2;
							pnt2 = pnt;
						}
						pnt1 = getLowerPoint(pnt1);
						pnt2 = getHigherPoint(pnt2);
						if (pnt1.x == pnt2.x) {
							if (decideRowAddHV())
							{
								if (pnt1.y > 0) {
									pnt1 = new Point(pnt1.x, pnt1.y - 1);
								}
								if (pnt2.y < m_fieldWidth - 1) {
									pnt2 = new Point(pnt2.x, pnt2.y + 1);
								}
								for (i = pnt1.y; i <= pnt2.y; i++) {
									pnt = new Point(pnt1.x, i);
									boolean pa = addRowShotPoint(pnt);
									pointAdded = (pointAdded || pa);
								}
								//System.out.println("size > 1 (x): pass 1 - " + pnt1.toString() + " - " + pnt2.toString() + " - Added: " + (pointAdded ? "true" : "false"));
							}
						} else if (pnt1.y == pnt2.y) {
							if (decideRowAddHV())
							{
								if (pnt1.x > 0) {
									pnt1 = new Point(pnt1.x - 1, pnt1.y);
								}
								if (pnt2.x < m_fieldWidth - 1) {
									pnt2 = new Point(pnt2.x + 1, pnt2.y);
								}
								for (i = pnt1.x; i <= pnt2.x; i++) {
									pnt = new Point(i, pnt1.y);
									boolean pa = addRowShotPoint(pnt);
									pointAdded = (pointAdded || pa);
								}
								//System.out.println("size > 1 (y): pass 2 - " + pnt1.toString() + " - " + pnt2.toString() + " - Added: " + (pointAdded ? "true" : "false"));
							}
						} else if (pnt1.y - pnt2.y == pnt1.x - pnt2.x) {
							if (decideRowAddD())
							{
								if (pnt1.x > 0 && pnt1.y > 0) {
									pnt1 = new Point(pnt1.x - 1, pnt1.y - 1);
								}
								if (pnt2.x < m_fieldWidth - 1 && pnt2.y < m_fieldWidth - 1) {
									pnt2 = new Point(pnt2.x + 1, pnt2.y + 1);
								}
								j = 0;
								for (i = pnt1.x; i <= pnt2.x; i++) {
									pnt = new Point(i, pnt1.y + j);
									boolean pa = addRowShotPoint(pnt);
									pointAdded = (pointAdded || pa);
									j++;
								}
								//System.out.println("size > 1 (diagonal): pass 1 - " + pnt1.toString() + " - " + pnt2.toString() + " - Added: " + (pointAdded ? "true" : "false"));
							}
						} else if (pnt1.x + pnt1.y == pnt2.x + pnt2.y) {
							if (decideRowAddD())
							{
								if (pnt1.x > 0 && pnt1.y < m_fieldWidth - 1) {
									pnt1 = new Point(pnt1.x - 1, pnt1.y + 1);
								}
								if (pnt2.x < m_fieldWidth - 1 && pnt2.y > 0) {
									pnt2 = new Point(pnt2.x + 1, pnt2.y - 1);
								}
								j = 0;
								for (i = pnt1.x; i <= pnt2.x; i++) {
									pnt = new Point(i, pnt1.y - j);
									boolean pa = addRowShotPoint(pnt);
									pointAdded = (pointAdded || pa);
									j++;
								}
								//System.out.println("size > 1 (diagonal): pass 2 - " + pnt1.toString() + " - " + pnt2.toString() + " - Added: " + (pointAdded ? "true" : "false"));
							}
						}
						if (!pointAdded) {
							applyFiringSolutionLogic("2");
						}
					}
				}
			} else {
				//System.out.println("Hitpoints: " + m_hitPoints.size());
				if (!sunk) {
					if (m_hitPoints.size() >= 3) {
						m_firingSolutions.clear();
					}
					applyFiringSolutionLogic("3");
				}
				//m_lastHitPoint = null;
				//m_rowShotPoints.clear();
			}
				
		}
		//System.out.println("Shot result: " + shot.x + " - " + shot.y + " - " + hit + " - " + sunk);
	}
	
	private boolean decideRowAddHV() {
		boolean seekRow = false;
		if (m_hitPoints.size() < 3 && (
								m_plEnemyScore.hasActiveShips(2) ||
								m_plEnemyScore.hasActiveShips(3) ||
								m_plEnemyScore.hasActiveShips(4) ||
								m_plEnemyScore.hasActiveShips(5) ||
								m_plEnemyScore.hasActiveShips(6)
								)
					|| m_hitPoints.size() <= 4 && (m_plEnemyScore.hasActiveShips(4) ||
								m_plEnemyScore.hasActiveShips(6)
								)
					|| m_hitPoints.size() == 5 && m_plEnemyScore.hasActiveShips(6)
		) {
			seekRow = true;
		}
		return seekRow;
	}
	
	private boolean decideRowAddD() {
		boolean seekRow = false;
		if (m_hitPoints.size() < 3 && (
								m_plEnemyScore.hasActiveShips(2) ||
								m_plEnemyScore.hasActiveShips(4) ||
								m_plEnemyScore.hasActiveShips(6)
								)
					|| m_hitPoints.size() <= 4 && (m_plEnemyScore.hasActiveShips(4) ||
								m_plEnemyScore.hasActiveShips(6)
								)
					|| m_hitPoints.size() == 5 && m_plEnemyScore.hasActiveShips(6)
		) {
			seekRow = true;
		}
		return seekRow;
	}

	private void applyFiringSolutionLogic(String debugMarker) {
		if (m_hitPoints.size() > 1 && m_hitPoints.size() <= 3) {
			if (m_plEnemyScore.hasActiveShips(3)) {
				//System.out.println("Looking for special ships (" + debugMarker + "): Submarine");
				manageFiringSolutions(3);
			} else {
				//System.out.println("Looking for special ships (" + debugMarker + "-): Battleship");
				manageFiringSolutions(5);
			}
		}
		//System.out.println("Last firing solution: " + m_lastFiringSolution.toString() + " - hitpoints: " + m_hitPoints.size() + " - m_rowShotPoints.size(): " + m_rowShotPoints.size());
		if (m_rowShotPoints.size() == 0 && m_hitPoints.size() >= 3) {
			//System.out.println("Looking for special ships (" + debugMarker + "): Battleship");
			manageFiringSolutions(5);
		}
	}
	
	private void manageFiringSolutions(int shipType) {
		if (m_firingSolutions.size() > 0) {
			getNextFiringSolution();
		} else {
			calculateFiringSolutions(shipType);
			if (m_firingSolutions.size() > 0) {
				getNextFiringSolution();
			}
		}
	}
	
	private void getNextFiringSolution() {
		ArrayList<Point> firingSolution;
		int i;
		Point pnt;
		
		m_rowShotPoints.clear();
		firingSolution = m_firingSolutions.remove(0);
		m_lastFiringSolution = firingSolution;
		for (i = 0; i < firingSolution.size(); i++) {
			pnt = firingSolution.get(i);
			addRowShotPoint(pnt);
		}
	}
	
	private void calculateFiringSolutions(int shipType) {
		int i, j, k, l;
		Point pnt;
		ArrayList<Point> surroundingFields = new ArrayList<Point>();
		ArrayList<Point> possibleHitpoints;
		boolean isSubset;
		BattleShip shipPattern = m_plTestShips.getShipPattern(shipType);
		int range;
		int maxShipPattern;
		
		if (shipType == 3) {
			range = 1;
			maxShipPattern = 4;
		} else {
			range = 2;
			maxShipPattern = 2;
		}
		for (i = 0; i < m_hitPoints.size(); i++) {
			pnt = m_hitPoints.get(i);
			fillNextShotPoints(pnt, surroundingFields, true, true, range);
			for (j = 0; j < surroundingFields.size(); j++) {
				pnt = surroundingFields.get(j);
				shipPattern.setPosition(pnt.x, pnt.y);
				for (k = 1; k <= maxShipPattern; k++) {
					shipPattern.setDirection(k);
					possibleHitpoints = shipPattern.getPossibleHitpoints();
					//System.out.println("Possible firing solution: " + possibleHitpoints.toString() + " - current hitpoints: " + m_hitPoints.toString());
					isSubset = true;
					for (l = 0; l < m_hitPoints.size(); l++) {
						pnt = m_hitPoints.get(l);
						if (!possibleHitpoints.contains(pnt)) {
							isSubset = false;
							break;
						}
						possibleHitpoints.remove(pnt);
					}
					if (isSubset) {
						for (l = 0; l < possibleHitpoints.size(); l++) {
							if (!containsShotPoint(possibleHitpoints.get(l))) {
								isSubset = false;
								break;
							}
						}
						if (isSubset) {
							if (!m_firingSolutions.contains(possibleHitpoints)) {
								m_firingSolutions.add(possibleHitpoints);
								//System.out.println("Added firing solution: " + possibleHitpoints.toString() + " - direction: " + k + " - position: " + pnt.toString() + " - current hitpoints: " + m_hitPoints.toString());
							}
						}
					}
				}
			}
			surroundingFields.clear();
		}
		
	}
	
	private boolean addRowShotPoint(Point pnt) {
		boolean pointAdded = false;

		if (containsShotPoint(pnt)) {
			m_rowShotPoints.add(pnt);
			pointAdded = true;
			//System.out.println("addRowShotPoint: " + pnt.toString());
		}
		return pointAdded;
	}
	
	private Point getHigherPoint(Point pnt) {
		int i;
		Point tmp;
		for (i = 0; i < m_hitPoints.size(); i++) {
			tmp = m_hitPoints.get(i);
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
			tmp = m_hitPoints.get(i);
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
		int index;
		
		for (y = 0; y < m_fieldWidth; y++) {
			for (x = 0; x < m_fieldWidth; x++) {
				/*index = (int)Math.round(Math.random() * (m_totalShotPoints.size() - 1));
				if (index < 0)
				index = 0;*/
				m_totalShotPoints.add(new Point(x, y));
			}
		}
	}

	private Point getNextShotPoint() {
		/* TODO:
		1. decrease probability weight of border shots to not waste shots; but reintroduce normal weight if border ship found
			- threshold 1:
			< 65 % of fields left; > 20 % of ship fields left
			- threshold 2:
			< 45 % of fields left; change from ship fields to actual ship count
		2. add starting pattern (slightly randomized, though) for first cpl of shots - not necessary
		3. when looking for 5 or 6-part rowship, check if it fits available space - nope
		4. include position of already sunk ships into next shot decision - nope
		5. towards the end (ie. only little space left for ships), add pattern search for still existing ships, include borders (from 70%/65% on?)
		6. give spaces touching existing (sunk) ships lower priority than those not touching, combine with pattern search ... ?
		7. lower priority/bias of shotpoints adjacent (even diagonal) to sunk ships
		TODO: decrease regular random border seek ...
		Goal: all ships found when 60% - 55% of fields are left
		*/
		Point pnt;
		int i, index;
		int range;
		ArrayList<Point> matrix;
		ArrayList<ArrayList<Point>> matrixList = new ArrayList<ArrayList<Point>>();
        int percent = (int)Math.round((double)m_totalShotPoints.size() / (double)(m_fieldWidth * m_fieldWidth) * 100d);
		int shipPercent = m_plEnemyScore.getActiveShipsFieldPercent();
		double randomBorderShot;
		int matrixIndex;
		
		if (percent > 89) // 89?
			range = 2;
		else
			range = 1;

		System.out.println("Ship Percentage Left: " + shipPercent);

		if (percent < 50 && shipPercent > 20 || percent < 40) {
			m_seekBorders = true;
		}
		
		for (i = 0; i < 50; i++) {
			matrix = new ArrayList<Point>();
			randomBorderShot = Math.random();
			do {
				index = (int)Math.round(Math.random() * (m_totalShotPoints.size() - 1));
				pnt = m_totalShotPoints.get(index);
			} while (!m_seekBorders && (pnt.x == 0 || pnt.y == 0 || pnt.x == m_fieldWidth - 1 || pnt.y == m_fieldWidth - 1) && randomBorderShot > 0.2d);
			if (percent > 60 || percent < 55 && percent > 49) { // > 75? < 50
				findSurrShots(pnt, matrix, range);
			} else {
				seekShotCrossing(pnt, matrix);
			}
			matrixList.add(matrix);
			//System.out.println("Point: " + pnt + " - Matrix size: " + matrix.size());
		}
		if (percent > 60 || percent < 55 && percent > 49) {
			Collections.sort(matrixList, new Comparator<ArrayList>(){
				public int compare(ArrayList a1, ArrayList a2) {
					return a1.size() - a2.size(); // ascending
				}
			});
		} else {
			Collections.sort(matrixList, new Comparator<ArrayList>(){
				public int compare(ArrayList a1, ArrayList a2) {
					return a2.size() - a1.size(); // descending
				}
			});
		}
		//matrixIndex = (int)Math.round(Math.random() * 5);
		matrixIndex = 0;
		matrix = matrixList.get(matrixIndex);
		pnt = matrix.get(0);
		//TEST
		//index = (int)Math.round(Math.random() * (m_totalShotPoints.size() - 1));
		//pnt = m_totalShotPoints.remove(index);
		m_totalShotPoints.remove(pnt);
		System.out.println("Chosen Point: " + pnt + " - Matrix size: " + matrix.size() + " - percent: " + percent);
		System.out.println("Chosen Matrix: " + matrix.toString() + " - matrixIndex: " + matrixIndex);
		return pnt;
	}

	private boolean containsShotPoint(Point pnt) {
		return m_totalShotPoints.contains(pnt);
	}

	private void fillNextShotPoints(Point shot, ArrayList<Point> nextShotPoints) {
		fillNextShotPoints(shot, nextShotPoints, false, false, 1);
	}
	
	private void fillNextShotPoints(Point shot, ArrayList<Point> nextShotPoints, boolean addSourcePoint, boolean ignoreSpentShots, int range) {
		int startX, startY, endX, endY;
		int x, y, i;
		Point pnt;
		if (shot.x - range < 0) startX = 0;
		else startX = shot.x - range;
		if (shot.x + range > m_fieldWidth - 1) endX = m_fieldWidth - 1;
		else endX = shot.x + range;
		if (shot.y - range < 0) startY = 0;
		else startY = shot.y - range;
		if (shot.y + range > m_fieldWidth - 1) endY = m_fieldWidth - 1;
		else endY = shot.y + range;
		if (addSourcePoint) {
			if (ignoreSpentShots || containsShotPoint(shot) && !nextShotPoints.contains(shot)) {
				nextShotPoints.add(shot);
			}
		}
		for (x = startX; x <= endX; x++) {
			for (y = startY; y <= endY; y++) {
				pnt = new Point(x, y);
				if (ignoreSpentShots || containsShotPoint(pnt) && !nextShotPoints.contains(pnt)) {
					nextShotPoints.add(pnt);
				}
			}
		}
	}

	private void findSurrShots(Point shot, ArrayList<Point> surrShots, int range) {
		int startX, startY, endX, endY;
		int x, y, i;
		Point pnt;
		boolean addedHitBias = false;
		if (shot.x - range < 0) startX = 0;
		else startX = shot.x - range;
		if (shot.x + range > m_fieldWidth - 1) endX = m_fieldWidth - 1;
		else endX = shot.x + range;
		if (shot.y - range < 0) startY = 0;
		else startY = shot.y - range;
		if (shot.y + range > m_fieldWidth - 1) endY = m_fieldWidth - 1;
		else endY = shot.y + range;
		surrShots.add(shot);
		for (x = startX; x <= endX; x++) {
			for (y = startY; y <= endY; y++) {
				pnt = new Point(x, y);
				if (!containsShotPoint(pnt)) {
					surrShots.add(pnt);
				}
				if (!addedHitBias && m_totalHitPoints.contains(pnt)) {
					for (i = 0; i < 8; i++) {
						surrShots.add(new Point(99, 99));
					}
					addedHitBias = true;
				}
			}
		}
	}

	private boolean checkSurrHits(Point shot, int range) {
		int startX, startY, endX, endY;
		int x, y, i;
		Point pnt;
		boolean hasSurrHits = false;
		if (shot.x - range < 0) startX = 0;
		else startX = shot.x - range;
		if (shot.x + range > m_fieldWidth - 1) endX = m_fieldWidth - 1;
		else endX = shot.x + range;
		if (shot.y - range < 0) startY = 0;
		else startY = shot.y - range;
		if (shot.y + range > m_fieldWidth - 1) endY = m_fieldWidth - 1;
		else endY = shot.y + range;
		for (x = startX; x <= endX; x++) {
			for (y = startY; y <= endY; y++) {
				pnt = new Point(x, y);
				if (m_totalHitPoints.contains(pnt)) {
					System.out.println("hasSurrHits: " + pnt.toString());
					hasSurrHits = true;
					break;
				}
			}
			if (hasSurrHits) break;
		}
		return hasSurrHits;
	}

	private void seekShotCrossing(Point shot, ArrayList<Point> crossing) {
		Point pnt;
		int i;
		int xLeft = 0;
		int xRight = 0;
		int yUp = 0;
		int yDown = 0;
		int yCount = 0;
		int xBias, yBias;
		crossing.add(shot);
		for (i = shot.x - 1; i >= 0; i--) {
			pnt = new Point(i, shot.y);
			if (containsShotPoint(pnt)) {
				crossing.add(pnt);
			} else {
				break;
			}
			xLeft++;
		}
		for (i = shot.x + 1; i < m_fieldWidth; i++) {
			pnt = new Point(i, shot.y);
			if (containsShotPoint(pnt)) {
				crossing.add(pnt);
			} else {
				break;
			}
			xRight++;
		}
		xBias = xLeft + xRight + 1 - Math.abs(xLeft - xRight);
		//System.out.println("xBias: " + xBias);
		for (i = shot.y - 1; i >= 0; i--) {
			pnt = new Point(shot.x, i);
			if (containsShotPoint(pnt)) {
				crossing.add(pnt);
			} else {
				break;
			}
			yUp++;
		}
		for (i = shot.y + 1; i < m_fieldWidth; i++) {
			pnt = new Point(shot.x, i);
			if (containsShotPoint(pnt)) {
				crossing.add(pnt);
			} else {
				break;
			}
			yDown++;
		}
		yBias = yUp + yDown + 1 - Math.abs(xLeft - xRight);
		//System.out.println("yBias: " + yBias);
		if (!checkSurrHits(shot, 1)) {
			pnt = new Point(99, 99); //padding ...
			for (i = 0; i < xBias + yBias; i++) {
				crossing.add(pnt);
			}
		}
		//System.out.println("Point: " + shot.toString() + "Crossing: " + crossing.toString());
	}
}
