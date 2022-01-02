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

public class BotShot {
	private int m_priority;
	private Point m_shot;
	private ArrayList<Point> m_firingSolution;
	
	public BotShot(int x, int y) {
		m_priority = 0;
		m_shot = new Point(x, y);
	}
	
	public BotShot(int priority, Point shot) {
		m_priority = priority;
		m_shot = shot;
	}
	
	public BotShot(int priority, ArrayList<Point> firingSolution) {
		m_priority = priority;
		m_firingSolution = firingSolution;
	}
	
	public Point getShot() {
		return m_shot;
	}
	
	public int getPriority() {
		return m_priority;
	}

	public static Comparator<BotShot> PriorityAsc = new Comparator<BotShot>() {
		public int compare(BotShot b1, BotShot b2)
		{
			return b1.getPriority() - b2.getPriority();
		}
	};
	
	public static Comparator<BotShot> PriorityDesc = new Comparator<BotShot>() {
		public int compare(BotShot b1, BotShot b2)
		{
			return b2.getPriority() - b1.getPriority();
		}
	};
	
	public ArrayList<Point> getFiringSolution() {
		return m_firingSolution;
	}

	public String toString() {
		return "Priority [" + m_priority + 
		"], FiringSolution [" + (m_firingSolution == null ? "" : m_firingSolution.toString()) + 
		"], " + (m_shot == null ? "" : m_shot.toString());
	}
}
