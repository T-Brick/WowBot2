package com.estrelsteel.wowbot.executer;

import com.estrelsteel.wowbot.WowBot;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.command.audio.Leave;
import com.estrelsteel.wowbot.command.misc.ReactCommandCall;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {

	private Executer exe;

	public EventListener(Executer exe) {
		this.exe = exe;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (e.getMessage().getAuthor().getIdLong() != e.getJDA().getSelfUser().getIdLong()) {
			exe.executePotentialCommand(exe.getParser().parse(e.getMessage().getContentRaw(), e));
		}
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getUserIdLong() != e.getJDA().getSelfUser().getIdLong()) {
			if (exe.executePotentialReactCommand(e.getReaction(), ReactCommandCall.constructWrapper(e))) {
				e.getReaction().removeReaction(e.getUser()).complete();
			}
		}
	}
	
	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		if(e.getChannelLeft().getMembers().size() == 1) {
			if(e.getChannelLeft().getMembers().get(0).getIdLong() == e.getJDA().getSelfUser().getIdLong()) {
				CommandWrapper wrapper = new CommandWrapper(WowBot.getCommandId(Leave.class), "");
				WowBot.runCommand(wrapper);
			}
		}
	}
}
