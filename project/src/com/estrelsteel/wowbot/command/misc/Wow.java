package com.estrelsteel.wowbot.command.misc;

import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.user.WowUser;

public class Wow implements Command {

	@Override
	public boolean hasPermission(WowUser user) {
		// TODO: Add permissions
		return true;
	}

	@Override
	public boolean isValid(CommandWrapper wrapper) {
		if (!wrapper.getChannel().isRespondableChannel())
			return false;
		return true;
	}

	@Override
	public boolean execute(CommandWrapper wrapper) {
		boolean bold = false;
		boolean italics = false;
		boolean underline = false;
		boolean caps = false;
		boolean code = false;
		boolean strike = false;

		for (int i = 1; i < wrapper.getArguments().length; i++) {
			if (!bold)
				bold = wrapper.getArguments()[i].equalsIgnoreCase("bold");
			if (!italics)
				italics = wrapper.getArguments()[i].equalsIgnoreCase("italics");
			if (!underline)
				underline = wrapper.getArguments()[i].equalsIgnoreCase("underline");
			if (!caps)
				caps = wrapper.getArguments()[i].equalsIgnoreCase("case");
			if (!code)
				code = wrapper.getArguments()[i].equalsIgnoreCase("code");
			if (!strike)
				strike = wrapper.getArguments()[i].equalsIgnoreCase("strike");
		}

		String wow = wrapper.getArguments()[0];
		if (!caps)
			wow = wow.toUpperCase();
		if (strike)
			wow = "~~" + wow + "~~";
		else if (code)
			wow = "```" + wow + "```";
		else {
			if (bold)
				wow = "**" + wow + "**";
			if (italics)
				wow = "*" + wow + "*";
			if (underline)
				wow = "__" + wow + "__";
		}

		wrapper.getChannel().sendMessage(wow);
		wrapper.deleteCalledMessage();
		return true;
	}

	@Override
	public int getId() {
		return 100;
	}

	@Override
	public String help() {
		return "**Description:** sends wow.";
	}

	@Override
	public String usage() {
		return "**Usage**: " + this + " (case) ((bold) (italics) (underline) | (code) | (strike))";
	}

	@Override
	public String toString() {
		return "wow";
	}

}
