package com.estrelsteel.wowbot.command.audio.play;

import java.util.ArrayList;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.WowChannel;
import com.estrelsteel.wowbot.audio.VoiceHelper;
import com.estrelsteel.wowbot.audio.WowAudioCore;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.command.audio.Queue;

public class PlayHelper {

	public static ArrayList<String> validAddress;

	protected static boolean safeLink(String link) {
		if (link.startsWith("http")) {
			for (String s : validAddress) {
				if (link.startsWith(s)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	protected static long getStartTime(String[] args) {
		if (args.length >= 3)
			return Long.parseLong(args[2]);
		return 0;
	}
	
	protected static long getEndTime(String[] args) {
		if (args.length >= 4)
			return Long.parseLong(args[3]);
		return -1;
	}
	
	protected static boolean execute(CommandWrapper wrapper, WowAudioCore wac, boolean playNext) {
		WowChannel vc = VoiceHelper.determineChannel(wrapper);
		
		wrapper.deleteCalledMessage();
		
		if (vc == null || !vc.isVoiceChannel()) {
			wrapper.getChannel().sendMessageDeleteAfter("You need to be in a voice channel to use this command.", 5000);
			return false;
		}
		
		if (safeLink(wrapper.getArguments()[1])) {
			long start, end;
			try {
				start = getStartTime(wrapper.getArguments());
				end = getEndTime(wrapper.getArguments());
			} catch(NumberFormatException e) {
				wrapper.getChannel().sendMessageDeleteAfter("Could not parse timestamps.", 5000);
				return false;
			}
			 
			wac.loadTrack(wrapper.getArguments()[1], wac.switchVoiceChannel(vc, false), start, end, playNext);
			
			try {
				CommandWrapper newWrapper = wrapper.convert(WowBot.getCommandId(Queue.class));
				WowBot.runCommand(newWrapper);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

		} else {
			wrapper.getChannel().sendMessageDeleteAfter("Could not parse URL.", 5000);
			return false;
		}
		return true;
	}
	
	protected static boolean isValid(CommandWrapper wrapper) {
		if (!wrapper.getChannel().isRespondableChannel())
			return false;
		if (wrapper.getArguments().length <= 1)
			return false;
		return true;
	}
}
