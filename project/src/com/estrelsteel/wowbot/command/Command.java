package com.estrelsteel.wowbot.command;

import com.estrelsteel.wowbot.user.WowUser;

public interface Command {

	public boolean hasPermission(WowUser user);

	public boolean isValid(CommandWrapper wrapper);

	public boolean execute(CommandWrapper wrapper);

	public int getId();

	public String help();

	public String usage();

	@Override
	public String toString();

	default boolean isExecuteable() {
		return true;
	}

	default boolean isDeprecated() {
		return false;
	}

	default boolean isVisible() {
		return true;
	}
}
