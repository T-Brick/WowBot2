package com.estrelsteel.wowbot.command.audio.play;

import com.estrelsteel.wowbot.audio.WowAudioCore;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.user.WowUser;

public class Play implements Command {

	private WowAudioCore wac;
	
	public Play(WowAudioCore wac) {
		this.wac = wac;
	}

	@Override
	public boolean hasPermission(WowUser user) {
		// TODO: Add permissions
		return true;
	}

	@Override
	public boolean isValid(CommandWrapper wrapper) {
		return PlayHelper.isValid(wrapper);
	}

	@Override
	public boolean execute(CommandWrapper wrapper) {
		return PlayHelper.execute(wrapper, wac, false);
	}

	@Override
	public int getId() {
		return 1000;
	}

	@Override
	public String help() {
		return "**Description:** plays audio in the user's voice channel.";
	}

	@Override
	public String usage() {
		return "**Usage**: " + this + " <url> (start) (end)";
	}

	@Override
	public String toString() {
		return "play";
	}

}
