package com.estrelsteel.wowbot.command.audio;

import java.util.List;
import java.util.function.Function;

import com.estrelsteel.wowbot.WowQueueMessage;
import com.estrelsteel.wowbot.audio.WowAudioCore;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.user.WowUser;

public class Queue implements Command {

	private WowAudioCore wac;
	private WowQueueMessage msg;
	private List<String> reacts;

	public Queue(WowAudioCore wac, Function<Integer, List<String>> getReacts) {
		this.wac = wac;
		this.reacts = getReacts.apply(getId());
	}

	@Override
	public boolean hasPermission(WowUser user) {
		// TODO: Add permissions
		return true;
	}

	@Override
	public boolean isValid(CommandWrapper wrapper) {
		if (!wrapper.getChannel().isRespondableChannel())
			return false;
		if (wrapper.getArguments().length <= 0)
			return false;
		return true;
	}

	@Override
	public boolean execute(CommandWrapper wrapper) {
		wrapper.deleteCalledMessage();
		if (!wac.getQueue().isPlaying()) {
			wrapper.getChannel().sendMessageDeleteAfter("The queue is empty...", 10000);
			return true;
		}

		if (msg == null) {
			msg = new WowQueueMessage(wac, wrapper.getChannel(), reacts);
		}
	
		msg.resetMessage();
		return true;
	}

	@Override
	public int getId() {
		return 1030;
	}

	@Override
	public String help() {
		return "**Description:** prints the current audio queue.";
	}

	@Override
	public String usage() {
		return "**Usage**: " + this + "";
	}

	@Override
	public String toString() {
		return "queue";
	}

}
