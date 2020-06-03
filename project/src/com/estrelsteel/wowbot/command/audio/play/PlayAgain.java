package com.estrelsteel.wowbot.command.audio.play;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.WowChannel;
import com.estrelsteel.wowbot.audio.VoiceHelper;
import com.estrelsteel.wowbot.audio.WowAudioCore;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.user.WowUser;

public class PlayAgain implements Command {

	private WowAudioCore wac;
	
	public PlayAgain(WowAudioCore wac) {
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
		WowChannel vc = VoiceHelper.determineChannel(wrapper);
		if (vc == null || !vc.isVoiceChannel()) {
			wrapper.getChannel().sendMessageDeleteAfter("You need to be in a voice channel to use this command.", 5000);
			return false;
		}
		
		wrapper.deleteCalledMessage();
		
		if(!wac.getQueue().isPlaying()) {
			wrapper.getChannel().sendMessageDeleteAfter("Nothing is playing.", 5000);
			return false;
		}
		
		String arg = wac.getQueue().getCurrentLink();
		arg = arg + " " + wac.getQueue().getStartTime();
		arg = arg + " " + wac.getQueue().getEndTime();
		
		
		CommandWrapper newWrapper = wrapper.convert(WowBot.getCommandId(PlayNext.class), arg);
		WowBot.runCommand(newWrapper);
		
		return true;
	}

	@Override
	public int getId() {
		return 1060;
	}

	@Override
	public String help() {
		return "**Description:** adds the currently playing audio track to the front of the queue.";
	}

	@Override
	public String usage() {
		return "**Usage**: " + this + "";
	}

	@Override
	public String toString() {
		return "repeat";
	}

}
