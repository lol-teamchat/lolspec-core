package com.DiscordLeagueBot.Listeners;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class TrackScheduler implements AudioEventListener{
	AudioPlayer audioplayer;
	public TrackScheduler(AudioPlayer player){
		audioplayer = player;
	}
	public void onEvent(AudioEvent event){
		return;
	}
	public void queue(AudioTrack track) {
		// TODO Auto-generated method stub
		
	}
}