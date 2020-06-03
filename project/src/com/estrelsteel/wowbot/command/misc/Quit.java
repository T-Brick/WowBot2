package com.estrelsteel.wowbot.command.misc;

import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.user.WowUser;

public class Quit implements Command {

	@Override
	public boolean hasPermission(WowUser user) {
		// TODO: Add permissions
		return true;
	}

	@Override
	public boolean isValid(CommandWrapper wrapper) {
		return true;
	}

	@Override
	public boolean execute(CommandWrapper wrapper) {
		wrapper.deleteCalledMessage();
		wrapper.getChannel().sendMessageDeleteAfter("Shutting down...", 30000);

		System.exit(0);
		return true;
	}

	@Override
	public int getId() {
		return 50;
	}

	@Override
	public String help() {
		return "**Description:** stops the bot.";
	}

	@Override
	public String usage() {
		return "**Usage**: " + this + " ";
	}

	@Override
	public String toString() {
		return "quit";
	}

}
