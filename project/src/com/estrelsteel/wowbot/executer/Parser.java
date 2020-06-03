package com.estrelsteel.wowbot.executer;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import com.estrelsteel.wowbot.WowChannel;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.user.WowUser;

public class Parser {

	private String trigger;

	public Parser(String trigger) {
		this.trigger = trigger;
	}

	public CommandWrapper parse(String message) {
		return parse(message, null);
	}

	public CommandWrapper parse(String message, MessageReceivedEvent e) {
		if (!message.startsWith(trigger))
			return null;

		String[] args = message.split(" ");
		args[0] = args[0].substring(trigger.length());

		long id = e == null ? -1 : e.getAuthor().getIdLong();
		WowChannel channel = e == null ? new WowChannel() : new WowChannel((GuildChannel) e.getChannel());
		Message msg = e == null ? null : e.getMessage();

		return new CommandWrapper(new WowUser(id), args, channel, msg);
	}

	public String addTrigger(String msg) {
		return trigger + msg;
	}
}
