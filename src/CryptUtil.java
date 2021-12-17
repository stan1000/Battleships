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
class CryptUtil {

	/*
	public static void main(String[] args) {
		CryptUtil oCr = new CryptUtil(args);
	}
	
	public CryptUtil(String[] args) {
		//String sTmp = encdec(args[0], 1);
		//System.out.println("Encoded: " + sTmp);
		//System.out.println("Decoded: " + encdec(sTmp, -1));
		String sTmp = args[0];
		System.out.println("Encoded: " + sTmp);
		System.out.println("Decoded: " + decode(sTmp));
	}
	// */
						   
	public String decode(String sString) {
		StringBuffer oStrTmp = new StringBuffer();
		int i;
		int iTmp;
		String sTmp;
		int iLen = 0;
		for (i = 0; i < sString.length(); i = i + iLen + 1) {
		    sTmp = String.valueOf(sString.charAt(i));
		    iLen = Integer.parseInt(sTmp);
		    sTmp = sString.substring((i + 1), (i + iLen + 1));
		    iTmp = ((((((Integer.parseInt(sTmp) ^ 68) - 1) + 11) + 10) - 7 - 4) / 22);
		    oStrTmp.append(String.valueOf((char)iTmp));
		}
		return oStrTmp.toString();
	}

	public String encode(String sString) {
		StringBuffer oStrTmp = new StringBuffer();
		int i;
		long lTmp;
		String sTmp;
		for (i = 0; i < sString.length(); i++) {
			lTmp = ((((sString.charAt(i) * 22 + 4 + 7) - 10) - 11) + 1) ^ 68;
			sTmp = Long.toString(lTmp);
		    oStrTmp.append(Integer.toString(sTmp.length()) + Long.toString(lTmp));
		}
		return oStrTmp.toString();
	}

}