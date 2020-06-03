package com.estrelsteel.wowbot.audio;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import com.estrelsteel.wowbot.WowChannel;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class AudioQueue extends AudioEventAdapter {

	private AudioPlayer p;
	private LinkedList<WowAudioTrack> queue;
	private WowAudioTrack currentlyPlaying;
	private WowChannel text;

	public AudioQueue(AudioPlayer p) {
		p.addListener(this);
		this.queue = new LinkedList<WowAudioTrack>();
		this.p = p;
		this.text = null;
	}

	public boolean isPlaying() {
		return currentlyPlaying != null;
	}

	public void skipTrack() {
		nextTrack();
	}

	public void skipAllTrack() {
		queue.clear();
		nextTrack();
	}

	public AudioPlayer getPlayer() {
		return p;
	}

	public Queue<WowAudioTrack> getQueue() {
		return queue;
	}

	public WowChannel getTextChannel() {
		return text;
	}

	public static Color getTrackColor(String title) {
		if (title.length() < 3)
			return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
		int r = 0;
		int g = 0;
		int b = 0;
		for (int i = 0; i < title.length() - 3; i = i + 3) {
			r = r + title.charAt(i);
			g = g + title.charAt(i + 1);
			b = b + title.charAt(i + 2);
		}
		return new Color(r % 256, g % 256, b % 256);
	}

	public Color getCurrentColor() {
		if (currentlyPlaying == null)
			return null;
		return getTrackColor(currentlyPlaying.getVisibleTitle());
	}
	
	public String getCurrentTitle() {
		if(currentlyPlaying == null)
			return null;
		return currentlyPlaying.getTrack().getInfo().title;
	}
	
	public String getCurrentLink() {
		if(currentlyPlaying == null)
			return null;
		String uri = currentlyPlaying.getTrack().getInfo().uri;
		if(uri == null || !uri.startsWith("http"))
			return null;
		return uri;
	}
	
	public long getStartTime() {
		if(currentlyPlaying == null)
			return -1;
		return currentlyPlaying.getStartTime();
	}
	
	public long getEndTime() {
		if(currentlyPlaying == null)
			return -1;
		return currentlyPlaying.getEndTime();
	}

	public void queue(WowAudioTrack track) {
		if (!p.startTrack(track.getTrack(), true))
			queue.add(track);
		else
			currentlyPlaying = track.startTimedDequeuer();
	}
	
	public void queueNext(WowAudioTrack track) {
		if (!p.startTrack(track.getTrack(), true))
			queue.addFirst(track);
		else
			currentlyPlaying = track.startTimedDequeuer();
	}

	public void nextTrack() {
		if (!queue.isEmpty()) {
			currentlyPlaying = queue.poll().startTimedDequeuer();
			p.startTrack(currentlyPlaying.getTrack(), false);
		} else {
			p.startTrack(null, false);
			currentlyPlaying = null;
		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext)
			nextTrack();
	}

	public long getTotalDuration() {
		long d = -1l;
		if (p.getPlayingTrack() != null) {
			d = currentlyPlaying.getTimeRemaining(currentlyPlaying.getTrack().getPosition());
			Object[] q = queue.toArray();
			for (int i = 0; i < queue.size(); i++) {
				d = d + ((WowAudioTrack) q[i]).getDuration();
			}
		}
		return d;
	}

	public AudioQueue setTextChannel(WowChannel text) {
		this.text = text;
		return this;
	}
}
