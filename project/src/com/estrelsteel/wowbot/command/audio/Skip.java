package com.estrelsteel.wowbot.command.audio;

import com.estrelsteel.wowbot.WowChannel;
import com.estrelsteel.wowbot.audio.VoiceHelper;
import com.estrelsteel.wowbot.audio.WowAudioCore;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.user.WowUser;

public class Skip implements Command {

	private WowAudioCore wac;

	public Skip(WowAudioCore wac) {
		this.wac = wac;
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
		WowChannel vc = VoiceHelper.determineChannel(wrapper);
		if (vc == null || !vc.isVoiceChannel()) {
			wrapper.getChannel().sendMessageDeleteAfter("You need to be in a voice channel to use this command.", 5000);
			return false;
		}
		
		if (wrapper.getArguments().length > 1 && wrapper.getArguments()[1].equalsIgnoreCase("all")) {
			wac.getQueue().skipAllTrack();
			wrapper.getChannel().sendMessageDeleteAfter("Skipping all the queued audio tracks.", 15000);
		} else {
			wac.getQueue().skipTrack();
			wrapper.getChannel().sendMessageDeleteAfter("Skipping the current audio track.", 10000);
		}

		return true;
	}

	@Override
	public int getId() {
		return 1020;
	}

	@Override
	public String help() {
		return "**Description:** skips the currently playing audio.";
	}

	@Override
	public String usage() {
		return "**Usage**: " + this + " (all)";
	}

	@Override
	public String toString() {
		return "skip";
	}

}
