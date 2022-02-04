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
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class BattleShipsPanel extends Container implements BattleShipsConnectionListener {
	
	private TextDisplayPanel m_oCntMain;
	private TextDisplayPanel m_oLblMyShips;
	private TextDisplayPanel m_oLblEnemyShips;
	private BattleShipsField m_oPlMyShips;
	private BattleShipsField m_oPlEnemyShips;
	private BattleShipsField m_oPlMyScore;
	private BattleShipsField m_oPlEnemyScore;
	private BattleShipsField m_oPlInfo;
	private BattleShipsField m_plTestShips;
	private TextDisplayPanel m_oTxtStatus;
	private TextField m_oTxtChatInput;
	private TextArea m_oTxtChatOutput;
	private GfxAnimatedButton m_oBtnReady;
	private GfxAnimatedButton m_oBtnToggleHideShips;
	private TextDisplayPanel m_oLblStatus;
	private TextDisplayPanel m_oLblChat;
	private boolean m_bPlaying;
	private Container m_oPnlConnect;
	private Container m_oPnlClient;
	private GfxAnimatedButton m_oBtnToggleConnect;
	private TextField m_oTxtServer;
	private TextField m_txtPort;
	private GfxAnimatedButton m_oBtnToggleServer;
	private int m_mode;
	private BattleShipsConnection m_oSchSocket;
	private boolean m_bServerRunning;
	private Checkbox m_oCbClient;
	private Checkbox m_oCbServer;
	private Checkbox m_cbComputer;
	private boolean m_bConnected;
	private boolean m_bMeReady;
	private boolean m_bEnemyReady;
	private boolean m_bMyTurn;
	private int m_iCellWidth;
	private int m_iFieldWidth;
	private int m_iServerPort;
	private boolean m_bShipsHidden;
	private AudioClip m_oAuGameStart;
	private AudioClip m_oAuShipHit;
	private AudioClip m_oAuShipSink;
	private AudioClip m_oAuGameWon;
	private AudioClip m_oAuGameLost;
	private AudioClip m_oAuConnect;
	private AudioClip m_oAuGameOver;
	private Image m_ImgOffScreenBuffer;
	private boolean m_bPlaySound;
	private TheTimerTask m_oTmrGameOver;
	private String m_sOriginalConfig;
	private String m_sCurrentConfig;
	private int m_iShipTypeCount[];
	private StateDisplayPanel m_oCntState;
	private boolean m_bClientOnly;
	private boolean m_bConfigReady;
	private Label m_oLblScore;
	private Point m_oPntScore;
	private GfxButton m_oGfxBtnInfo;
	private GfxToggleButton m_oBtnToggleSound;
	private Color m_oColShip;
	private Color m_oColShipSunk;
	private CryptUtil m_oCryptUtil;
	private boolean m_bUseWebServer;
	private int m_iWebServerPort;
	private boolean m_bWebServerLog;
	private WebServer m_oWebServer;
	private Checkbox m_oCbRestoreWindow;
	private FontMetrics m_oFntMetrMain;
	private BattleShipsParentContainer m_oBspcParent;
	private boolean m_bHaveServerError;
	private TextDisplayPanel m_lblHost;
	private TextDisplayPanel m_lblPort;
	private TextDisplayPanel m_lblPlayerName;
	private Container m_pnlPlayerName;
	private TextField m_txtPlayerName;
	private GfxAnimatedButton m_btnSetPlayerName;
	private String m_playerName; 
	private TextDisplayPanel m_cntSelectEnemy;
	private java.awt.List m_lstPlayerName;
	private boolean m_connWithDedicatedServer;
	private boolean m_showSelectEnemy;
	private String m_enemyPlayerName;
	private GfxButton m_btnDisconnectEnemy;
	private TextDisplayPanel m_cntConfirmation;
	private TextDisplayPanel m_lblConfirmation;
	private TextDisplayPanel m_cntModalMask;
	private boolean m_connectToFirstPlayer;
	private boolean m_isBot;
	private BattleShipsBotLogic m_Ai;
	private TheTimerTask m_tmrTimeOut;
	private boolean m_gameOver;
	private boolean m_botPaused;
	private boolean m_autoBot;
	private boolean m_debug;
	private int m_timeOutSeconds;
	private boolean m_startBot;
	private boolean m_startServer;
	private BotThread m_botThread;
	
	private final int HORIZ_BORDER_PADDING = 15;
	private final int VERT_BORDER_PADDING = 8;
	private final int DEFAULT_CELL_WIDTH = 22;
	private final int DEFAULT_FIELD_WIDTH = 18;
	private final int MIN_FIELD_SIZE = 340;
	
	public BattleShipsPanel(boolean bClientOnly, boolean debug) {
		m_bClientOnly = bClientOnly;
		m_debug = debug;
		m_autoBot = false;
		m_startBot = false;
		m_startServer = false;
	}
	
	public void init() {
		Color oColField;
		Color oColFieldLines;
		Color oColShipHitMark;
		Color oColShipHighlight;
		Color oColFieldShotMark;
		Color oColFieldShotMarkEnemy;
		Color oColFieldLastShotMark;
		int iShipType;
		int iShipTypeCount;
		Font oFntMainFont;
		CheckboxGroup oCbgConnect;
		boolean bRetTmp;
		boolean bRestoreWindow;
		Image oImgBtnOn;
		Image oImgBtnOff;
		Cursor oShootCursor = null;
		boolean bUseLocalConfig;
		Font fntMainFontBold;
		GfxAnimatedButton btnSelectEnemy;
		GfxAnimatedButton btnOk;
		GfxAnimatedButton btnCancel;
		int xLoc;
		int port;
		
		setLayout(null);
		setBackground(Color.white);
		m_bPlaying = false;
		m_mode = BattleShipsUtility.MODE_CLIENT;
		m_bServerRunning = false;
		m_bConnected = false;
		m_bMeReady = false;
		m_bEnemyReady = false;
		m_bMyTurn = false;
		m_bShipsHidden = false;
		m_iShipTypeCount = new int[BattleShipsUtility.MAX_SHIP_TYPE];
		m_bConfigReady = false;
		m_oBspcParent = (BattleShipsParentContainer)getParent();
		m_playerName = "";
		m_connWithDedicatedServer = false;
		m_showSelectEnemy = false;
		m_enemyPlayerName = "";
		m_connectToFirstPlayer = false;
		m_gameOver = true;
		m_botPaused = true;
		m_timeOutSeconds = 0;

		// get external config parms
		m_iCellWidth = parseIntParm("CellWidth", DEFAULT_CELL_WIDTH);
		m_iFieldWidth = parseIntParm("FieldWidth", DEFAULT_FIELD_WIDTH);
		port = parseIntParm("Port", 666, true);
		if (port < 1 || port > 65535) port = 666;
		m_iServerPort = parseIntParm("ServerPort", 666);
		if (m_iServerPort < 1 || m_iServerPort > 65535) m_iServerPort = 666;
		m_bPlaySound = (parseIntParm("PlaySound", 0, true) == 1 ? true : false);
		bRestoreWindow = (parseIntParm("RestoreWindow", 0, true) == 1 ? true : false);
		bUseLocalConfig = (parseIntParm("UseLocalConfig", 0) == 1 ? true : false);
		oColField = parseColorParm("ColorPlayField", Color.white);
		oColFieldLines = parseColorParm("ColorPlayFieldLines", Color.black);
		m_oColShip = parseColorParm("ColorShip", Color.red);
		m_oColShipSunk = parseColorParm("ColorShipSunk", Color.magenta);
		oColShipHitMark = parseColorParm("ColorShipHitMark", Color.black);
		oColShipHighlight = parseColorParm("ColorShipHighlight", Color.blue);
		oColFieldShotMark = parseColorParm("ColorPlayFieldShotMark", Color.lightGray);
		oColFieldShotMarkEnemy = parseColorParm("ColorPlayFieldShotMarkEnemy", Color.lightGray);
		oColFieldLastShotMark = parseColorParm("ColorPlayFieldLastShotMark", Color.gray);
		if (m_bClientOnly && !bUseLocalConfig) {
			m_sOriginalConfig = "";
		} else {
			m_sOriginalConfig = m_iCellWidth + ";" + m_iFieldWidth;
			for (iShipType = 1; iShipType <= BattleShipsUtility.MAX_SHIP_TYPE; iShipType++) {
				iShipTypeCount = parseIntParm("ShipTypeCount" + iShipType, 1);
				m_iShipTypeCount[iShipType - 1] = iShipTypeCount;
				m_sOriginalConfig += ";" + iShipTypeCount;
			}
		}
		m_sCurrentConfig = m_sOriginalConfig;
		m_bUseWebServer = !m_bClientOnly && (parseIntParm("UseWebServer", 0) == 1 ? true : false);
		m_bWebServerLog = (parseIntParm("WebServerLog", 0) == 1 ? true : false);
		m_iWebServerPort = parseIntParm("WebServerPort", 80);
		if (m_iWebServerPort < 1 || m_iWebServerPort > 65535 || m_iWebServerPort == m_iServerPort) m_iWebServerPort = 80;
		
		oFntMainFont = new Font("SansSerif", Font.PLAIN, 14);
		fntMainFontBold = new Font("SansSerif", Font.BOLD, 14);
			
		oImgBtnOn = getImage("res/bg_btn_on.gif");
		oImgBtnOff = getImage("res/bg_btn_off.gif");
		
		m_oCntMain = (TextDisplayPanel)add(new TextDisplayPanel());
		m_oCntMain.setFont(oFntMainFont);
		m_oFntMetrMain = m_oCntMain.getFontMetrics(oFntMainFont);

		m_oPlInfo = (BattleShipsField)m_oCntMain.add(new BattleShipsField(BattleShipsUtility.MAX_SHIP_TYPE, BattleShipsField.FIELD_TYPE_SCORE));
		m_oPlInfo.setBackground(Color.white);
		m_oPlInfo.setForeground(Color.black);
		m_oPlInfo.setColorShip(m_oColShip);
		m_oPlInfo.setVisible(false);

		m_cntSelectEnemy = (TextDisplayPanel)m_oCntMain.add(new TextDisplayPanel());
		m_cntSelectEnemy.setForeground(Color.black);
		m_cntSelectEnemy.setBackground(Color.white);
		m_cntSelectEnemy.setPadding(2);
		m_cntSelectEnemy.setWithBorder(true);
		m_cntSelectEnemy.setSize(220, 250);
		
		TextDisplayPanel pnlSelect = (TextDisplayPanel)m_cntSelectEnemy.add(new TextDisplayPanel());
		pnlSelect.setLocation(3, 3);
		pnlSelect.setFont(fntMainFontBold);
		pnlSelect.setText(getString("SelectEnemy"), TextDisplayPanel.AUTO_RESIZE);
		
		m_lstPlayerName = (java.awt.List)m_cntSelectEnemy.add(new java.awt.List(10));
		m_lstPlayerName.setFont(oFntMainFont);
		m_lstPlayerName.addMouseListener(new TheMouseAdapter((Object)this, "selE"));
		m_lstPlayerName.setBounds(5, m_oFntMetrMain.getHeight() + 5, m_cntSelectEnemy.getSize().width - 10, 180);

		btnSelectEnemy = (GfxAnimatedButton)m_cntSelectEnemy.add(new GfxAnimatedButton((Object)this, "se", oImgBtnOn, oImgBtnOff));
		btnSelectEnemy.setFont(fntMainFontBold);
		btnSelectEnemy.setLabel(getString("Play"));

		xLoc = m_cntSelectEnemy.getSize().width / 2 - btnSelectEnemy.getSize().width / 2;
		btnSelectEnemy.setLocation(xLoc, m_oFntMetrMain.getHeight() + 5 + m_lstPlayerName.getSize().height + 8);

		m_cntSelectEnemy.setVisible(false);

		m_cntConfirmation = (TextDisplayPanel)m_oCntMain.add(new TextDisplayPanel());
		m_cntConfirmation.setForeground(Color.black);
		m_cntConfirmation.setBackground(Color.white);
		m_cntConfirmation.setWithBorder(true);
		m_cntConfirmation.setSize(220, 145);
		m_cntConfirmation.addMouseListener(new TheMouseAdapter((Object)this, "dummy"));

		m_lblConfirmation = (TextDisplayPanel)m_cntConfirmation.add(new TextDisplayPanel());
		m_lblConfirmation.setLocation(0, 0);
		m_lblConfirmation.setPadding(5);
		m_lblConfirmation.setFont(oFntMainFont);
		m_lblConfirmation.setSize(m_cntConfirmation.getSize().width, 100);
		
		btnOk = (GfxAnimatedButton)m_cntConfirmation.add(new GfxAnimatedButton((Object)this, "ok", oImgBtnOn, oImgBtnOff));
		btnOk.setFont(oFntMainFont);
		btnOk.setLabel(getString("Ok"));
		btnOk.setSize(m_oFntMetrMain.stringWidth(btnOk.getLabel()) + 15, m_oFntMetrMain.getHeight() + 8);
		
		btnCancel = (GfxAnimatedButton)m_cntConfirmation.add(new GfxAnimatedButton((Object)this, "cancel", oImgBtnOn, oImgBtnOff));
		btnCancel.setFont(oFntMainFont);
		btnCancel.setLabel(getString("Cancel"));
		btnCancel.setSize(m_oFntMetrMain.stringWidth(btnCancel.getLabel()) + 15, m_oFntMetrMain.getHeight() + 8);

		xLoc = m_cntConfirmation.getSize().width / 2 - (btnOk.getSize().width + 10 + btnCancel.getSize().width) / 2;
		btnOk.setLocation(xLoc, m_lblConfirmation.getSize().height + 10);
		btnCancel.setLocation(xLoc + btnOk.getSize().width + 10, m_lblConfirmation.getSize().height + 10);
		
		m_cntConfirmation.setVisible(false);
		
		m_cntModalMask = (TextDisplayPanel)m_oCntMain.add(new TextDisplayPanel());
		m_cntModalMask.addMouseListener(new TheMouseAdapter((Object)this, "dummy"));
		m_cntModalMask.setVisible(false);
		
		m_oCntState = (StateDisplayPanel)m_oCntMain.add(new StateDisplayPanel());
		m_oCntState.setBackground(Color.white);
		m_oCntState.setBounds(1, 1, 1, 1);
		m_oCntState.setVisible(false);

		m_oLblMyShips = (TextDisplayPanel)m_oCntMain.add(new TextDisplayPanel());
		m_oLblMyShips.setFont(oFntMainFont);
		m_oLblMyShips.setText(getString("MyShips"), TextDisplayPanel.AUTO_RESIZE);
		
		m_oGfxBtnInfo = (GfxButton)m_oCntMain.add(new GfxButton((Object)this, "j", getImage("res/info.gif")));
		m_oGfxBtnInfo.setBounds(1, 1);
			
		m_oPlMyShips = (BattleShipsField)m_oCntMain.add(new BattleShipsField(BattleShipsUtility.MAX_SHIP_TYPE, BattleShipsField.FIELD_TYPE_ME));
		m_oPlMyShips.setLocation(HORIZ_BORDER_PADDING, VERT_BORDER_PADDING + 19);
		m_oPlMyShips.setBackground(oColField);
		m_oPlMyShips.setForeground(oColFieldLines);
		m_oPlMyShips.setColorShotMark(oColFieldShotMark);
		m_oPlMyShips.setColorLastShotMark(oColFieldLastShotMark);
		m_oPlMyShips.setColorShip(m_oColShip);
		m_oPlMyShips.setColorShipSunk(m_oColShipSunk);
		m_oPlMyShips.setColorShipHitMark(oColShipHitMark);
		m_oPlMyShips.setColorShipHighlight(oColShipHighlight);
		m_oPlMyShips.setDebug(m_debug);
		
		m_oPlMyScore = (BattleShipsField)m_oCntMain.add(new BattleShipsField(BattleShipsUtility.MAX_SHIP_TYPE, BattleShipsField.FIELD_TYPE_SCORE));
		m_oPlMyScore.setBackground(oColField);
		m_oPlMyScore.setForeground(oColFieldLines);
		m_oPlMyScore.setColorShip(m_oColShip);
		m_oPlMyScore.setColorShipSunk(m_oColShipSunk);
		m_oPlMyScore.init(4, 68);
		
		m_oLblEnemyShips = (TextDisplayPanel)m_oCntMain.add(new TextDisplayPanel());
		m_oLblEnemyShips.setFont(oFntMainFont);
		m_oLblEnemyShips.setText(getString("EnemyShips"), TextDisplayPanel.AUTO_RESIZE);

		//**m_oBtnToggleSound = (GfxToggleButton)m_oCntMain.add(new GfxToggleButton((Object)this, "BtnToggleSound", getImage("res/speaker_on.gif"), getImage("res/speaker_off.gif")));
		m_oBtnToggleSound = (GfxToggleButton)m_oCntMain.add(new GfxToggleButton((Object)this, "k", getImage("res/speaker_on.gif"), getImage("res/speaker_off.gif")));
		m_oBtnToggleSound.setBounds(1, 1);
		m_oBtnToggleSound.setOn(m_bPlaySound);
		
		try {
			oShootCursor = Graphics2DFactory.getShootCursor(getImage("res/shoot_cursor.gif"));
		} catch (Throwable t) {}
		if (oShootCursor == null) {
			oShootCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
		}
		m_oPlEnemyShips = (BattleShipsField)m_oCntMain.add(new BattleShipsField(BattleShipsUtility.MAX_SHIP_TYPE, BattleShipsField.FIELD_TYPE_ENEMY));
		m_oPlEnemyShips.setBackground(oColField);
		m_oPlEnemyShips.setForeground(oColFieldLines);
		m_oPlEnemyShips.setColorShotMark(oColFieldShotMarkEnemy);
		m_oPlEnemyShips.setColorLastShotMark(oColFieldLastShotMark);
		m_oPlEnemyShips.setColorShip(m_oColShip);
		m_oPlEnemyShips.setColorShipSunk(m_oColShipSunk);
		m_oPlEnemyShips.setColorShipHitMark(oColShipHitMark);
		m_oPlEnemyShips.setShootCursor(oShootCursor);
		m_oPlEnemyShips.setDebug(m_debug);

		m_oPlEnemyScore = (BattleShipsField)m_oCntMain.add(new BattleShipsField(BattleShipsUtility.MAX_SHIP_TYPE, BattleShipsField.FIELD_TYPE_SCORE));
		m_oPlEnemyScore.setBackground(oColField);
		m_oPlEnemyScore.setForeground(oColFieldLines);
		m_oPlEnemyScore.setColorShip(m_oColShip);
		m_oPlEnemyScore.setColorShipSunk(m_oColShipSunk);
		m_oPlEnemyScore.init(4, 68);
		
		if (m_isBot) {
			m_plTestShips = (BattleShipsField)m_oCntMain.add(new BattleShipsField(BattleShipsUtility.MAX_SHIP_TYPE, BattleShipsField.FIELD_TYPE_ME));
			m_plTestShips.setBackground(oColField);
			m_plTestShips.setForeground(oColFieldLines);
			m_plTestShips.setColorShip(m_oColShip);
			m_plTestShips.setColorShipSunk(m_oColShipSunk);
			m_plTestShips.setVisible(false);
		}
		
		//**m_oBtnReady = (GfxAnimatedButton)m_oCntMain.add(new GfxAnimatedButton((Object)this, "BtnReady", oImgBtnOn, oImgBtnOff));
		m_oBtnReady = (GfxAnimatedButton)m_oCntMain.add(new GfxAnimatedButton((Object)this, "n", oImgBtnOn, oImgBtnOff));
		m_oBtnReady.setFont(fntMainFontBold);
		m_oBtnReady.setLabel(getString("Ready"));
		m_oBtnReady.setEnabled(false);
			
		//**m_oBtnToggleHideShips = (GfxAnimatedButton)m_oCntMain.add(new GfxAnimatedButton((Object)this, "BtnToggleHideShips", oImgBtnOn, oImgBtnOff));
		m_oBtnToggleHideShips = (GfxAnimatedButton)m_oCntMain.add(new GfxAnimatedButton((Object)this, "h", oImgBtnOn, oImgBtnOff));
		m_oBtnToggleHideShips.setFont(oFntMainFont);
		m_oBtnToggleHideShips.setLabel(getString("HideShips"));

		m_oLblStatus = (TextDisplayPanel)m_oCntMain.add(new TextDisplayPanel());
		m_oLblStatus.setFont(oFntMainFont);
		m_oLblStatus.setText(getString("Status"), TextDisplayPanel.AUTO_RESIZE);

		m_oLblScore = (Label)m_oCntMain.add(new Label());
		m_oLblScore.setFont(new Font("Monospaced", Font.PLAIN, 12));
		m_oLblScore.setVisible(false);
		
		m_oTxtStatus = (TextDisplayPanel)add(new TextDisplayPanel());
		m_oTxtStatus.setFont(fntMainFontBold);
		m_oTxtStatus.setBackground(Color.white);
		m_oTxtStatus.setPadding(2);
		m_oTxtStatus.setText(BattleShipsUtility.getCopyright());
		m_oTxtStatus.setWithBorder(true);
		//**m_oTxtStatus.addMouseListener(new TheMouseAdapter((Object)this, "TxtStatus"));
		m_oTxtStatus.addMouseListener(new TheMouseAdapter((Object)this, "t"));
		
		Font oFntChat = new Font("SansSerif", Font.PLAIN, 12);

		m_oTxtChatInput = (TextField)m_oCntMain.add(new TextField());
		m_oTxtChatInput.setFont(oFntChat);
		m_oTxtChatInput.setBackground(Color.white);
		m_oTxtChatInput.setEnabled(false);
		//**m_oTxtChatInput.addKeyListener(new TheKeyAdapter((Object)this, "TxtChatInput"));
		m_oTxtChatInput.addKeyListener(new TheKeyAdapter((Object)this, "l"));
		
		m_oLblChat = (TextDisplayPanel)m_oCntMain.add(new TextDisplayPanel());
		m_oLblChat.setFont(oFntMainFont);
		m_oLblChat.setText(getString("Chat"), TextDisplayPanel.AUTO_RESIZE);
		//**m_oLblChat.addMouseListener(new TheMouseAdapter((Object)this, "LblChat"));
		m_oLblChat.addMouseListener(new TheMouseAdapter((Object)this, "ch"));

		m_oTxtChatOutput = (TextArea)m_oCntMain.add(new TextArea("", 1, 1, TextArea.SCROLLBARS_VERTICAL_ONLY));
		m_oTxtChatOutput.setEditable(false);
		m_oTxtChatOutput.setFont(oFntChat);
		m_oTxtChatOutput.setBackground(Color.white);

		m_oPnlConnect = (Container)m_oCntMain.add(new Container());
		
		oCbgConnect = new CheckboxGroup();

		m_oCbClient = (Checkbox)m_oPnlConnect.add(new Checkbox(getString("Client"), false, oCbgConnect));
		m_oCbClient.setFont(oFntMainFont);
		m_oCbClient.setLocation(0, 0);
		//**m_oCbClient.addMouseListener(new TheMouseAdapter((Object)this, "CbClient"));
		m_oCbClient.addMouseListener(new TheMouseAdapter((Object)this, "x"));

		m_oCbServer = (Checkbox)m_oPnlConnect.add(new Checkbox(getString("Server"), true, oCbgConnect));
		m_oCbServer.setFont(oFntMainFont);
		m_oCbServer.setLocation(0, 18);
		//**m_oCbServer.addMouseListener(new TheMouseAdapter((Object)this, "CbServer"));
		m_oCbServer.addMouseListener(new TheMouseAdapter((Object)this, "y"));
		
		m_cbComputer = (Checkbox)m_oPnlConnect.add(new Checkbox(getString("Computer"), true, oCbgConnect));
		m_cbComputer.setFont(oFntMainFont);
		m_cbComputer.setLocation(0, 36);
		//**m_oCbServer.addMouseListener(new TheMouseAdapter((Object)this, "CbComputer"));
		m_cbComputer.addMouseListener(new TheMouseAdapter((Object)this, "com"));
		
		m_oPnlClient = (Container)m_oPnlConnect.add(new Container());
		m_oPnlClient.setVisible(false);
		
		m_oTxtServer = (TextField)m_oPnlClient.add(new TextField(getHost()));
		m_oTxtServer.setFont(oFntChat);
		m_oTxtServer.addKeyListener(new TheKeyAdapter((Object)this, "r"));

		m_txtPort = (TextField)m_oPnlClient.add(new TextFieldWithLimit(Integer.toString(port), 5, 5));
		m_txtPort.setFont(oFntChat);

		m_lblHost = (TextDisplayPanel)m_oPnlClient.add(new TextDisplayPanel());
		m_lblHost.setFont(oFntMainFont);
		m_lblHost.setText(getString("Host"), TextDisplayPanel.AUTO_RESIZE);
		m_lblHost.setLocation(0, -2);

		m_lblPort = (TextDisplayPanel)m_oPnlClient.add(new TextDisplayPanel());
		m_lblPort.setFont(oFntMainFont);
		m_lblPort.setText(getString("Port"), TextDisplayPanel.AUTO_RESIZE);
		m_lblPort.setLocation(151, -2);

		//**m_oBtnToggleConnect = (GfxAnimatedButton)m_oPnlClient.add(new GfxAnimatedButton((Object)this, "BtnToggleConnect", oImgBtnOn, oImgBtnOff));
		m_oBtnToggleConnect = (GfxAnimatedButton)m_oPnlClient.add(new GfxAnimatedButton((Object)this, "q", oImgBtnOn, oImgBtnOff));
		m_oBtnToggleConnect.setFont(oFntMainFont);
		m_oBtnToggleConnect.setLabel(getString("Connect"));

		//**m_oBtnToggleServer = (GfxAnimatedButton)m_oPnlClient.add(new GfxAnimatedButton((Object)this, "BtnToggleServer", oImgBtnOn, oImgBtnOff));
		m_oBtnToggleServer = (GfxAnimatedButton)m_oPnlConnect.add(new GfxAnimatedButton((Object)this, "g", oImgBtnOn, oImgBtnOff));
		m_oBtnToggleServer.setFont(oFntMainFont);
		m_oBtnToggleServer.setLabel(getString("ServerStart"));

		m_oCbRestoreWindow = (Checkbox)m_oPnlConnect.add(new Checkbox(getString("RestoreWindow"), bRestoreWindow));
		m_oCbRestoreWindow.setFont(oFntMainFont);
		//**m_oCbRestoreWindow.addMouseListener(new TheMouseAdapter((Object)this, "CbRestoreWindow"));
		m_oCbRestoreWindow.addMouseListener(new TheMouseAdapter((Object)this, "rw"));
		
		m_oAuGameStart = getAudioClip("res/game_start.au");
		m_oAuShipHit = getAudioClip("res/ship_hit.au");
		m_oAuShipSink = getAudioClip("res/ship_sink.au");
		m_oAuGameWon = getAudioClip("res/game_won.au");
		m_oAuGameLost = getAudioClip("res/game_lost.au");
		m_oAuConnect = getAudioClip("res/connect.au");
		m_oCryptUtil = new CryptUtil();

		m_pnlPlayerName = (Container)m_oPnlClient.add(new Container());
		m_pnlPlayerName.setVisible(true);
		
		m_txtPlayerName = (TextField)m_pnlPlayerName.add(new TextFieldWithLimit(20));
		m_txtPlayerName.setFont(oFntChat);
		m_txtPlayerName.setLocation(0, 1);
		m_txtPlayerName.setBackground(Color.white);
		m_txtPlayerName.addKeyListener(new TheKeyAdapter((Object)this, "pn"));
		//**m_oTxtChatInput.addKeyListener(new TheKeyAdapter((Object)this, "TxtChatInput"));
		//m_oTxtChatInput.addKeyListener(new TheKeyAdapter((Object)this, "l"));

		m_lblPlayerName = (TextDisplayPanel)m_pnlPlayerName.add(new TextDisplayPanel());
		m_lblPlayerName.setFont(oFntMainFont);
		m_lblPlayerName.setText(getString("PlayerName"), TextDisplayPanel.AUTO_RESIZE);
		m_lblPlayerName.setLocation(0, -2);
		m_lblPlayerName.addMouseListener(new TheMouseAdapter((Object)this, "fps"));

		m_btnSetPlayerName = (GfxAnimatedButton)m_pnlPlayerName.add(new GfxAnimatedButton((Object)this, "spn", oImgBtnOn, oImgBtnOff));
		m_btnSetPlayerName.setFont(oFntMainFont);
		m_btnSetPlayerName.setLabel(getString("Set"));
		m_btnSetPlayerName.setEnabled(false);

		m_btnDisconnectEnemy = (GfxButton)m_pnlPlayerName.add(new GfxButton((Object)this, "de", getImage("res/disconnect.gif")));
		m_btnDisconnectEnemy.setBounds(1, 1);
		m_btnDisconnectEnemy.setVisible(false);
		
		setPlayerName(getCookie("PlayerName"));
		try {
			m_mode = Integer.parseInt(getCookie("Mode"));
		} catch (NumberFormatException e) { }
		if (m_startBot) {
			m_mode = BattleShipsUtility.MODE_COMPUTER;
		} else if (m_startServer) {
			m_mode = BattleShipsUtility.MODE_SERVER;
		}
		setMode(m_mode, false);
		//m_btnDisconnectEnemy.setText(getString("DisconnectEnemy"), TextDisplayPanel.AUTO_RESIZE);
		
		if (m_bClientOnly && !bUseLocalConfig) {
			m_mode = BattleShipsUtility.MODE_CLIENT;
			setBounds(0, getParent().getInsets().top, 400, 300);
			remove(m_oTxtStatus);
			add(m_oTxtStatus, 0);
			m_oTxtStatus.setBounds(HORIZ_BORDER_PADDING, VERT_BORDER_PADDING, 300, 82);
			connect();
		} else {
			bRetTmp = initConfig();
			if (bRetTmp) {
				if (m_startServer || m_startBot) {
					startServer();
					setStatus(getString("ServerStarted"));
					if (m_bUseWebServer) {
						startWebServer();
					}
				}
				if (m_startBot) {
					m_botThread = new BotThread();
					m_botThread.start();
				}
			}
		}
	}
	
	public void stop() {
		if (m_oSchSocket != null) {
			m_oSchSocket.interrupt();
		}
		if (m_oWebServer != null) {
			m_oWebServer.interrupt();
		}
		if (m_oTmrGameOver != null) m_oTmrGameOver.interrupt();
		m_oCntState.stop();
	}
	
	private Image buildShipSegment(Color oColor, int iCellWidth) {
		iCellWidth--;
		int x = 0;
		int y = 0;
		int aiPixel[] = new int[iCellWidth * iCellWidth];
		int iIndex = 0;
		int iRed = 0;
		int iGreen = 0;
		int iBlue = 0;
		int iRedMax = oColor.getRed();
		int iGreenMax = oColor.getGreen();
		int iBlueMax = oColor.getBlue();
		for (y = 0; y < iCellWidth; y++) {
		    for (x = 0; x < iCellWidth; x++) {
			    iRed = calcComp(x, y, iRedMax, iCellWidth);
				iGreen = calcComp(x, y, iGreenMax, iCellWidth);
				iBlue = calcComp(x, y, iBlueMax, iCellWidth);
				aiPixel[iIndex++] = 255 << 24 | iRed << 16 | iGreen << 8 | iBlue;
		    }
		}
		return createImage(new MemoryImageSource(iCellWidth, iCellWidth, aiPixel, 0, iCellWidth));
	}
	
	private int calcComp(int x, int y, int iMax, int iCellWidth) {
		int iTmp;
		//System.out.println("new x: " + x + " val1: " + Math.round(iCellWidth / 4.5) + " val2: " + Math.round(iCellWidth / 4.5));
		iTmp = (x * iMax / 2) / (iCellWidth - 1) + (y * iMax / 2) / (iCellWidth - 1) + Math.round(iMax / 3);
		if (iTmp > 255) iTmp = 255;
		return iTmp;
	}

	private boolean initConfig() {
		int i;
		int iShipTypeCount;
		int iFieldDim;
		FontMetrics oFntMetrChat = m_oCntMain.getFontMetrics(m_oTxtChatInput.getFont());
		FontMetrics oFntMetrScore = m_oCntMain.getFontMetrics(m_oLblScore.getFont());
		int iFontHeight = m_oFntMetrMain.getHeight();
		int iFontHeightChat = oFntMetrChat.getHeight() + oFntMetrChat.getMaxDescent() * 2;
		Container oParent = getParent();
		int iTop = oParent.getInsets().top;
		int iWidth;
		int iHeight;
		Image oImgShipSegment;
		Image oImgShipSegmentSunk;
		boolean bRet = true;
		int infoCellWidth = Math.round(m_iCellWidth * 77 / 100);
		if (infoCellWidth < 14)
			infoCellWidth = 14;
		int scoreFieldHeight = 28; //old 21 - recalc all number :-(((
		int statePanelFontSize = (int)Math.round(m_iCellWidth * m_iFieldWidth / 100d * 12.5d);
		
		oImgShipSegment = buildShipSegment(m_oColShip, m_iCellWidth);
		oImgShipSegmentSunk = buildShipSegment(m_oColShipSunk, m_iCellWidth);
		m_oLblMyShips.setLocation(HORIZ_BORDER_PADDING, VERT_BORDER_PADDING);
		m_oPlInfo.removeAllShips();
		m_oPlInfo.init(infoCellWidth, 14);
		m_oPlInfo.setImageShipSegment(buildShipSegment(m_oColShip, infoCellWidth));
		m_oPlMyShips.removeAllShips();
		m_oPlMyShips.init(m_iCellWidth, m_iFieldWidth);
		m_oPlMyShips.setImageShipSegment(oImgShipSegment);
		m_oPlMyShips.setImageShipSegmentSunk(oImgShipSegmentSunk);
		m_oPlMyScore.removeAllShips();
		m_oPlEnemyShips.removeAllShips();
		m_oPlEnemyShips.init(m_iCellWidth, m_iFieldWidth);
		m_oPlEnemyShips.setImageShipSegment(oImgShipSegment);
		m_oPlEnemyShips.setImageShipSegmentSunk(oImgShipSegmentSunk);
		m_oPlEnemyScore.removeAllShips();
		for (i = 1; i <= BattleShipsUtility.MAX_SHIP_TYPE; i++) {
			iShipTypeCount = m_iShipTypeCount[i - 1];
			m_oPlMyShips.setShipTypeCount(i, iShipTypeCount);
			m_oPlMyScore.setShipTypeCount(i, iShipTypeCount);
			m_oPlEnemyScore.setShipTypeCount(i, iShipTypeCount);
			m_oPlInfo.setShipTypeCount(i, iShipTypeCount);
		}
		if (!m_oPlMyShips.addAllMyShips()) {
			m_oPlMyShips.removeAllShips();
			m_oCbClient.setEnabled(false);
			m_oCbServer.setEnabled(false);
			m_cbComputer.setEnabled(false);
			m_oTxtServer.setEnabled(false);
			m_txtPort.setEnabled(false);
			m_oBtnToggleConnect.setEnabled(false);
			m_oBtnToggleServer.setEnabled(false);
			m_oBtnToggleHideShips.setEnabled(false);
			setStatus(getString("FieldTooSmall"));
			bRet = false;
		}
		m_oPlMyScore.addAllMyShips();
		m_oPlEnemyScore.addAllMyShips();
		
		if (m_isBot) {
			m_plTestShips.removeAllShips();
			m_plTestShips.init(4, m_iFieldWidth);
			m_plTestShips.addTestShips();
		}
		
		iFieldDim = m_oPlMyShips.getSize().width;
		if (iFieldDim < MIN_FIELD_SIZE) iFieldDim = MIN_FIELD_SIZE;
		iHeight = iFieldDim + VERT_BORDER_PADDING + scoreFieldHeight + 221;
		iWidth = iFieldDim * 2 + HORIZ_BORDER_PADDING * 3;
		oParent.setSize(iWidth, iHeight + iTop);
		setBounds(0, iTop, iWidth, iHeight);
		m_oCntMain.setBounds(0, 0, iWidth, iHeight);
		m_cntModalMask.setBounds(0, 0, iWidth, iHeight);
		m_oGfxBtnInfo.setLocation(HORIZ_BORDER_PADDING + iFieldDim - 16, VERT_BORDER_PADDING);
		m_oPlInfo.addShipsAndInfo();
		m_oPlInfo.setLocation(m_oPlInfo.getLocation().x, VERT_BORDER_PADDING + 19 + Math.round(m_iCellWidth / 2));
		m_oPlMyScore.setBounds(HORIZ_BORDER_PADDING, VERT_BORDER_PADDING + iFieldDim + 18, iFieldDim, scoreFieldHeight);
		m_oLblEnemyShips.setLocation(HORIZ_BORDER_PADDING + iFieldDim + 15, VERT_BORDER_PADDING);
		m_oBtnToggleSound.setLocation(HORIZ_BORDER_PADDING + iFieldDim + 15 + (iFieldDim - 16), VERT_BORDER_PADDING);
		m_oPlEnemyShips.setLocation(HORIZ_BORDER_PADDING + iFieldDim + 15, VERT_BORDER_PADDING + 19);
		m_oPlEnemyScore.setBounds(HORIZ_BORDER_PADDING + iFieldDim + 15, VERT_BORDER_PADDING + iFieldDim + 18, iFieldDim, scoreFieldHeight);
		m_oBtnReady.setLocation(HORIZ_BORDER_PADDING, VERT_BORDER_PADDING + iFieldDim + scoreFieldHeight + 22);
		m_oBtnToggleHideShips.setLocation(HORIZ_BORDER_PADDING + iFieldDim - (m_oFntMetrMain.stringWidth(m_oBtnToggleHideShips.getLabel()) + 15), VERT_BORDER_PADDING + iFieldDim + scoreFieldHeight + 22);
		m_oLblStatus.setLocation(HORIZ_BORDER_PADDING, VERT_BORDER_PADDING + iFieldDim + scoreFieldHeight + 50);
		m_oLblScore.setText(getString("You") + ":00 " + getString("Enemy") + ":00");
		m_oLblScore.setBounds(HORIZ_BORDER_PADDING + iFieldDim - (oFntMetrScore.stringWidth(m_oLblScore.getText()) + 5), VERT_BORDER_PADDING + iFieldDim + scoreFieldHeight + 52, oFntMetrScore.stringWidth(m_oLblScore.getText()) + 5, oFntMetrScore.getHeight());
		m_oTxtStatus.setBounds(HORIZ_BORDER_PADDING, VERT_BORDER_PADDING + iFieldDim + scoreFieldHeight + 69, iFieldDim, 82);
		m_oLblChat.setLocation(HORIZ_BORDER_PADDING + iFieldDim + 15, VERT_BORDER_PADDING + iFieldDim + scoreFieldHeight + 20);
		m_oTxtChatInput.setBounds(HORIZ_BORDER_PADDING + iFieldDim + 15, VERT_BORDER_PADDING + iFieldDim + scoreFieldHeight + 39, iFieldDim, iFontHeightChat);
		m_oTxtChatOutput.setBounds(HORIZ_BORDER_PADDING + iFieldDim + 15, VERT_BORDER_PADDING + iFieldDim + iFontHeightChat + scoreFieldHeight + 40, iFieldDim, 112 - iFontHeightChat);
		m_oPnlConnect.setBounds(HORIZ_BORDER_PADDING, VERT_BORDER_PADDING + iFieldDim + scoreFieldHeight + 156, iFieldDim * 2 + 15, iFontHeight * 3);
		m_oCbClient.setSize(m_oFntMetrMain.stringWidth(m_oCbClient.getLabel()) + 20, iFontHeight);
		m_oCbServer.setSize(m_oFntMetrMain.stringWidth(m_oCbServer.getLabel()) + 20, iFontHeight);
		m_cbComputer.setSize(m_oFntMetrMain.stringWidth(m_cbComputer.getLabel()) + 20, iFontHeight);
		m_oTxtServer.setBounds(0, iFontHeight - 2, 150, iFontHeightChat);
		m_txtPort.setBounds(151, iFontHeight - 2, 35, iFontHeightChat);
		m_oBtnToggleConnect.setLocation(187, iFontHeight - 2);
		m_oBtnToggleConnect.setSize(m_oBtnToggleConnect.getSize().width, iFontHeightChat - 1);
		m_oPnlClient.setBounds(m_oFntMetrMain.stringWidth(m_cbComputer.getLabel()) + 25, 0, iFieldDim * 2 + 15 - (m_oFntMetrMain.stringWidth(m_oCbServer.getLabel()) + 30), iFontHeightChat * 2);
		//m_oPnlClient.setBackground(Color.green);
		m_oBtnToggleServer.setLocation(m_oFntMetrMain.stringWidth(m_cbComputer.getLabel()) + 30, 5);
		m_oCbRestoreWindow.setBounds(HORIZ_BORDER_PADDING + iFieldDim + (iFieldDim - (m_oFntMetrMain.stringWidth(m_oCbRestoreWindow.getLabel()) + 20)), 0, m_oFntMetrMain.stringWidth(m_oCbRestoreWindow.getLabel()) + 20, iFontHeight);
		m_pnlPlayerName.setBounds(iFieldDim - m_oFntMetrMain.stringWidth(m_cbComputer.getLabel()) - 15, 0, iFieldDim, iFontHeight * 2);
		//m_pnlPlayerName.setBackground(Color.green);
		m_txtPlayerName.setBounds(0, iFontHeight - 2, 170, iFontHeightChat);
		m_btnSetPlayerName.setLocation(171, iFontHeight - 2);
		m_btnSetPlayerName.setSize(m_btnSetPlayerName.getSize().width, iFontHeightChat - 1);
		m_btnDisconnectEnemy.setLocation(m_pnlPlayerName.getSize().width - 25, iFontHeight - 6);
		m_cntSelectEnemy.setLocation(2 * iFieldDim + HORIZ_BORDER_PADDING * 2 - m_cntSelectEnemy.getSize().width - 5, iFieldDim + VERT_BORDER_PADDING + 19 - m_cntSelectEnemy.getSize().height - 5);
		//m_cntSelectEnemy.setVisible(true);
		m_cntConfirmation.setLocation((2 * iFieldDim + HORIZ_BORDER_PADDING + 30) / 2 - m_cntConfirmation.getSize().width / 2, VERT_BORDER_PADDING + iFieldDim / 2 - m_cntConfirmation.getSize().height / 2);
		m_oCntState.setFont(new Font("SansSerif", Font.BOLD, statePanelFontSize));
		if (m_bClientOnly) {
			m_oCbClient.setVisible(false);
			m_oCbServer.setVisible(false);
			m_cbComputer.setVisible(false);
			m_oCbRestoreWindow.setVisible(false);
			m_oBtnToggleServer.setVisible(false);
			m_oBtnToggleConnect.setVisible(false);
			m_oTxtServer.setVisible(false);
			m_txtPort.setVisible(false);
			m_lblHost.setVisible(false);
			m_lblPort.setVisible(false);
			m_oPnlClient.setVisible(true);
		}
		return bRet;
	}
	
	public void setIsBot(boolean value, boolean autoBot) {
		m_isBot	= value;
		m_autoBot = autoBot;
	}
	
	public void setTimeOutSeconds(int timeOutSeconds) {
		m_timeOutSeconds = timeOutSeconds;
	}
	
	public void setStartBot(boolean startBot) {
		m_startBot = startBot;
	}
	
	public void setStartServer(boolean startServer) {
		m_startServer = startServer;
	}
	
	public void passShot(boolean bEnemy, Point oPoint, boolean bHit, boolean bSunk, int iType, boolean bWon, ArrayList<Point> fieldHits) {
		String sStatus;
		String sTmp;
		Color oCol;
		
		// play the sound; extra section due to slightly different logic
		//System.out.println("BattleShipsPanel.passShot(): " + bEnemy + " - " + oPoint.toString() + " - " + bHit);
		if (m_bPlaySound) {
			if (bSunk) {
				playAudioClip(m_oAuShipSink);
				if (bWon) {
					if (bEnemy) {
						m_oAuGameOver = m_oAuGameWon;
					} else {
						m_oAuGameOver = m_oAuGameLost;
					}
				}
			} else if (bHit) {
				playAudioClip(m_oAuShipHit);
			}
		}

		//System.out.println("BattleShipsPanel.passShot(): after playing sound ...");
		if (bEnemy) {
			sStatus = getString("You") + ":";
		} else {
			if (!m_enemyPlayerName.equals("")) {
				sStatus = m_enemyPlayerName + ":";
			} else {
				sStatus = getString("Enemy") + ":";
			}
		}
		sStatus += " " + MessageFormat.format(getString("Shot"), new Object[]{Integer.toString(oPoint.x + 1), Integer.toString(oPoint.y + 1)});
		if (bHit) sStatus += " " + getString("Hit");
		if (bSunk) {
			sStatus += " " + getString("Sunk") + " (" + getString("BattleShipType" + iType) + ")";
			if (bEnemy) {
				m_oPlEnemyScore.sinkShip(iType);
			} else {
				m_oPlMyScore.sinkShip(iType);
			}
		}

		//System.out.println("BattleShipsPanel.passShot(): after setting score panel ...");
		if (bWon) {
			if (bEnemy) {
				sTmp = getString("Won");
				oCol = new Color(0, 130, 0);
				m_oPntScore.x++;
			} else {
				sTmp = getString("Lost");
				oCol = new Color(180, 0, 0);
				m_oPntScore.y++;
			}
			m_gameOver = true;
			m_botPaused = true;
			m_oPlEnemyShips.setPlaying(false);
			setButtonLabelAndSize(m_oBtnReady, getString("NewGame"), false);
			m_oBtnReady.setEnabled(true);
			m_oCntState.setText(sTmp);
			m_oCntState.setForeground(oCol);
			//**m_oTmrGameOver = new TheTimerTask((Object)this, "GameOver");
			m_oTmrGameOver = new TheTimerTask((Object)this, "i");
			m_oTmrGameOver.start(1600, 0);
			setScore();
			sStatus += "\n" + sTmp;
			setStatus(sStatus);
			if (bEnemy) {
				sendMessage("shoot", oPoint.x + ";" + oPoint.y);
				sendMessage("gameresult", m_oPntScore.x + ";" + m_oPntScore.y);
			}
		} else {
			setStatus(sStatus);
			if (!bHit) {
				if (bEnemy) {
					m_bMyTurn = false;
				} else {
					m_bMyTurn = true;
				}
			}
			setTurn(false);
			if (bEnemy) {
				sendMessage("shoot", oPoint.x + ";" + oPoint.y);
				if (m_isBot) {
					m_Ai.reportLastShot(oPoint, bHit, bSunk, fieldHits);
				}
			}
			if (!bHit) {
				if (!bEnemy) {
					if (m_isBot) {
						shootBot();
					}
				}
			} else {
				if (bEnemy) {
					if (m_isBot) {
						shootBot();
					}
				}
			}
			
		}
		//System.out.println("BattleShipsPanel.passShot(): after setting status and turn ...: " + oPoint.x + " - " + oPoint.y + " - " + bEnemy + " - " + bHit);
		//if (bEnemy) sendMessage("shoot", oPoint.x + ";" + oPoint.y);
		//System.out.println("finished BattleShipsPanel.passShot()");
		
	}
	
	private void shootBot() {
		if (!m_botPaused) {
			try {
				Thread.sleep(BattleShipsBotLogic.BOT_DELAY);
			} catch (InterruptedException e) {}
			m_oPlEnemyShips.shoot(m_Ai.getNextShot());
		}
	}
	
	private void setScoreBounds() {
		FontMetrics oFntMetrScore = m_oCntMain.getFontMetrics(m_oLblScore.getFont());
		int width = oFntMetrScore.stringWidth(m_oLblScore.getText());
		int fieldDim = m_oPlMyShips.getSize().width;
		m_oLblScore.setBounds(HORIZ_BORDER_PADDING + fieldDim - (width + 5), VERT_BORDER_PADDING + fieldDim + m_oPlMyScore.getSize().height + 52, width + 5, oFntMetrScore.getHeight());
	}
	
	private void setStatus(String sMessage) {
		m_oTxtStatus.setText(sMessage);
	}

	private void appendStatus(String sMessage) {
		m_oTxtStatus.append("\n" + sMessage);
	}
	
	private int parseIntParm(String sParm, int iDefault) {
		sParm = getParameter(sParm);
		int iTmp = iDefault;
		if (sParm != null) {
			try {
				iTmp = Integer.parseInt(sParm);
			} catch (NumberFormatException e) {}
		}
		return iTmp;
	}
	
	private int parseIntParm(String sParm, int iDefault, boolean readCookie) {
		int iTmp = iDefault;
		String cookie = getCookie(sParm);
		try {
			if (cookie != null && !cookie.equals("")) {
				iTmp = Integer.parseInt(cookie);
			} else {
				sParm = getParameter(sParm);
				if (sParm != null) {
					iTmp = Integer.parseInt(sParm);
				}
			}
		} catch (NumberFormatException e) {}
		return iTmp;
	}

	private Color parseColorParm(String sParm, Color oColDefault) {
		sParm = getParameter(sParm);
		Color oColTmp = oColDefault;
		int[] iTmp = new int[3];
		int i = 0;
		if (sParm != null) {
			StringTokenizer oStr = new StringTokenizer(sParm, ",");
			if (oStr.countTokens() == 3) {
				try {
					while (oStr.hasMoreTokens()) {
						iTmp[i] = Integer.parseInt(oStr.nextToken());
						if (iTmp[i] < 0 || iTmp[i] > 255) break;
						i++;
					}
				} catch (NumberFormatException e) {}
			}
			if (i == 3) {
				oColTmp = new Color(iTmp[0], iTmp[1], iTmp[2]);
			}
		}
		return oColTmp;
	}
	
	private void changeConfig(String sConfig) {
		int iShipType;
		int iShipTypeCount;
		boolean bServerInconsistent = false;
		StringTokenizer oStr;

		if (m_sCurrentConfig.equals(sConfig)) {
			m_bConfigReady = true;
			return;
		}
		oStr = new StringTokenizer(sConfig, ";");
		m_iCellWidth = Integer.parseInt(oStr.nextToken());
		m_iFieldWidth = Integer.parseInt(oStr.nextToken());
		for (iShipType = 1; iShipType <= BattleShipsUtility.MAX_SHIP_TYPE; iShipType++) {
			if (oStr.hasMoreTokens()) {
				iShipTypeCount = Integer.parseInt(oStr.nextToken());
				m_iShipTypeCount[iShipType - 1] = iShipTypeCount;
			} else {
				bServerInconsistent = true;
				break;
			}
		}
		if (oStr.hasMoreTokens()) bServerInconsistent = true;
		if (bServerInconsistent) {
			setStatus(getString("VersionConflict"));
			stopConnect();
		} else {
			m_sCurrentConfig = sConfig;
			initConfig();
			if (!m_bClientOnly) appendStatus(getString("ConfigChange"));
			repaint();
		}
		m_bConfigReady = true;
	}
	
	private void setScore() {
		String sTmp = getString("You") + ":" + BattleShipsUtility.getZeroed(m_oPntScore.x) + " " +
					  (!m_enemyPlayerName.equals("") ? m_enemyPlayerName : getString("Enemy")) +
					  ":" + BattleShipsUtility.getZeroed(m_oPntScore.y);
		m_oLblScore.setText(sTmp);
	}
	
	public String getString(String sKey) {
		String string = getParameter("String_" + sKey);
		if (string.indexOf("{br}") > -1) {
			string = StringTool.replace(string, "{br}", "\n");
		}
		return string;
	}

	private String getParameter(String sName) {
		return m_oBspcParent.getParameter(sName);
	}

	private String getParameter(String sName, String sDefault) {
		return m_oBspcParent.getParameter(sName, sDefault);
	}

	private String getHost() {
		return m_oBspcParent.getHost();
	}
	
	private AudioClip getAudioClip(String sFileName) {
		return m_oBspcParent.getAudioClip(sFileName);
	}
	
	private Image getImage(String sFileName) {
		return m_oBspcParent.getImage(sFileName);
	}
	
	private void setNormalState() {
		m_oBspcParent.setNormalState();
	}
	
	private void setCookie(String cookieName, String value) {
		m_oBspcParent.setCookie(cookieName, value, 365);
	}
	
	private String getCookie(String cookieName) {
		return m_oBspcParent.getCookie(cookieName);
	}

	public boolean isConfigReady() {
		return m_bConfigReady;
	}
	
	public boolean isConnected() {
		return m_bConnected;
	}
	
	public void setPlayerName(String playerName) {
		m_txtPlayerName.setText(playerName);
		setServerPlayerName();
	}
	
	public void socketDataArrived(String sMessage, String sData) {
		//System.out.println("starting socketDataArrived(" + sMessage + ", " + sData + ")");
		StringTokenizer oStrToken = null;
		boolean refreshTimeOut = false;
		if (sMessage.equals("shoot")) {
			oStrToken = new StringTokenizer(sData, ";");
			Point oPoint = new Point(Integer.parseInt(oStrToken.nextToken()), Integer.parseInt(oStrToken.nextToken()));
			m_oPlMyShips.shoot(oPoint);
			refreshTimeOut = true;
		} else if (sMessage.equals("shipinfo")) {
			m_oPlEnemyShips.addAllEnemyShips(m_oCryptUtil.decode(sData));
			setStatusCondName("EnemyReady");
			m_bEnemyReady = true;
			setTurn(true);
			if (m_bMeReady) {
				m_oBtnReady.setEnabled(false);
			}
			if (m_isBot) {
				try {
					Thread.sleep(BattleShipsBotLogic.BOT_DELAY);
				} catch (InterruptedException e) {}
				setReady();
			}
			refreshTimeOut = true;
		} else if (sMessage.equals("newgame")) {
			reset();
			setStatus(getString("NewGame"));
			if (m_isBot) {
				m_Ai = new BattleShipsBotLogic(m_iFieldWidth, m_debug);
				m_Ai.setTestShipsPanel(m_plTestShips);
				m_Ai.setEnemyScorePanel(m_oPlEnemyScore);
				m_oPlMyShips.setShipsRandomPosition();
				m_botPaused = false;
			}
			refreshTimeOut = true;
		} else if (sMessage.equals("chat")) {
			if (!m_enemyPlayerName.equals("")) {
				m_oTxtChatOutput.append(m_enemyPlayerName + "> " + sData + "\n");
			} else {
				m_oTxtChatOutput.append(getString("Enemy") + "> " + sData + "\n");
			}
			refreshTimeOut = true;
		} else if (sMessage.equals("serverresponse")) {
			m_oTxtChatOutput.append(getString("Server") + "> " + sData + "\n");
		} else if (sMessage.equals("config")) {
			changeConfig(sData);
		} else if (sMessage.equals("disconnected")) {
			stopConnect();
			setStatus(getString("Disconnected"));
			if (m_bClientOnly) {
				appendStatus(getString("RefreshPage"));
				connect();
			}
			if (m_mode != BattleShipsUtility.MODE_CLIENT) {
				startServer();
			}
			if (m_tmrTimeOut != null) m_tmrTimeOut.interrupt();
		} else if (sMessage.equals("playernameset")) {
			m_playerName = sData;
			setCookie("PlayerName", m_playerName);
			m_oTxtChatOutput.append(getString("Server") + "> " + getString("PlayerNameSet") + ": " + sData + "\n");
			m_oBspcParent.setWindowTitle(m_playerName + "@" + m_oTxtServer.getText());
		} else if (sMessage.equals("playernameexists")) {
			if (!m_playerName.equals("")) m_txtPlayerName.setText(m_playerName);
			m_oTxtChatOutput.append(getString("Server") + "> " + getString("PlayerNameExists") + ": " + sData + "\n");
			stopConnect();
			setStatus(getString("Disconnected"));
			if (m_tmrTimeOut != null) m_tmrTimeOut.interrupt();
		} else if (sMessage.equals("clientlistupdate")) {
			m_connWithDedicatedServer = true;
			updatePlayerList(sData);
		} else if (sMessage.equals("enemyfound")) {
			m_enemyPlayerName = sData;
			setStatus(
				MessageFormat.format(
				  	getString("EnemyFoundName"), 
				  	new Object[] { m_enemyPlayerName }
				)
			);
			m_oLblEnemyShips.setText(
				MessageFormat.format(
				  	getString("EnemyShipsName"), 
				  	new Object[] { m_enemyPlayerName }
				), TextDisplayPanel.AUTO_RESIZE
			);
			m_btnDisconnectEnemy.setVisible(true);
			if (m_isBot) {
				m_oPlMyShips.setShipsRandomPosition();
			}
			setEnemyFound();
			refreshTimeOut = true;
		} else if (sMessage.equals("status")) {
			if (sData.equals("Connected")) {
				if (m_mode == BattleShipsUtility.MODE_COMPUTER) {
					setStatus(getString("BotStarted"));
				} else {
					setStatus(getString("Connected"));
				}
				m_oBtnReady.setEnabled(true);
				m_oTxtChatInput.setEnabled(true);
				m_oPntScore = new Point(0, 0);
				m_oLblScore.setVisible(true);
				m_btnSetPlayerName.setEnabled(true);
				setScore();
				setScoreBounds();
				setServerPlayerName();
				if (!m_bClientOnly) {
					setCookie("ServerName", m_oTxtServer.getText());
					setCookie("Port", m_txtPort.getText());
				}
				if (m_mode != BattleShipsUtility.MODE_CLIENT) {
					if (!m_bClientOnly && m_oCbRestoreWindow.getState()) setNormalState();
					if (m_mode != BattleShipsUtility.MODE_COMPUTER && m_bPlaySound) playAudioClip(m_oAuConnect);
					sendMessage("config", m_sCurrentConfig);
				}
			} else if (sData.equals("Waiting")) {
				m_oBtnReady.setEnabled(false);
				m_oLblScore.setVisible(false);
				m_showSelectEnemy = true;
				//toggleSelectEnemy(true);
				appendStatus(getString("Waiting"));
			} else if (sData.equals("EnemyFound")) {
				setStatus(getString("EnemyFound"));
				setEnemyFound();
				refreshTimeOut = true;
			} else if (sData.equals("EnemyChanged")) {
				reset();
				m_oBtnReady.setEnabled(true);
				m_oPntScore = new Point(0, 0);
				m_oLblScore.setVisible(true);
				setScore();
				setStatus(getString("EnemyLeft"));
				appendStatus(getString("NewEnemy"));
				if (m_bPlaySound) playAudioClip(m_oAuConnect);
				refreshTimeOut = true;
			} else if (sData.equals("EnemyLeft")) {
				reset();
				m_oBtnReady.setEnabled(false);
				m_oLblScore.setVisible(false);
				m_showSelectEnemy = true;
				toggleSelectEnemy(true);
				togglePlayerNameControls(true);
				setStatusCondName("EnemyLeft");
				m_oLblEnemyShips.setText(getString("EnemyShips"), TextDisplayPanel.AUTO_RESIZE);
				toggleConfirmation(null);
				m_btnDisconnectEnemy.setVisible(false);
				appendStatus(getString("Waiting"));
				m_enemyPlayerName = "";
				if (m_tmrTimeOut != null) m_tmrTimeOut.interrupt();
			}
		}
		if (m_isBot && refreshTimeOut) {
			refreshTimeOut();
		}
		//System.out.println("finished socketDataArrived()");
		//m_oTxtStatus.setText(sMessage + ": " + sData);
	}
	
	private void refreshTimeOut() {
		if (m_tmrTimeOut != null) m_tmrTimeOut.interrupt();
		if (m_timeOutSeconds > 0) {
			m_tmrTimeOut = new TheTimerTask((Object)this, "tu");
			m_tmrTimeOut.start(m_timeOutSeconds * 1000, 0);
		}
	}
	
	private void toggleConfirmation(String message) {
		if (message == null) {
			m_cntModalMask.setVisible(false);
			m_cntConfirmation.setVisible(false);
		} else {
			m_cntModalMask.setVisible(true);
			m_lblConfirmation.setText(message, TextDisplayPanel.MANUAL_RESIZE);
			m_cntConfirmation.setVisible(true);
		}
	}
	
	private void setStatusCondName(String stringKey) {
		if (!m_enemyPlayerName.equals("")) {
			setStatus(
				MessageFormat.format(
				  	getString(stringKey + "Name"), 
				  	new Object[] { m_enemyPlayerName }
				)
			);
		} else {
			setStatus(getString(stringKey));
		}
	}
	
	private void setEnemyFound() {
		m_oBtnReady.setEnabled(true);
		m_oPntScore = new Point(0, 0);
		m_oLblScore.setVisible(true);
		m_showSelectEnemy = false;
		toggleSelectEnemy(false);
		togglePlayerNameControls(false);
		setScore();
		setScoreBounds();
		if (m_bPlaySound) playAudioClip(m_oAuConnect);
	}
	
	private void toggleSelectEnemy(boolean visible) {
		if (m_connWithDedicatedServer) {
			if (m_showSelectEnemy || !visible) {
				m_cntSelectEnemy.setVisible(visible);
				if (m_connectToFirstPlayer && visible) {
					m_lstPlayerName.select(0);
					requestGame();
				}
			}
		}
	}
	
	private void togglePlayerNameControls(boolean enabled) {
		m_txtPlayerName.setEnabled(enabled);
		m_btnSetPlayerName.setEnabled(enabled);
	}
	
	private void updatePlayerList(String playerList) {
		int i;
		boolean hasPlayers = false;
		StringTokenizer stk = new StringTokenizer(playerList, "\n");
		String playerName;
		ArrayList<String> list = new ArrayList<>();

		m_lstPlayerName.removeAll();
		while (stk.hasMoreTokens()) {
			playerName = stk.nextToken();
			list.add(playerName);
			hasPlayers = true;
		}
		if (hasPlayers) {
			Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
			ListIterator<String> listIterator = list.listIterator();
			while (listIterator.hasNext()) {
				m_lstPlayerName.add(listIterator.next());
			}
		}
		toggleSelectEnemy(hasPlayers);
	}
	
	public void socketDataArrived(String sMessage, String sData, String sSocketIdentifier) {}

	public void socketError(String sType, String sData) {
		String sStatus = getString("Error") + " ";
		m_oTxtStatus.setBackground(Color.white);
		if (sType.equals("UnknownHost")) {
			sStatus += getString("UnknownHost");
		} else if (sType.equals("ServerStart")) {
			if (sData.indexOf("Address already in use") > -1) {
				sStatus += MessageFormat.format(getString("ServerPortInUse"),
										new Object[]{String.valueOf(m_iServerPort)});
			} else if (sData.indexOf("socket closed") > -1) {
				sStatus = getString("ServerStopped");
			} else {
				sStatus += sData;
			}
		} else if (sType.equals("General")) {
			if (sData.indexOf("Connection refused") > -1) {
				sStatus += getString("ServerNotRunning");
				if (m_bClientOnly) sStatus += " " + getString("RefreshPage");
			} else {
				sStatus += sData;
			}
		}
		m_bHaveServerError = true;
		stopConnect();
		setStatus(sStatus);
	}

	public void socketError(String sType, String sData, String sSocketIdentifier) {}

	private void sendMessage(String sMessage, String sData) {
		//System.out.println("starting sendMessage(" + sMessage + ", " + sData + ")");
		if (m_oSchSocket != null) m_oSchSocket.sendMessage(sMessage, sData);
		//System.out.println("finished sendMessage");
	}
	
	private void setTurn(boolean bPlayStartSound) {
		if (m_bMeReady && m_bEnemyReady) {
			if (m_bPlaySound && bPlayStartSound) playAudioClip(m_oAuGameStart);
			if (m_bMyTurn) {
				m_oPlEnemyShips.setPlaying(true);
				m_oTxtStatus.setBackground(new Color(120, 255, 120));
				appendStatus(getString("YourTurn"));
			} else {
				m_oPlEnemyShips.setPlaying(false);
				m_oTxtStatus.setBackground(new Color(255, 90, 90));
				if (!m_enemyPlayerName.equals("")) {
					appendStatus(
						MessageFormat.format(
						  	getString("EnemyTurnName"), 
						  	new Object[] { m_enemyPlayerName }
						)
					);
				} else {
					appendStatus(getString("EnemyTurn"));
				}
			}
		}
	}
	
	private void reset() {
		//System.out.println("starting reset()");
		m_bPlaying = false;
		m_gameOver = true;
		m_botPaused = true;
		m_bMeReady = false;
		m_bEnemyReady = false;
		m_bMyTurn = false;
		m_oPlMyShips.reset();
		if (m_bShipsHidden) {
			m_bShipsHidden = false;
			m_oPlMyShips.setAllBattleShipsVisible(true);
		}
		m_oPlEnemyShips.reset();
		m_oPlEnemyScore.reset();
		m_oPlMyScore.reset();
		setButtonLabelAndSize(m_oBtnReady, getString("Ready"), false);
		m_oTxtStatus.setBackground(Color.white);
		setButtonLabelAndSize(m_oBtnToggleHideShips, getString("HideShips"), true);
		m_oCntState.stop();
		if (m_oTmrGameOver != null) m_oTmrGameOver.interrupt();
		repaint();
		//System.out.println("finished reset()");
	}
	
	private void stopConnect() {
		reset();
		if (m_connWithDedicatedServer) m_oBspcParent.setWindowTitle("");
		if (m_oSchSocket != null) m_oSchSocket.interrupt();
		m_oBtnReady.setEnabled(false);
		m_oTxtChatInput.setEnabled(false);
		m_oLblScore.setVisible(false);
		m_connWithDedicatedServer = false;
		m_showSelectEnemy = false;
		switch (m_mode) {
			case BattleShipsUtility.MODE_CLIENT:
				if (!m_bClientOnly)
					setButtonLabelAndSize(m_oBtnToggleConnect, getString("Connect"), false);
				m_oBtnToggleConnect.repaint();
				m_bConnected = false;
				m_oCbClient.setEnabled(true);
				m_oCbServer.setEnabled(true);
				m_cbComputer.setEnabled(true);
				m_oTxtServer.setEnabled(true);
				m_txtPort.setEnabled(true);
				m_cntSelectEnemy.setVisible(false);
				m_btnSetPlayerName.setEnabled(false);
				m_txtPlayerName.setEnabled(true);
				m_playerName = "";
				m_enemyPlayerName = "";
				toggleConfirmation(null);
				m_btnDisconnectEnemy.setVisible(false);
				m_oLblEnemyShips.setText(getString("EnemyShips"), TextDisplayPanel.AUTO_RESIZE);
				break;
			case BattleShipsUtility.MODE_SERVER:
			case BattleShipsUtility.MODE_COMPUTER:
				setButtonLabelAndSize(m_oBtnToggleServer, getString("ServerStart"), false);
				m_oBtnToggleServer.repaint();
				m_bServerRunning = false;
				m_oCbClient.setEnabled(true);
				m_oCbServer.setEnabled(true);
				m_cbComputer.setEnabled(true);
				break;
		}
	}
	
	//**public void BtnReady_MouseClicked(MouseEvent event) {
	public void n_ou(MouseEvent event) {
		if (event.getModifiers() != 16) return;
		setReady();
	}
	
	private void setReady() {
		if (m_bPlaying) {
			if (!m_isBot || m_gameOver) {
				sendMessage("newgame", "1");
				reset();
				setStatus(getString("NewGame"));
			}
		} else {
			if (m_isBot) {
				m_Ai = new BattleShipsBotLogic(m_iFieldWidth, m_debug);
				m_Ai.setTestShipsPanel(m_plTestShips);
				m_Ai.setEnemyScorePanel(m_oPlEnemyScore);
				m_botPaused = false;
				refreshTimeOut();
			}
			Point oPntShips = m_oPlMyShips.getShipsIntersectionPoint(true, false);
			if (oPntShips != null) {
				String sStatus = MessageFormat.format(getString("ShipsIntersect"),
										new Object[]{getString("BattleShipType" + oPntShips.x),
													 getString("BattleShipType" + oPntShips.y)});
				setStatus(sStatus);
			} else {
				String sShipInfo = m_oPlMyShips.getShipInfo();
				sendMessage("shipinfo", m_oCryptUtil.encode(sShipInfo));
				m_bPlaying = true;
				m_bMeReady = true;
				m_gameOver = false;
				if (m_bEnemyReady) {
					m_oBtnReady.setEnabled(false);
				} else {
					m_bMyTurn = true;
				}
				m_oBtnReady.setEnabled(false);
				setStatus(getString("Ready"));
				setTurn(true);
				//System.out.println("Info: " + sShipInfo);
			}
		}
	}

	//**public void BtnToggleHideShips_MouseClicked(MouseEvent event) {
	public void h_ou(MouseEvent event) {
		// TMP DEBUG
		//m_oCntState.setText("SOME TEST");
		//m_oCntState.start();
		// TMP DEBUG END
		if (event.getModifiers() != 16) return;
		if (m_bShipsHidden) {
			m_bShipsHidden = false;
			m_oPlMyShips.setAllBattleShipsVisible(true);
			setButtonLabelAndSize(m_oBtnToggleHideShips, getString("HideShips"), true);
		} else {
			m_bShipsHidden = true;
			m_oPlMyShips.setAllBattleShipsVisible(false);
			setButtonLabelAndSize(m_oBtnToggleHideShips, getString("ShowShips"), true);
		}
	}

	//**public void TxtStatus_MouseClicked(MouseEvent event) {
	public void t_ou(MouseEvent event) {
		if (m_bClientOnly && event.getModifiers() == 16 && !m_bConnected) {
			connect();
		}
	}
	
	//**public void TxtChatInput_KeyPressed(KeyEvent event) {
	public void l_ve(KeyEvent event) {
		if (event.getKeyCode() == 10) {
			String sText = m_oTxtChatInput.getText();
			if (sText.length() > 0) {
				m_oTxtChatInput.setText("");
				m_oTxtChatOutput.append(getString("You") + "> " + sText + "\n");
				if (sText.substring(0, 1).equals("/")) {
					sendMessage("servercommand", sText);
				} else {
					sendMessage("chat", sText);
				}
			}
		}
	}

	//**public void LblChat_MouseClicked(MouseEvent event) {
	public void ch_ou(MouseEvent event) {
		if (!m_isBot || event.getModifiers() != 16) return;
		if (m_botPaused) {
			m_botPaused = false;
			shootBot();
		} else {
			m_botPaused = true;
		}
	}
	
	//**public void TxtServer_KeyPressed(KeyEvent event) {
	public void r_ve(KeyEvent event) {
		if (event.getKeyCode() == 10) {
			connect();
		}
	}
	
	//**public void TxtPlayerName_KeyPressed(KeyEvent event) {
	public void pn_ve(KeyEvent event) {
		if (event.getKeyCode() == 10) {
			setServerPlayerName();
		}
	}
	
	//**public void CbClient_MouseClicked(MouseEvent event) {
	public void x_ou(MouseEvent event) {
		if (event.getModifiers() != 16) return;
		setMode(BattleShipsUtility.MODE_CLIENT, true);
	}
	
	//**public void CbServer_MouseClicked(MouseEvent event) {
	public void y_ou(MouseEvent event) {
		if (event.getModifiers() != 16) return;
		setMode(BattleShipsUtility.MODE_SERVER, true);
	}
	
	//**public void CbComputer_MouseClicked(MouseEvent event) {
	public void com_ou(MouseEvent event) {
		if (event.getModifiers() != 16) return;
		setMode(BattleShipsUtility.MODE_COMPUTER, true);
	}
	
	private void setMode(int mode, boolean setCookie) {
		/*TODO:
		- don't start server + bot when selecting "Computer"
		- leave Start button for mode = computer, let it act similar like for starting server
		- on stop, interrupt bot + server
		*/
		m_mode = mode;
		switch (mode) {
			case BattleShipsUtility.MODE_CLIENT:
				m_oPnlClient.setVisible(true);
				m_oBtnToggleServer.setVisible(false);
				m_oCbRestoreWindow.setVisible(false);
				if (!setCookie) {
					m_oCbClient.setState(true);
				}
				break;
			case BattleShipsUtility.MODE_SERVER:
				m_oPnlClient.setVisible(false);
				m_oBtnToggleServer.setVisible(true);
				m_oCbRestoreWindow.setVisible(true);
				if (!setCookie) {
					m_oCbServer.setState(true);
				}
				break;
			case BattleShipsUtility.MODE_COMPUTER:
				m_oPnlClient.setVisible(false);
				m_oBtnToggleServer.setVisible(true);
				m_oCbRestoreWindow.setVisible(false);
				if (!setCookie) {
					m_cbComputer.setState(true);
				}
				break;
		}
		if (setCookie)
			setCookie("Mode", Integer.toString(mode));
	}

	//**public void CbRestoreWindow_MouseClicked(MouseEvent event) {
	public void rw_omc(MouseEvent event) {
		if (event.getModifiers() != 16) return;
		setCookie("RestoreWindow", (m_oCbRestoreWindow.getState() ? "1" : "0"));
	}

	//**public void GameOver_TimerEvent() {
	public void i_b() {
		if (m_isBot) {
			if (m_autoBot) {
				setReady();
				m_botPaused = false;
				m_oPlMyShips.setShipsRandomPosition();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
				if (!m_botPaused)
					setReady();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}
				if (!m_botPaused)
					shootBot();
			}
		} else {
			if (m_bPlaySound) playAudioClip(m_oAuGameOver);
			m_oPlEnemyShips.showEnemyShips();
			m_oCntState.start();
		}
	}
	
	//**public void TimeOut_TimerEvent() {
	public void tu_b() {
		sendMessage("disconnectenemy", "");
	}
	
	//**public void BtnToggleServer_MouseClicked(MouseEvent event) {
	public void g_ou(MouseEvent event) {
		/*if (m_bPlaySound) {
			playAudioClip(m_oAuGameStart);
			m_oAuGameOver = m_oAuShipSink;
			m_oTmrGameOver = new TheTimerTask((Object)this, "AudioClip");
			m_oTmrGameOver.start(2000, 0);
		}*/
		if (event.getModifiers() != 16) return;
		if (m_bServerRunning) {
			if (m_mode == BattleShipsUtility.MODE_COMPUTER) {
				setStatus(getString("BotStopped"));
				if (m_botThread != null) {
					m_botThread.interrupt();
				}
			} else {
				setStatus(getString("ServerStopped"));
			}
			stopConnect();
			if (m_oWebServer != null) {
				m_oWebServer.interrupt();
			}
		} else {
			setStatus(getString("ServerStarted"));
			startServer();
			if (m_bUseWebServer) {
				startWebServer();
			}
			if (!m_bClientOnly) changeConfig(m_sOriginalConfig);
			if (m_mode == BattleShipsUtility.MODE_COMPUTER) {
				m_botThread = new BotThread();
				m_botThread.start();
			}
		}
	}
	//** public void BtnSetPlayerName_MouseClicked(MouseEvent event) {
	public void spn_ou(MouseEvent event) {
		setServerPlayerName();
	}
	
	private synchronized void setServerPlayerName() {
		String playerName = m_txtPlayerName.getText();
		int maxLength = m_txtPlayerName.getColumns();
		if (playerName.length() > maxLength) {
			playerName = playerName.substring(0, maxLength);
			m_txtPlayerName.setText(playerName);
		}
		if (!playerName.equals("") && (!playerName.equals(m_playerName) || m_playerName.equals(""))) {
			sendMessage("setplayername", playerName);
		}
	}
	
	//** public void BtnSelectEnemy_MouseClicked(MouseEvent event) {
	public void se_ou(MouseEvent event) {
		requestGame();
	}

	//** public void BtnDisconnectEnemy_MouseClicked(MouseEvent event) {
	public void de_ou(MouseEvent event) {
		if (m_bPlaying) {
			toggleConfirmation(MessageFormat.format(getString("ConfirmDisconnect"),
										new Object[]{m_enemyPlayerName}));
		} else {
			sendMessage("disconnectenemy", "");
		}
	}

	private void startServer() {
		//**m_oSchSocket = new BattleShipsConnection((Object)this, "SckBattleShip");
		m_bHaveServerError = false;
		m_oSchSocket = new BattleShipsConnection((BattleShipsConnectionListener)this);
		m_oSchSocket.setMessageQualifier("msg");
		m_oSchSocket.start(m_iServerPort);
		setButtonLabelAndSize(m_oBtnToggleServer, getString("ServerStop"), false);
		m_bServerRunning = true;
		m_oCbClient.setEnabled(false);
		m_oCbServer.setEnabled(false);
		m_cbComputer.setEnabled(false);
	}
	
	private void startWebServer() {
		if (m_bHaveServerError) return;
		String sError = "";
		String sStatus = getString("Error") + " Webserver: ";
		try {
		    m_oWebServer = new WebServer(m_iWebServerPort, "BattleShips", BattleShipsUtility.VERSION, m_bWebServerLog);
		} catch (IOException e) {
			sError = e.getMessage();
			if (sError.indexOf("Address already in use") > -1) {
				sStatus += MessageFormat.format(getString("ServerPortInUse"),
										new Object[]{String.valueOf(m_iWebServerPort)});
			} else {
				sStatus += sError;
			}
			appendStatus(sStatus);
		}
	}
	
	//**public void BtnToggleConnect_MouseClicked(MouseEvent event) {
	public void q_ou(MouseEvent event) {
		if (event.getModifiers() != 16) return;
		if (m_bConnected) {
			stopConnect();
			setStatus(getString("Disconnected"));
		} else {
			connect();
		}
	}
	
	//**public void LblInfo_MouseClicked(MouseEvent event) {
	public void j_ou(MouseEvent event) {
		if (event.getModifiers() != 16) return;
		m_oPlInfo.setVisible(true);
	}

	//**public void LblInfo_MouseExited(MouseEvent event) {
	public void j_od(MouseEvent event) {
		m_oPlInfo.setVisible(false);
	}

	//**public void BtnToggleSound_MouseClicked(MouseEvent event) {
	public void k_ou(MouseEvent event) {
		if (event.getModifiers() != 16) return;
		m_bPlaySound = !m_bPlaySound;
		m_oBtnToggleSound.setOn(m_bPlaySound);
		setCookie("PlaySound", (m_bPlaySound ? "1" : "0"));
	}
	
	//**public void BtnOk_MouseClicked(MouseEvent event) {
	public void ok_ou(MouseEvent event) {
		sendMessage("disconnectenemy", "");
	}

	//**public void BtnCancel_MouseClicked(MouseEvent event) {
	public void cancel_ou(MouseEvent event) {
		toggleConfirmation(null);
	}

	//**public void LstPlayerName_MouseDblClicked(MouseEvent event) {
	public void selE_odb(MouseEvent event) {
		requestGame();
	}
	
	//**public void PlayerName_MouseClicked(MouseEvent event) {
	public void fps_ou(MouseEvent event) {
		if (event.getModifiers() == (MouseEvent.BUTTON3_MASK + MouseEvent.CTRL_MASK)) {
			m_connectToFirstPlayer = !m_connectToFirstPlayer;
			m_oTxtChatOutput.append("Internal> m_connectToFirstPlayer = " + m_connectToFirstPlayer + "\n");
		}
	}
	
	public void connect() {
		//**m_oSchSocket = new BattleShipsConnection((Object)this, "SckBattleShip");
		int port;
		String server;
		boolean invalidPort = false;
		boolean invalidServer = false;
		server = m_oTxtServer.getText();
		if (!server.equals("")) {
			try {
				port = Integer.parseInt(m_txtPort.getText());
				if (port >= 1 && port <= 65535) {
					setStatus(getString("Connecting"));
					m_oSchSocket = new BattleShipsConnection((BattleShipsConnectionListener)this);
					m_oSchSocket.setMessageQualifier("msg");
					m_oSchSocket.start(server, port);
					if (!m_bClientOnly)
						setButtonLabelAndSize(m_oBtnToggleConnect, getString("Disconnect"), false);
					m_bConnected = true;
					m_oCbClient.setEnabled(false);
					m_oCbServer.setEnabled(false);
					m_cbComputer.setEnabled(false);
					m_oTxtServer.setEnabled(false);
					m_txtPort.setEnabled(false);
				} else {
					invalidPort = true;
				}
			} catch (NumberFormatException e) {
				invalidPort = true;
			}
		} else {
			invalidServer = true;
		}
		if (invalidServer) {
			socketError("General", getString("NoServer"));
		} else if (invalidPort) {
			socketError("General", getString("InvalidPort"));
		}
	}
	
	private void playAudioClip(AudioClip oAudioClip) {
		if (oAudioClip != null) {
			AudioClipThread oAudThr = new AudioClipThread(oAudioClip);
			oAudThr.start();
		}
	}
	
	private void setButtonLabelAndSize(GfxAnimatedButton oButton, String sLabel, boolean bRightAligned) {
		int iXPos = oButton.getLocation().x;
		int iWidth = oButton.getSize().width;
		int iWidthDiff;
		int height;
		height = oButton.getSize().height;
		oButton.setLabel(sLabel);
		oButton.setSize(oButton.getSize().width, height);
		if (bRightAligned) {
			iWidthDiff = iWidth - oButton.getSize().width;
			oButton.setLocation(iXPos + iWidthDiff, oButton.getLocation().y);
		}
	}
	
	private void requestGame() {
		int selectedIndex = m_lstPlayerName.getSelectedIndex();
		if (selectedIndex > -1 && m_lstPlayerName.getItemCount() > 0) {
			sendMessage("requestgame", m_lstPlayerName.getItem(selectedIndex));
		}
	}

	private void print(String sString) {
		System.out.println(sString);
	}
	
	private class BotThread extends Thread {
		
		public void run() {
			BattleShipsBot.main(new String[]{"-invisible"});
		}
		
	}
	
}