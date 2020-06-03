package com.estrelsteel.wowbot.command;

import java.util.concurrent.TimeUnit;

import com.estrelsteel.wowbot.WowChannel;
import com.estrelsteel.wowbot.user.Console;
import com.estrelsteel.wowbot.user.WowUser;

import net.dv8tion.jda.api.entities.Message;

public class CommandWrapper {

	public enum CommandStatus {
		VALID, INVALID, MISSING_TRIGGER
	}

	private WowUser user;
	private String[] args;
	private WowChannel channel;
	private Message message;
	private CommandStatus status;
	
	public CommandWrapper(Integer commandId, String message) {
		this.user = new Console();
		this.channel = new WowChannel();
		this.message = null;
		CommandWrapper temp = convert(commandId, message);
		this.args = temp.args;
		this.status = temp.status;
	}

	public CommandWrapper(WowUser user, String[] args, WowChannel channel, Message message) {
		this.user = user;
		this.args = args;
		this.channel = channel;
		this.message = message;
		this.status = CommandStatus.VALID;
	}

	public WowUser getUser() {
		return user;
	}

	public String[] getArguments() {
		return args;
	}

	public WowChannel getChannel() {
		return channel;
	}

	public Message getMessage() {
		return message;
	}

	public CommandStatus getStatus() {
		return status;
	}

	public void deleteCalledMessage() {
		if (message != null)
			message.delete().queue();
		message = null;
	}

	public void deleteCalledMessage(long delay) {
		if (message != null)
			message.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
		message = null;
	}

	public CommandWrapper convert(Integer commandId) throws CloneNotSupportedException {
		return convert(commandId, new String[0]);
	}
	
	public CommandWrapper convert(Integer commandId, String arg) {
		return convert(commandId, arg.split(" "));
	}
	
	public CommandWrapper convert(Integer commandId, String[] in_args) {
		int n = in_args.length;
		String[] nArgs = new String[n + 1];
		nArgs[0] = commandId.toString();
		for(int i = 0; i < n; i++) nArgs[i + 1] = in_args[i];
		
		CommandWrapper wrapper = new CommandWrapper(user, nArgs, channel, message);
		wrapper.setStatus(CommandStatus.MISSING_TRIGGER);
		
		return wrapper;
	}

	public void setStatus(CommandStatus status) {
		this.status = status;
	}
}
