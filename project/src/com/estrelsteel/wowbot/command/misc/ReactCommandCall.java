package com.estrelsteel.wowbot.command.misc;

import java.util.List;

import com.estrelsteel.wowbot.BotSettings;
import com.estrelsteel.wowbot.WowChannel;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.command.CommandWrapper.CommandStatus;
import com.estrelsteel.wowbot.executer.Executer;
import com.estrelsteel.wowbot.user.WowUser;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReactCommandCall implements Command {

	private String[] command;
	private Executer exe;

	public ReactCommandCall(Executer exe, String[] command) {
		this.command = command;
		this.exe = exe;

		if (command.length >= 1) {
			command[0] = exe.getParser().addTrigger(command[0]);
		}
	}

	public static boolean hasBotReact(MessageReaction r) {
		List<User> users = r.retrieveUsers().complete();
		for (User u : users) {
			if (u.isBot() && u.getIdLong() == BotSettings.id) {
				return true;
			}
		}
		return false;
	}

	public static CommandWrapper constructWrapper(MessageReactionAddEvent e) {
		if (e == null)
			return null;
		WowUser user = new WowUser(e.getUserIdLong());
		WowChannel channel = new WowChannel((GuildChannel) e.getChannel());
		CommandWrapper wrap = new CommandWrapper(user, null, channel, null);
		if (!hasBotReact(e.getReaction()))
			wrap.setStatus(CommandStatus.INVALID);
		return wrap;
	}

	@Override
	public boolean isExecuteable() {
		return false;
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public boolean hasPermission(WowUser user) {
		// TODO: Add permissions
		return true;
	}

	@Override
	public boolean isValid(CommandWrapper wrapper) {
		if (wrapper.getStatus() != CommandStatus.VALID)
			return false;
		return true;
	}

	@Override
	public boolean execute(CommandWrapper wrapper) {
		CommandWrapper newWrap = new CommandWrapper(wrapper.getUser(), command, wrapper.getChannel(), null);
		return exe.executePotentialCommand(newWrap);
	}

	@Override
	public int getId() {
		return 1;
	}

	@Override
	public String help() {
		return "INTERNAL USE ONLY";
	}

	@Override
	public String usage() {
		return "INTERNAL USE ONLY";
	}

	@Override
	public String toString() {
		return "ReactCommandCall -> " + command;
	}
}
