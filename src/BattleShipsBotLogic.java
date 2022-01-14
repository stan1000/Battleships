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
import java.util.*;

public class BattleShipsBotLogic {
		
	private int m_fieldWidth;
	private ArrayList<Point> m_totalShotPoints;
	private ArrayList<Point> m_hitPoints;
	private ArrayList<BotShot> m_surrShotPoints;
	private ArrayList<Point> m_rowShotPoints;
	private ArrayList<Point> m_totalHitPoints;
	private Point m_firstPoint;
	private Point m_lastHitPoint;
	private Point m_lastShot;
	private BattleShipsField m_plTestShips;
	private ArrayList<BotShot> m_firingSolutions;
	private ArrayList<Point> m_lastFiringSolution;
	private BattleShipsField m_plEnemyScore;
	private boolean m_seekEdges;
	private boolean m_debug;
	
	public static final int BOT_DELAY = 200;
	private static final double EDGE_SHOT_PROBABILITY = 0.2d;
		
	public BattleShipsBotLogic(int fieldWidth, boolean debug) {
		m_totalShotPoints = new ArrayList<Point>();
		m_hitPoints = new ArrayList<Point>();
		m_surrShotPoints = new ArrayList<BotShot>();
		m_rowShotPoints = new ArrayList<Point>();
		m_totalHitPoints = new ArrayList<Point>();
		m_fieldWidth = fieldWidth;
		m_debug = debug;
		//m_firstPoint = new Point(14, 3);
		m_firingSolutions = new ArrayList<BotShot>();
		m_lastFiringSolution = new ArrayList<Point>();
		m_seekEdges = false;
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
	
	public void setTestShipsPanel(BattleShipsField testShips) {
		m_plTestShips = testShips;
	}
	
	public void setEnemyScorePanel(BattleShipsField enemyScore) {
		m_plEnemyScore = enemyScore;
	}
	
	public Point getNextShot() {
		Point pnt;
		BotShot surrShot;
		int index;
		int size1 = m_rowShotPoints.size();
		int size2 = m_surrShotPoints.size();
		if (size1 > 0) {
			pnt = m_rowShotPoints.get(size1 - 1);
			m_rowShotPoints.remove(size1 - 1);
			removeSurrShotPoint(pnt);
			m_totalShotPoints.remove(pnt);
			printDebug("El count after 1: (" + pnt.toString() + ")" + m_totalShotPoints.size());
		} else if (size2 > 0) {
			pnt = getSurrShotPoint();
			m_totalShotPoints.remove(pnt);
			printDebug("El count after 2: (" + pnt.toString() + ")" + m_totalShotPoints.size());
		} else {
			if (m_firstPoint != null) {
				pnt = m_firstPoint;
				m_totalShotPoints.remove(pnt);
				m_firstPoint = null;
			} else {
				pnt = getNextShotPoint();
			}
			printDebug("El count after 4: (" + pnt.toString() + ")" + m_totalShotPoints.size());
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
			//printDebug("fieldHits.size(): " + fieldHits.size());
			for (i = 0; i < fieldHits.size(); i++) {
				pnt = fieldHits.get(i);
				//printDebug("removing point: " + pnt.toString());
				m_hitPoints.remove(pnt);
			}
			//printDebug("m_hitPoints.size(): " + m_hitPoints.size());
			/*for (i = 0; i < m_hitPoints.size(); i++) {
				pnt = m_hitPoints.get(i);
				printDebug("hit point: " + pnt.toString());
			}*/
			size = m_hitPoints.size();
			if (size > 0) {
				m_rowShotPoints.clear();
				m_surrShotPoints.clear();
				m_firingSolutions.clear();
				m_lastFiringSolution.clear();
				for (i = 0; i < size; i++) {
					pnt = m_hitPoints.get(i);
					printDebug("next shot point: " + pnt.toString());
					fillSurrShotPoints(pnt, m_surrShotPoints);
				}
			//printDebug("size > 1 (Surr full): " + m_surrShotPointsFull.size());
			}
		}
		if (sunk && m_hitPoints.size() == 0) {
			m_rowShotPoints.clear();
			m_surrShotPoints.clear();
			m_firingSolutions.clear();
			m_lastFiringSolution.clear();
			m_lastHitPoint = null;
		} else {
			//printDebug("hit: " + (hit ? "true" : "false") + " - m_lastHitPoint: " + (m_lastHitPoint != null ? m_lastHitPoint.toString() : "null"));
			if (hit || m_lastHitPoint != null) {
				m_lastShot = shot;
				if (hit) {
					if (shot.x == 0 || shot.y == 0 || shot.x == m_fieldWidth - 1 || shot.y == m_fieldWidth - 1) {
						m_seekEdges = true;
					};
					if (!sunk) {
						m_lastHitPoint = shot;
						m_hitPoints.add(shot);
						for (i = 0; i < m_hitPoints.size(); i++) {
							pnt = m_hitPoints.get(i);
							printDebug("hit point after add: " + pnt.toString());
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
						//printDebug("Looking for special ships (1-after sub): Battleship");
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
						fillSurrShotPoints(shot, m_surrShotPoints);
						printDebug("individual next shot point: " + shot.toString());
						//printDebug("Shot surr: " + m_surrShotPoints.size());
					}
					size = m_hitPoints.size();
					if (size > 1) {
						//printDebug("size > 1");
						pnt1 = m_hitPoints.get(0);
						pnt2 = m_hitPoints.get(size - 1);
						//printDebug("size > 1: pass1 - " + pnt1.toString() + " - " + pnt2.toString());
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
								//printDebug("size > 1 (x): pass 1 - " + pnt1.toString() + " - " + pnt2.toString() + " - Added: " + (pointAdded ? "true" : "false"));
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
								//printDebug("size > 1 (y): pass 2 - " + pnt1.toString() + " - " + pnt2.toString() + " - Added: " + (pointAdded ? "true" : "false"));
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
								//printDebug("size > 1 (diagonal): pass 1 - " + pnt1.toString() + " - " + pnt2.toString() + " - Added: " + (pointAdded ? "true" : "false"));
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
								//printDebug("size > 1 (diagonal): pass 2 - " + pnt1.toString() + " - " + pnt2.toString() + " - Added: " + (pointAdded ? "true" : "false"));
							}
						}
						if (!pointAdded) {
							applyFiringSolutionLogic("2");
						}
					}
				}
			} else {
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
		//printDebug("Shot result: " + shot.x + " - " + shot.y + " - " + hit + " - " + sunk);
	}

	private int getShotsLeftPercent() {
		return (int)Math.round((double)m_totalShotPoints.size() / (double)(m_fieldWidth * m_fieldWidth) * 100d);
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
				printDebug("Looking for special ships (" + debugMarker + "): Submarine");
				manageFiringSolutions(3);
			} else {
				printDebug("Looking for special ships (" + debugMarker + "-): Battleship");
				manageFiringSolutions(5);
			}
		}
		//printDebug("Last firing solution: " + m_lastFiringSolution.toString() + " - hitpoints: " + m_hitPoints.size() + " - m_rowShotPoints.size(): " + m_rowShotPoints.size());
		if (m_rowShotPoints.size() == 0 && m_hitPoints.size() >= 3) {
			printDebug("Looking for special ships (" + debugMarker + "): Battleship");
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
		BotShot botShot;
		
		m_rowShotPoints.clear();
		botShot = m_firingSolutions.remove(0);
		firingSolution = botShot.getFiringSolution();
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
		boolean touchesHit;
		int priority;
		BotShot botShot;
		
		if (shipType == 3) {
			range = 1;
			maxShipPattern = 4;
		} else {
			range = 2;
			maxShipPattern = 2;
		}
		for (i = 0; i < m_hitPoints.size(); i++) {
			pnt = m_hitPoints.get(i);
			fillNextShotPoints(pnt, surroundingFields, range);
			for (j = 0; j < surroundingFields.size(); j++) {
				pnt = surroundingFields.get(j);
				shipPattern.setPosition(pnt.x, pnt.y);
				for (k = 1; k <= maxShipPattern; k++) {
					shipPattern.setDirection(k);
					possibleHitpoints = shipPattern.getPossibleHitpoints();
					//printDebug("Possible firing solution: " + possibleHitpoints.toString() + " - current hitpoints: " + m_hitPoints.toString());
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
							if (!containsFiringSolution(possibleHitpoints)) {
								touchesHit = false;
								for (l = 0; l < possibleHitpoints.size(); l++) {
									if (checkSurrHits(possibleHitpoints.get(l), 1)) {
										touchesHit = true;
										break;
									}
								}
								if (!touchesHit) {
									priority = 1;
								} else {
									priority = 2;
								}
								botShot = new BotShot(priority, possibleHitpoints);
								m_firingSolutions.add(botShot);
								printDebug("Added firing solution: " + botShot.toString() + " - direction: " + k + " - position: " + pnt.toString() + " - current hitpoints: " + m_hitPoints.toString());
							}
						}
					}
				}
			}
			surroundingFields.clear();
		}
		Collections.sort(m_firingSolutions, BotShot.PriorityAsc);
		//m_firingSolutions.sort((o1, o2) -> Integer.compare(o1.getPriority(), o2.getPriority())); // ascending
	}
	
	private boolean containsFiringSolution(ArrayList<Point> firingSolution) {
		int i;
		BotShot botShot;
		boolean containsSolution = false;
		
		for (i = 0; i < m_firingSolutions.size(); i++) {
			botShot = m_firingSolutions.get(i);
			if (firingSolution.equals(botShot.getFiringSolution())) {
				containsSolution = true;
				break;
			}
		}
		return containsSolution;
	}
	
	private Point getFiringPatternShotPoint() {
		int i, j, k, l;
		int index = 0;
		int shipType = 0;
		Point pnt;
		ArrayList<BotShot> shotList = new ArrayList<BotShot>();
		ArrayList<Point> surroundingFields = new ArrayList<Point>();
		ArrayList<Point> possibleHitpoints;
		boolean fitsPattern;
		BattleShip shipPattern;
		int range = 1;
		int maxShipPattern;
		boolean touches;
		int priority;
		BotShot botShot;
		Point shot = null;
		
		if (m_plEnemyScore.hasActiveShips(5)) {
			shipType = 5;
		} else if (m_plEnemyScore.hasActiveShips(4)) {
			shipType = 4;
		} else if (m_plEnemyScore.hasActiveShips(3)) {
			shipType = 3;
		} else if (m_plEnemyScore.hasActiveShips(2)) {
			shipType = 2;
		} else if (m_plEnemyScore.hasActiveShips(1)) {
			shipType = 1;
		}
		if (shipType == 0) {
			return shot;
		}
		if (shipType == 5) {
			maxShipPattern = 2;
		} else {
			maxShipPattern = 4;
		}
		shipPattern = m_plTestShips.getShipPattern(shipType);
		
		for (i = 0; i < m_fieldWidth * m_fieldWidth; i++) {
			index = (int)Math.round(Math.random() * (m_totalShotPoints.size() - 1));
			pnt = m_totalShotPoints.get(index);
			findSurrShots(pnt, shotList, range, EDGE_SHOT_PROBABILITY);
		}
		Collections.sort(shotList, BotShot.PriorityAsc);
		
		for (i = 0; i < m_fieldWidth * m_fieldWidth / 2; i++) {
			pnt = shotList.get(i).getShot();
			fillNextShotPoints(pnt, surroundingFields, range);
			for (j = 0; j < surroundingFields.size(); j++) {
				pnt = surroundingFields.get(j);
				shipPattern.setPosition(pnt.x, pnt.y);
				for (k = 1; k <= maxShipPattern; k++) {
					shipPattern.setDirection(k);
					possibleHitpoints = shipPattern.getPossibleHitpoints();
					fitsPattern = true;
					for (l = 0; l < possibleHitpoints.size(); l++) {
						pnt = possibleHitpoints.get(l);
						if (!m_totalShotPoints.contains(pnt)) { 
							fitsPattern = false;
							break;
						}
					}
					if (fitsPattern) {
						touches = false;
						for (l = 0; l < possibleHitpoints.size(); l++) {
							pnt = possibleHitpoints.get(l);
							if (checkSurrHits(pnt, 1) || !m_seekEdges && (pnt.x == 0 || pnt.y == 0 || pnt.x == m_fieldWidth - 1 || pnt.y == m_fieldWidth - 1)) {
								touches = true;
								break;
							}
						}
						if (!touches) {
							switch (shipType) {
								case 1:
									index = (int)Math.round(Math.random() * (possibleHitpoints.size() - 1));
									break;
								case 2:
									index = 1;
									break;
								case 3:
									switch (k) {
										case 1:
										case 2:
											index = 1;
											break;
										case 3:
										case 4:
											index = 2;
											break;
									}
									break;
								case 4:
								case 6:
									index = 2;
									break;
								case 5:
									if (k == 1) {
										index = 3;
									} else {
										index = 4;
									}
									break;
							}
							shot = possibleHitpoints.get(index);
							printDebug("Firing Pattern Point: " + shot.toString() + " - shipType: " + shipType + " - found after: " + (i * j) + " - index: " + index + " - direction: " + k + " - pattern: " + possibleHitpoints.toString());
							return shot;
						}
					}
				}
			}
			surroundingFields.clear();
		}
		return shot;
	}
	
	private boolean addRowShotPoint(Point pnt) {
		boolean pointAdded = false;

		if (containsShotPoint(pnt)) {
			m_rowShotPoints.add(pnt);
			pointAdded = true;
			//printDebug("addRowShotPoint: " + pnt.toString());
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
				m_totalShotPoints.add(new Point(x, y));
			}
		}
	}

	private Point getNextShotPoint() {
		/* TODO:
		1. decrease probability weight of edge shots to not waste shots; but reintroduce normal weight if edge ship found
			- threshold 1:
			< 65 % of fields left; > 20 % of ship fields left
			- threshold 2:
			< 45 % of fields left; change from ship fields to actual ship count
		2. add starting pattern (slightly randomized, though) for first cpl of shots - not necessary
		3. when looking for 5 or 6-part rowship, check if it fits available space - nope
		4. include position of already sunk ships into next shot decision - nope
		5. towards the end (ie. only little space left for ships), add pattern search for still existing ships, include edges (from 70%/65% on?)
		6. give spaces touching existing (sunk) ships lower priority than those not touching - done
		7. lower priority/bias of shotpoints adjacent (even diagonal) to sunk ships (see 6.) - done
		8. implement pattern search to the end of game (from 65 - 60%?) (see 5.) - done
			- @65%: check for Battleships
			- @60%: check for Subs, Destroyers
			- @55%: (earlier, if no other ships left?) check for Frigate + Minesweeper: top 10 best points, 
				check for those where either fits in all different positions/directions/shifted,
				prioritize those with no adjacent sunk ships?
			- look for areas with the least hitpoints (vs. shotpoints like in the other logic), get those points and do the checks - nope
			- ignore points adjacent to sunk ships - done
		9. add priority to firing solutions (lower p. for those touching sunk ships) - done
		10. every now and then, place a ship directly adjacent to another one ...
		TODO: decrease regular random edge seek ... - done
		TODO: when priotizing surr. points, ignore content of m_hitPoints ... - done
		TODO: check bug with totalhitpoints vs. fieldhits, when looking for firing solutions ... none found ... ?
		TODO: 1. use middle point of firing pattern - done
			  2. consider m_seekEdges when looking for pattern - done
		
		Goal: all ships found when 60% - 55% of fields are left
		*/
		Point pnt = null;
		int i, index;
		int range;
		ArrayList<BotShot> shotList = new ArrayList<BotShot>();
        int percent = getShotsLeftPercent();
		int shipPercent = m_plEnemyScore.getActiveShipsFieldPercent();
		double randomEdgeShot;
		BotShot botShot;
		
		if (percent > 89) // 89?
			range = 2;
		else
			range = 1;

		printDebug("Ship Percentage Left: " + shipPercent);

		if (percent < 50 && shipPercent > 20 || percent < 40) {
			m_seekEdges = true;
		}
		
		if (percent < 66) {
			pnt = getFiringPatternShotPoint();
			if (pnt == null) {
				m_seekEdges = true;
			}
		}

		if (pnt == null) {
			randomEdgeShot = Math.random();
			for (i = 0; i < 100; i++) {
				index = (int)Math.round(Math.random() * (m_totalShotPoints.size() - 1));
				pnt = m_totalShotPoints.get(index);
				if (percent > 63 || percent < 53) { // > 75? < 50
					findSurrShots(pnt, shotList, range, randomEdgeShot);
				} else {
					seekShotCrossing(pnt, shotList, randomEdgeShot);
				}
				//printDebug("Point: " + pnt + " - Matrix size: " + matrix.size());
			}
			if (percent > 63 || percent < 53) {
				Collections.sort(shotList, BotShot.PriorityAsc);
				//shotList.sort((o1, o2) -> Integer.compare(o1.getPriority(), o2.getPriority())); // ascending		
			} else {
				Collections.sort(shotList, BotShot.PriorityDesc);
				//shotList.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority())); // descending		
			}
			botShot = shotList.get(0);
			pnt = botShot.getShot();
		}
		printDebug("Chosen Shot: " + pnt.toString() + " - percent: " + percent);
		m_totalShotPoints.remove(pnt);
		//printDebug("Shotlist: " + shotList.toString());
		return pnt;
	}

	private Point getSurrShotPoint() {
		ArrayList<BotShot> subList = new ArrayList<BotShot>();
		BotShot surrShot;
		int index;
		int i;
		
		for (i = 0; i < m_surrShotPoints.size(); i++) {
			surrShot = m_surrShotPoints.get(i);
			if (surrShot.getPriority() == 1) {
				subList.add(surrShot);
			}
		}
		//subList = (ArrayList<BotShot>)m_surrShotPoints.stream().filter(p -> p.getPriority() == 1).collect(Collectors.toList());
		if (subList.size() == 0) {
			subList = m_surrShotPoints;
		}
		//printDebug("subList: " + subList.toString());
		index = (int)Math.round(Math.random() * (subList.size() - 1));
		surrShot = subList.get(index);
		m_surrShotPoints.remove(surrShot);
		return surrShot.getShot();
	}
	
	private boolean containsShotPoint(Point pnt) {
		return m_totalShotPoints.contains(pnt);
	}

	private void removeSurrShotPoint(Point pnt) {
		int i;
		BotShot botShot;
		
		for (i = 0; i < m_surrShotPoints.size(); i++) {
			botShot = m_surrShotPoints.get(i);
			if (pnt.equals(botShot.getShot())) {
				m_surrShotPoints.remove(i);
				//printDebug("Found and removing surr. point: " + botShot.toString());
				break;
			}
		}
	}
	
	private boolean containsSurrShotPoint(Point pnt) {
		int i;
		BotShot botShot;
		boolean containsPoint = false;
		
		for (i = 0; i < m_surrShotPoints.size(); i++) {
			botShot = m_surrShotPoints.get(i);
			if (pnt.equals(botShot.getShot())) {
				containsPoint = true;
				break;
			}
		}
		return containsPoint;
	}
	
	private void fillSurrShotPoints(Point shot, ArrayList<BotShot> surrShotPoints) {
		int x, y, i;
		int priority;
		Point pnt;
		int range = 1;
		BotShot nextShot;
		SeekBounds bounds = new SeekBounds(shot, range, m_fieldWidth);
		
		for (x = bounds.getStartX(); x <= bounds.getEndX(); x++) {
			for (y = bounds.getStartY(); y <= bounds.getEndY(); y++) {
				pnt = new Point(x, y);
				if (containsShotPoint(pnt) && !containsSurrShotPoint(pnt)) {
					if (!checkSurrHits(pnt, 1)) {
						priority = 1;
					} else {
						priority = 2;
					}
					nextShot = new BotShot(priority, pnt);
					surrShotPoints.add(nextShot);
				}
			}
		}
		//printDebug("surrShotPoints: " + surrShotPoints.toString());
	}

	private void fillNextShotPoints(Point shot, ArrayList<Point> nextShotPoints, int range) {
		int x, y, i;
		Point pnt;
		SeekBounds bounds = new SeekBounds(shot, range, m_fieldWidth);
		
		nextShotPoints.add(shot);
		for (x = bounds.getStartX(); x <= bounds.getEndX(); x++) {
			for (y = bounds.getStartY(); y <= bounds.getEndY(); y++) {
				pnt = new Point(x, y);
				nextShotPoints.add(pnt);
			}
		}
	}

	private void findSurrShots(Point shot, ArrayList<BotShot> surrShots, int range, double randomEdgeShot) {
		int startX, startY, endX, endY;
		int x, y, i;
		int priority = 1;
		Point pnt;
		boolean addedHitBias = false;
		SeekBounds bounds = new SeekBounds(shot, range, m_fieldWidth);
		
		for (x = bounds.getStartX(); x <= bounds.getEndX(); x++) {
			for (y = bounds.getStartY(); y <= bounds.getEndY(); y++) {
				pnt = new Point(x, y);
				if (!containsShotPoint(pnt)) {
					priority++;
				}
				if (!addedHitBias && m_totalHitPoints.contains(pnt)) {
					priority += 8;
					addedHitBias = true;
				}
			}
		}
		if (!m_seekEdges && randomEdgeShot > EDGE_SHOT_PROBABILITY && (shot.x == 0 || shot.y == 0 || shot.x == m_fieldWidth - 1 || shot.y == m_fieldWidth - 1)) {
			priority += 4;
		}
		surrShots.add(new BotShot(priority, shot));
	}

	private boolean checkSurrHits(Point shot, int range) {
		int startX, startY, endX, endY;
		int x, y, i;
		Point pnt;
		boolean surrHits = false;
		SeekBounds bounds = new SeekBounds(shot, range, m_fieldWidth);
		
		for (x = bounds.getStartX(); x <= bounds.getEndX(); x++) {
			for (y = bounds.getStartY(); y <= bounds.getEndY(); y++) {
				pnt = new Point(x, y);
				if (m_totalHitPoints.contains(pnt) && !m_hitPoints.contains(pnt)) {
					//printDebug("hasSurrHits: " + pnt.toString());
					surrHits = true;
					break;
				}
			}
		}
		return surrHits;
	}

	private void seekShotCrossing(Point shot, ArrayList<BotShot> crossing, double randomEdgeShot) {
		Point pnt;
		int i;
		int xLeft = 0;
		int xRight = 0;
		int yUp = 0;
		int yDown = 0;
		int yCount = 0;
		int xBias, yBias;
		int priority = 1;

		for (i = shot.x - 1; i >= 0; i--) {
			pnt = new Point(i, shot.y);
			if (containsShotPoint(pnt)) {
				xLeft++;
			} else {
				break;
			}
		}
		priority += xLeft - 1;
		for (i = shot.x + 1; i < m_fieldWidth; i++) {
			pnt = new Point(i, shot.y);
			if (containsShotPoint(pnt)) {
				xRight++;
			} else {
				break;
			}
		}
		priority += xRight - 1;
		xBias = xLeft + xRight + 1 - Math.abs(xLeft - xRight);
		//printDebug("xBias: " + xBias);
		for (i = shot.y - 1; i >= 0; i--) {
			pnt = new Point(shot.x, i);
			if (containsShotPoint(pnt)) {
				yUp++;
			} else {
				break;
			}
		}
		priority += yUp - 1;
		for (i = shot.y + 1; i < m_fieldWidth; i++) {
			pnt = new Point(shot.x, i);
			if (containsShotPoint(pnt)) {
				yDown++;
			} else {
				break;
			}
		}
		priority += yDown - 1;
		yBias = yUp + yDown + 1 - Math.abs(xLeft - xRight);
		//printDebug("yBias: " + yBias);
		if (checkSurrHits(shot, 1)) {
			priority = 1;
		}
		crossing.add(new BotShot(priority, shot));
		//printDebug("Point: " + shot.toString() + "Crossing: " + crossing.toString());
	}
	
	private void printDebug(String output) {
		if (m_debug) {
			System.out.println(output);
		}
	}
	
	private class SeekBounds {
		private int m_startX;
		private int m_startY;
		private int m_endX;
		private int m_endY;
		
		public SeekBounds(Point shot, int range, int fieldWidth) {
			if (shot.x - range < 0) m_startX = 0;
			else m_startX = shot.x - range;
			if (shot.x + range > fieldWidth - 1) m_endX = fieldWidth - 1;
			else m_endX = shot.x + range;
			if (shot.y - range < 0) m_startY = 0;
			else m_startY = shot.y - range;
			if (shot.y + range > fieldWidth - 1) m_endY = fieldWidth - 1;
			else m_endY = shot.y + range;
		}
		
		public int getStartX() {
			return m_startX;
		}
		
		public int getStartY() {
			return m_startY;
		}
		
		public int getEndX() {
			return m_endX;
		}
		
		public int getEndY() {
			return m_endY;
		}
		
	}
}
