package com.estrelsteel.wowbot.audio;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class WowAudioTrack {

	private AudioTrack track;
	private long duration;
	private long start;
	private ScheduledExecutorService exe;

	public WowAudioTrack(AudioTrack track, long duration, long start) {
		track.setPosition(start);

		this.track = track;
		this.duration = duration;
		this.start = start;
		this.exe = null;
		if (hasEndTime())
			this.exe = Executors.newSingleThreadScheduledExecutor();
	}

	public WowAudioTrack(AudioTrack track) {
		this(track, -1, 0);
	}

	public String getVisibleTitle() {
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

	public AudioTrack getTrack() {
		return track;
	}

	public long getTimeRemaining(long pos) {
		if (hasEndTime())
			return duration - pos;
		return track.getDuration() - pos;
	}

	public long getDuration() {
		if (hasEndTime())
			return duration;
		return track.getDuration();
	}

	public boolean hasEndTime() {
		return duration >= 0;
	}
	
	public long getStartTime() {
		return start;
	}
	
	public long getEndTime() {
		if(hasEndTime())
			return duration - start;
		return -1;
	}
	
	public void dequeue() {
		track.setPosition(track.getDuration());
	}

	public WowAudioTrack startTimedDequeuer() {
		if (!hasEndTime())
			return this;
		exe.schedule(this::dequeue, duration, TimeUnit.MILLISECONDS);
		return this;
	}
}
