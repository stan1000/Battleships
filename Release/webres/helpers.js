var g_iIntervalID;

function startChange() {
	g_iIntervalID = window.setInterval("changeAppletSize(false)", 500);
}

function changeAppletSize(bInit) {
	var oApplet = document.BattleShipsApplet;
	if (oApplet.isConfigReady() || bInit) {
		var iWidth = parseInt(oApplet.getSize("width"));
		var iHeight = parseInt(oApplet.getSize("height"));
		resizeToInt(iWidth, iHeight);
		if (!bInit) clearInterval(g_iIntervalID);
	}
}

function resizeToInt(iWidth, iHeight) {
	var iDiffWidth;
	var iDiffHeight;

	if (self.outerWidth) {
		iDiffWidth = self.outerWidth - self.innerWidth;
		iDiffHeight = self.outerHeight - self.innerHeight;
	} else {
		iDiffWidth = 10;
		iDiffHeight = 30;
	}
	resizeTo(iWidth + iDiffWidth, iHeight + iDiffHeight);
}

function setCookie(c_name, value, exdays) {
	var exdate=new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value=escape(value) + ((exdays==null) ? '' : '; expires=' + exdate.toUTCString());
	document.cookie=c_name + '=' + c_value;
}

function getCookie(c_name) {
	var oApplet = document.BattleShipsApplet;
	var i,x,y,ARRcookies=document.cookie.split(';');
	for (i=0;i<ARRcookies.length;i++) {
		x=ARRcookies[i].substr(0,ARRcookies[i].indexOf('='));
		y=ARRcookies[i].substr(ARRcookies[i].indexOf('=')+1);
		x=x.replace(/^\s+|\s+$/g,'');
		if (x==c_name) {
			oApplet.setAppCookie(c_name, unescape(y));
		}
	}
}
