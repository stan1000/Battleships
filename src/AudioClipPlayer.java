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
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import javax.sound.sampled.*;

public class AudioClipPlayer {
	
	private Hashtable<String, byte[]> m_audioClipData;
	private Hashtable<String, byte[]> m_fallbackAudioClipData;
	private Hashtable<String, ArrayList<Clip>> m_audioClips;
	private boolean m_debug;
	
	public AudioClipPlayer(boolean debug) {
		m_audioClipData = new Hashtable<String, byte[]>();
		m_fallbackAudioClipData = new Hashtable<String, byte[]>();
		m_audioClips = new Hashtable<String, ArrayList<Clip>>();
		m_debug = debug;
	}
	
	public void loadAudioClip(AudioClipPath path) {
		loadAudioClip(path.getUrl(), m_audioClipData);
		loadAudioClip(path.getFallbackUrl(), m_fallbackAudioClipData);
	}
	
	private void loadAudioClip(URL url, Hashtable<String, byte[]> clipData) {
		int fileSize;
		InputStream stream = null;
		if (url != null) {
			try {
				if (m_debug) System.out.println(url.toString());
				if (url.getProtocol().equals("jar")) {
					JarURLConnection conn = (JarURLConnection)url.openConnection();
					JarFile file = conn.getJarFile();
					JarEntry entry = conn.getJarEntry();
					fileSize = (int)entry.getSize();
					if (m_debug) System.out.println("JAR: " + entry.getName() + " - " + fileSize);
					stream = new BufferedInputStream(file.getInputStream(entry));
				} else {
					File file = new File(url.toURI());
					fileSize = (int)file.length();
					if (m_debug) System.out.println("FileSystem: " + file.getName() + " - " + fileSize);
					stream = new BufferedInputStream(new FileInputStream(file));
				}
				byte[] data = new byte[fileSize];
				stream.read(data);
				stream.close();
				clipData.put(url.toString(), data);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	public synchronized void playAudioClip(AudioClipPath path) {
		if (path.getUrl() != null || path.getFallbackUrl() != null) {
			try {
				Clip clip = getNextAvailableClip(path);
				if (clip != null) {
					AudioClipThread audioCt = new AudioClipThread(clip);
					audioCt.start();
				}
			} catch (Exception e) {
				if (m_debug) {
					System.out.println("Error: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	private Clip getNextAvailableClip(AudioClipPath path) throws UnsupportedAudioFileException, IOException {
		int i;
		Clip clip = null;
		boolean newClipList = false;
		URL url = path.getUrl();
		URL fallbackUrl = path.getFallbackUrl();
		ArrayList<Clip> clipList = m_audioClips.get(url.toString());
		boolean tryAgain = false;
		
		if (clipList == null) {
			clipList = new ArrayList<Clip>();
			newClipList = true;
		}
		for (i = 0; i < clipList.size(); i++) {
			clip = clipList.get(i);
			if (!clip.isActive()) {
				if (m_debug) System.out.println("Using old: " + clip.toString() + " (" + clipList.size() + ")");
				clip.setFramePosition(0);
				break;
			} else {
				clip = null;
			}
		}
		if (clip == null) {
			try {
				clip = createClipFromStream(url, m_audioClipData);
				clipList.add(clip);
				if (m_debug) System.out.println("Getting new: " + url.toString() + " (" + clipList.size() + ")");
				if (newClipList) {
					m_audioClips.put(url.toString(), clipList);
				}
			} catch (LineUnavailableException e) {
				tryAgain = true;
			} catch (IllegalArgumentException ea) {
				tryAgain = true;
			}
			if (tryAgain) {
				try {
					clip = createClipFromStream(fallbackUrl, m_fallbackAudioClipData);
					clipList.add(clip);
					if (m_debug) System.out.println("Getting new fallback: " + fallbackUrl.toString() + " (" + clipList.size() + ")");
					if (newClipList) {
						m_audioClips.put(url.toString(), clipList);
					}
				} catch (LineUnavailableException e) {
					if (m_debug) System.out.println("Error: " + e.getMessage());
				} catch (IllegalArgumentException ea) {
					if (m_debug) System.out.println("Error: " + ea.getMessage());
				}
			}
		}
		return clip;
	}
	
	private Clip createClipFromStream(URL url, Hashtable<String, byte[]> clipData) throws
				UnsupportedAudioFileException, LineUnavailableException, IOException
	{
		byte[] data = clipData.get(url.toString());
		InputStream stream = new ByteArrayInputStream(data);
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(stream);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);
		return clip;
	}

	public void printSupportedFormats() {
        try {
            Mixer.Info [] mixers = AudioSystem.getMixerInfo();
            for(int i = 0 ; i< mixers.length; i ++) {
                System.out.println((i+1)+". " + mixers[i].getName() + " --> " + mixers[i].getDescription() );

                Line.Info [] sourceLines = AudioSystem.getMixer(mixers[i]).getSourceLineInfo();
                System.out.println("\tSource Lines:" );
                for(int j = 0; j< sourceLines.length; j++) {
                    System.out.println("\t" + (j+1) + ". " + sourceLines[j].toString() );
                }
                System.out.println();

                Line.Info [] targetLines = AudioSystem.getMixer(mixers[i]).getTargetLineInfo();                 
                System.out.println("\tTarget Lines:" );
                for(int j = 0; j< targetLines.length; j++) {
                    System.out.println("\t" + (j+1) + ". " + targetLines[j].toString() );

                }       
                System.out.println("\n" );
            }           
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
	
}
