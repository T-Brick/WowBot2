package com.estrelsteel.wowbot.user;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Console extends WowUser {

	public static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");
	public static final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' kk:mm:ss");

	public Console() {
		super(-1);
	}

	public static void log(String message) {
		System.out.println("[" + df.format(Calendar.getInstance().getTime()).toString() + "] " + message);
	}

	public static void logErr(String message) {
		System.err.println("[ERROR][" + df.format(Calendar.getInstance().getTime()).toString() + "] " + message);
	}

}
