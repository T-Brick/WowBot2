package com.estrelsteel.wowbot.user;

import com.estrelsteel.wowbot.BotSettings;

public class WowUser {

	private long id;

	public WowUser(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public boolean isConsole() {
		return id < 0;
	}

	public boolean isThisBot() {
		return id == BotSettings.id;
	}
}
