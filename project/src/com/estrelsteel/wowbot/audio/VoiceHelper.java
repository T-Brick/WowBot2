package com.estrelsteel.wowbot.audio;

import com.estrelsteel.wowbot.WowChannel;
import com.estrelsteel.wowbot.command.CommandWrapper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;

public class VoiceHelper {

	public static void disconnect(AudioManager am) {
		if (am.isConnected())
			am.closeAudioConnection();
	}

	public static void disconnect(Guild g) {
		if (g.getAudioManager() != null)
			disconnect(g.getAudioManager());
	}

	public static void disconnect(WowChannel c) {
		disconnect(c.getGuild());
	}

	public static void disconnect(JDA jda) {
		for (Guild g : jda.getGuilds())
			disconnect(g);
	}

	public static WowChannel determineChannel(Guild g, long userid) {
		return new WowChannel(g.getMemberById(userid).getVoiceState().getChannel());
	}

	public static WowChannel determineChannel(CommandWrapper cw) {
		if (cw.getChannel().isConsole())
			return null;
		return determineChannel(cw.getChannel().getGuild(), cw.getUser().getId());
	}

}
