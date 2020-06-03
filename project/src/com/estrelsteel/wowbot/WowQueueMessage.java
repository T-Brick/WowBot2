package com.estrelsteel.wowbot;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.estrelsteel.wowbot.audio.WowAudioCore;
import com.estrelsteel.wowbot.audio.WowAudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

public class WowQueueMessage {

	private WowChannel channel;
	private List<String> reacts;

	private Message sentMessage;
	private EmbedBuilder sentBuild;
	private ScheduledExecutorService exe;
	private CompletableFuture<Message> op;
	private WowAudioCore wac;

	public WowQueueMessage(WowAudioCore wac, WowChannel channel, List<String> reacts) {
		this.wac = wac;
		this.channel = channel;
		this.reacts = reacts;
	}

	public long getMessageId() {
		return sentMessage == null ? -1 : sentMessage.getIdLong();
	}

	public String constructMessage() {
		Object[] queue = wac.getQueue().getQueue().toArray();

		String msg = "\t**Currently playing: "
				+ WowAudioCore.getVisibleTitle(wac.getQueue().getPlayer().getPlayingTrack()) + "**";
		if (queue.length == 0) {
			msg = msg + "";
		} else {
			for (int i = 0; i < wac.getQueue().getQueue().size(); i++) {
				msg = msg + "\n" + (i + 1) + ".) " + WowAudioCore.getVisibleTitle(((WowAudioTrack) queue[i]));
				if (msg.toCharArray().length > 900) {
					msg = msg + "\n\nAnd " + (wac.getQueue().getQueue().size() - i) + " more.";
					break;
				}
			}
		}

		return msg;
	}

	private EmbedBuilder generateMessage(EmbedBuilder build) {
		String msg = constructMessage();

		build.getFields().clear();
		build.addField("Queue: ", msg, false);

		if (wac.getQueue().getPlayer().getPlayingTrack() != null) {
			long time = wac.getQueue().getPlayer().getPlayingTrack().getDuration()
					- wac.getQueue().getPlayer().getPlayingTrack().getPosition();
			build.setFooter("Time remaining: " + wac.getStringDuration() + " (" + WowAudioCore.timeToString(time) + ")",
					null);
		} else
			build.setFooter("Time remaining: " + wac.getStringDuration(), null);

		return build;
	}

	public void updateMessage() {
		if (wac.getQueue().getTotalDuration() <= 0 && wac.getQueue().getQueue().size() <= 0) {
			if (exe != null)
				exe.shutdown();
			sentMessage.delete().complete();
			return;
		}

		sentBuild = generateMessage(sentBuild);
		if (op != null)
			op.cancel(true);
		op = sentMessage.editMessage(sentBuild.build()).submit();
	}

	public void resetMessage() {
		sentBuild = generateMessage(new EmbedBuilder().setColor(wac.getQueue().getCurrentColor()));
		if (exe != null)
			exe.shutdownNow();
		if (sentMessage != null) {
			sentMessage.delete().queue();
			sentMessage = null;
		}
		sentMessage = channel.sendEmbedMessage(sentBuild);

		if (sentMessage != null) {
			if (reacts != null) {
				for (String r : reacts) {
					sentMessage.addReaction(r).complete();
				}
			}

			updateMessage();
			exe = Executors.newSingleThreadScheduledExecutor();
			exe.scheduleAtFixedRate(this::updateMessage, 0, 1250, TimeUnit.MILLISECONDS);
		}
	}
}
