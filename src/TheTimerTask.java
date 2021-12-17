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
import java.lang.reflect.*;

public class TheTimerTask extends Thread {

	private Object m_oOwner;
	private long m_lDelay;
	private long m_lPeriod;
	private boolean m_bRunning;
	private String m_sName;

	public TheTimerTask(Object oOwner, String sName) {
		m_oOwner = oOwner;
		m_bRunning = false;
		m_sName = sName;
		setPriority(Thread.MIN_PRIORITY);
		setName("BattleShips::" + sName);
	}

	public void start(long lDelay, long lPeriod) {
		m_lDelay = lDelay;
		m_lPeriod = lPeriod;
		start();
	}
	
	public void start() {
		if (!m_bRunning) {
			m_bRunning = true;
			super.start();
		}
	}
	
	public void interrupt() {
		if (m_bRunning) {
			m_bRunning = false;
			super.interrupt();
		}
	}
	
	public void run() {
		try {
			if (!m_bRunning) return;
			sleep(m_lDelay);
			Class oClass = m_oOwner.getClass();
			try {
				//**Method oMeth = oClass.getMethod(m_sName + "_TimerEvent", null);
				Method oMeth = oClass.getMethod(m_sName + "_b", new Class[]{});
				while (m_bRunning) {
					try {
						oMeth.invoke(m_oOwner, new Object[]{});
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					if (m_lPeriod > 0) {
						sleep(m_lPeriod);
					} else {
						interrupt();
					}
				}
			} catch (NoSuchMethodException e) {
				//System.out.println(m_oOwner.toString() + " has no method handleTimerEvent().\nYou must declare it as public.");
			}
		} catch(InterruptedException objExc){}
	}

}
