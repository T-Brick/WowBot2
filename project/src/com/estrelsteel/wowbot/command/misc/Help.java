package com.estrelsteel.wowbot.command.misc;

import java.util.function.Function;

import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.user.WowUser;

public class Help implements Command {

	private Function<String, Command> lookup;
	
	public Help(Function<String, Command> lookup) {
		this.lookup = lookup;
	}
	
	@Override
	public boolean hasPermission(WowUser user) {
		// TODO: Add permissions
		return true;
	}

	@Override
	public boolean isValid(CommandWrapper wrapper) {
		if(wrapper.getArguments().length < 2)
			return false;
		return true;
	}

	@Override
	public boolean execute(CommandWrapper wrapper) {
		String label = wrapper.getArguments()[1];
		Command cmd = lookup.apply(label);
		if(cmd == null)
			return false;
		String msg = cmd.toString().toUpperCase() + ":";
		msg += "\n\t" + cmd.help();
		msg += "\n\t" + cmd.usage();
		wrapper.getChannel().sendMessage(msg);
		
		return true;
	}

	@Override
	public int getId() {
		return 51;
	}

	@Override
	public String help() {
		return "**Description:** help.";
	}

	@Override
	public String usage() {
		return "**Usage**: " + this + " ";
	}

	@Override
	public String toString() {
		return "help";
	}

}
