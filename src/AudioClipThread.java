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
import java.net.*;
import javax.sound.sampled.*;

public class AudioClipThread extends Thread {
	
	private Clip m_clip;
	private boolean m_running;
	
	public AudioClipThread(Clip clip) {
		m_clip = clip;
	}

	public void start() {
		if (!m_running) {
			m_running = true;
			super.start();
		}
	}
	
	public void run() {
		m_clip.start();
		while (m_running) {
			if (!m_clip.isActive()) {
				interrupt();
			}
			try {
				sleep(100);
			} catch (InterruptedException e) {
				interrupt();
				break;
			}
		}
	}

	public void interrupt() {
		if (m_running) {
			super.interrupt();
			m_running = false;
		}
	}

	
}