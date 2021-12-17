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
public class StringTool {

	public static String[] split(String sString) {
		return split(sString, ",");
	}

	public static String[] split(String sString, String sDelimiter) {
		String a_sBuffer[] = new String[10000]; // temporary buffer
		int iPos = -1;
		int iPosBefore = 0;
		int iCnt = 0;
		String sTmp = "";
		do {
			iPosBefore = iPos;
			iPos = sString.indexOf(sDelimiter, iPos + 1);
			if (iPos > -1) {
				sTmp = sString.substring(iPosBefore + 1, iPos);
			} else {
				sTmp = sString.substring(iPosBefore + 1);
			}
			a_sBuffer[iCnt] = sTmp;
			iCnt += 1;
		} while (iPos > -1);
		String a_sData[] = new String[iCnt];
		for (int i = 0; i < iCnt; i++) {
			a_sData[i] = a_sBuffer[i];
		}
		return a_sData;
	}

	public static String replace(String sString, String sOldString, String sNewString) {
		int iStartPos = -1;
		int iStartPosBefore = 0;
		int iReadPos = 0;
		int iFinalPos = 0;
		int iCnt = 0;
		int iLenOldString = sOldString.length();
		String sTmp = "";
		if (sString.indexOf(sOldString) == -1) {
			sTmp = sString;
		} else {
			do {
				iStartPos = sString.indexOf(sOldString, iStartPos + 1);
				if (iStartPos > -1) {
					if (iStartPos > 0) {
						if (iCnt > 0) {
							iReadPos = iStartPosBefore + iLenOldString;
						} else {
							iReadPos = 0;
						}
					} else {
						iReadPos = 0;
						if (iCnt > 0) {
							sTmp += sNewString;
						}
					}
					if (iStartPos < sString.length()) sTmp += sString.substring(iReadPos, iStartPos) + sNewString;
				}
				iFinalPos = iStartPosBefore;
				iStartPosBefore = iStartPos;
				iCnt++;
			} while (iStartPos > -1);
			if (iCnt == 1) iLenOldString = 0;
			if (sString.length() > iFinalPos + iLenOldString) {
				sTmp += sString.substring(iFinalPos + iLenOldString);
			}
		}
		return sTmp;
	}
	
}
