package com.estrelsteel.wowbot.audio;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import com.estrelsteel.wowbot.WowChannel;
import com.estrelsteel.wowbot.user.Console;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.managers.AudioManager;

public class WowAudioCore {

	private AudioPlayerManager apm;
	private AudioPlayer player;
	private AudioQueue queue;

	private WowChannel c;

	public WowAudioCore() {
		apm = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerLocalSource(apm);
		AudioSourceManagers.registerRemoteSources(apm);
		player = apm.createPlayer();
		queue = new AudioQueue(player);
	}

	public static String getVisibleTitle(WowAudioTrack track) {
		return getVisibleTitle(track.getTrack());
	}

	public static String getVisibleTitle(AudioTrack track) {
		if (track == null) {
			return "Loading...";
		}
		String title = track.getInfo().title;
		if (title.equalsIgnoreCase("Unknown title")) {
			title = new File(track.getInfo().uri).getName();
			title = title.split(".wav")[0];
			title = title.replaceAll("_", " ");
			String[] title_words = title.split(" ");
			title = "";
			for (int i = 0; i < title_words.length; i++) {
				if (title_words[i].length() > 1) {
					title = title + title_words[i].substring(0, 1).toUpperCase();
					title = title + title_words[i].substring(1) + " ";
				} else {
					title = title + title_words[i].toUpperCase() + " ";
				}
			}
			title = title.trim();
		}
		return title;
	}

	public static String timeToString(long dur) {
		int d = (int) (dur / 1000);
		String time = "";
		String h = (d / 3600) + "";
		String m = (d % 3600 / 60) + "";
		String s = (d % 3600 % 60) + "";
		if (m.length() < 2) {
			m = "0" + m;
		}
		if (s.length() < 2) {
			s = "0" + s;
		}
		if (d / 3600 == 0) {
			if (d % 3600 / 60 == 0) {
				return time + s + " seconds";
			}
			return time + m + ":" + s;
		}
		return time + h + ":" + m + ":" + s;
	}

	public String getStringDuration() {
		return timeToString(queue.getTotalDuration());
	}

	public String getUserStringDuration() {
		String msg = getStringDuration();
		if (msg.equals("00"))
			return "now";
		return "in " + msg;
	}

	public AudioQueue getQueue() {
		return queue;
	}

	public AudioManager switchVoiceChannel(WowChannel nc, boolean summon) {
		if (nc == null || !nc.isVoiceChannel()) {
			Console.logErr("Attempted to join null channel.");
			return null;
		}
		if (summon || player.getPlayingTrack() == null)
			c = nc;
		if (c == null)
			c = nc;

		AudioManager am = c.getGuild().getAudioManager();
		am.openAudioConnection(c.getVoiceChannel());
		return am;
	}

	public void loadTrack(String file, AudioManager am, boolean playNext) {
		loadTrack(file, am, 0, -1, playNext);
	}

	public boolean loadTrack(String file, AudioManager am, long start, long end, boolean playNext) {
		if (file == null) {
			Console.logErr("Requested track link is null.");
			return false;
		}
		if (am == null)
			return false;
		
		final CountDownLatch latch = new CountDownLatch(1);
		
		am.setSendingHandler(new AudioPlayerSendHandler(player));
		apm.loadItem(file, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				WowAudioTrack wowtrack = (end >= 0) ? new WowAudioTrack(track, end - start, start)
						: new WowAudioTrack(track);
				
				if(playNext)
					queue.queueNext(wowtrack);
				else
					queue.queue(wowtrack);
				
				latch.countDown();
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				WowAudioTrack track;
				for (int i = 0; i < playlist.getTracks().size(); i++) {
					track = new WowAudioTrack(playlist.getTracks().get(i));
					if(playNext)
						queue.queueNext(track);
					else
						queue.queue(track);
				}
				
				latch.countDown();
			}

			@Override
			public void noMatches() {
				Console.log("Requested a track that doesn't exist.");
				
				latch.countDown();
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				exception.printStackTrace();
				
				latch.countDown();
			}
		});
		
		try {
			latch.await();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void disconnectFromVoice() {
		queue.skipAllTrack();
		c.getGuild().getAudioManager().closeAudioConnection();
		c = null;
	}
}
