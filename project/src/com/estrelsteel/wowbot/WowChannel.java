package com.estrelsteel.wowbot;

import java.util.concurrent.TimeUnit;

import com.estrelsteel.wowbot.user.Console;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class WowChannel {

	private GuildChannel channel;

	public WowChannel() {
		this.channel = null;
	}

	public WowChannel(GuildChannel channel) {
		this.channel = channel;
	}

	public boolean isTextChannel() {
		return channel instanceof TextChannel;
	}

	public boolean isVoiceChannel() {
		return channel instanceof VoiceChannel;
	}

	public boolean isConsole() {
		return channel == null;
	}

	public boolean isRespondableChannel() {
		return isConsole() || isTextChannel();
	}

	public TextChannel getTextChannel() {
		return (TextChannel) channel;
	}

	public VoiceChannel getVoiceChannel() {
		return (VoiceChannel) channel;
	}

	public Guild getGuild() {
		if (isConsole()) {
			Console.logErr("Attempting to access guild of console");
			return null;
		}
		return channel.getGuild();
	}

	public Message sendEmbedMessage(EmbedBuilder build) {
		if (isTextChannel()) {
			MessageEmbed msg = build.build();
			return getTextChannel().sendMessage(msg).complete();
		}

		String message = "";
		for (Field f : build.getFields())
			message = message + "\n" + f.getName() + "\t" + f.getValue();

		if (isConsole()) {
			Console.log("[ EmbedMessage ] ==> " + message);
			return null;
		}
		Console.logErr("Trying to send embed message to voice channel... (" + message + ").");
		return null;
	}

	public MessageEmbed sendEmbedMessageDeleteAfter(EmbedBuilder build, long time) {
		if (isTextChannel()) {
			MessageEmbed msg = build.build();
			getTextChannel().sendMessage(msg).complete().delete().queueAfter(time, TimeUnit.MILLISECONDS);
			return msg;
		}

		String message = build.getDescriptionBuilder().toString();

		if (isConsole()) {
			Console.log("[ Delete after \" + time + \"ms ][ EmbedMessage ] ==> " + message);
			return null;
		}
		Console.logErr("Trying to send embed message to voice channel... (" + message + ").");
		return null;
	}

	public boolean sendMessage(String message) {
		return sendMessage(message, false);
	}
	
	public boolean sendMessage(String message, boolean suppressEmbed) {
		if (isTextChannel()) {
			MessageAction m = getTextChannel().sendMessage(message);
			if(suppressEmbed)
				m.complete().suppressEmbeds(true).queue();
			else
				m.queue();
			
			return true;
		}
		if (isConsole()) {
			Console.log(message);
			return true;
		}
		Console.logErr("Trying to send message to voice channel... (" + message + ").");
		return false;
	}

	public boolean sendMessageDeleteAfter(String message, long time) {
		return sendMessageDeleteAfter(message, time, false);
	}
	
	public boolean sendMessageDeleteAfter(String message, long time, boolean suppressEmbed) {
		if (isTextChannel()) {
			Message m = getTextChannel().sendMessage(message).complete();
			m.delete().queueAfter(time, TimeUnit.MILLISECONDS);
			if(suppressEmbed)
				m.suppressEmbeds(suppressEmbed).queue();
			return true;
		}
		if (isConsole()) {
			Console.log("[ Delete after " + time + "ms ] ==> " + message);
			return true;
		}
		Console.logErr("Trying to send message to voice channel... (" + message + ").");
		return false;
	}

}
