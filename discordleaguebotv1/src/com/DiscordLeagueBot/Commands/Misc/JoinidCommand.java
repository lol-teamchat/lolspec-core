package com.DiscordLeagueBot.Commands.Misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.DiscordLeagueBot.DiscordLeagueBot;
import com.DiscordLeagueBot.Listeners.*;
import com.DiscordLeagueBot.Commands.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.entities.VoiceChannel;


public class JoinidCommand implements Command {
	
	public VoiceChannel findCorrectChannel(String g) throws IOException{
        int j = 0;
        int [] arr = new int [DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().size()+1];
        for (int e : arr){
        	arr[e]=0;
        } 
        
       // will use serversettings instead of "names" file in the future for better functionality
       // String [] names = DiscordLeagueBot.serverSettings.get(DiscordLeagueBot.api.getGuildById(g).getId()).names;
        
		FileReader fr = new FileReader("names");
        BufferedReader br = new BufferedReader(fr);
        String name;
        while((name = br.readLine()) != null){
        	j = 0;
	        for (VoiceChannel i : DiscordLeagueBot.api.getGuildById(g).getVoiceChannels()){
	        	for (net.dv8tion.jda.core.entities.Member k : i.getMembers()){
		        	if(k.getUser().getName().equals(name)){
			        	arr[j]++;
		        	}
	        	}
	        	j++;
	        }
        }
        br.close();
        fr.close();
		
        VoiceChannel vc = DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().get(0);
        int biggest = arr[0];
        for(int i = 0; i < DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().size()+1; i++){
        	if(arr[i] > biggest){
        		vc = DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().get(i);
        		biggest = arr[i];
        	}
        }
        System.out.println(vc.getName() + " has the most matching members as on website team");
        return vc;
	}
    	
    
 public void action(String[] args) {
    	VoiceChannel vc = null;
		try {
			vc = findCorrectChannel(args[0]);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		DiscordLeagueBot.joinVoiceChannel(vc,true);
		AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioPlayer player = playerManager.createPlayer();
		TrackScheduler trackscheduler = new TrackScheduler(player);
		player.addListener(trackscheduler);
		playerManager.loadItem("http://xd.ddnsking.com/lolspec/lolspec-core/core-server/recording/intro_mp3/JannaIntro.mp3", new AudioLoadResultHandler() {
			  @Override
			  public void trackLoaded(AudioTrack track) {
				  player.playTrack(track);
			  }

			  @Override
			  public void playlistLoaded(AudioPlaylist playlist) {
			  }

			  @Override
			  public void noMatches() {
			    // Notify the user that we've got nothing
			  }

			@Override
			public void loadFailed(FriendlyException exception) {
				// TODO Auto-generated method stub
				
			}
			});
		
		
		AudioPlayerSendHandler asl = new AudioPlayerSendHandler(player);
		DiscordLeagueBot.api.getGuildById(args[0]).getAudioManager().setSendingHandler(asl);
		AudioReceiveListener ah = (AudioReceiveListener) DiscordLeagueBot.api.getGuildById(args[0]).getAudioManager().getReceiveHandler();
		UserSpeakingListener usl = new UserSpeakingListener(vc, ah.rand);
		DiscordLeagueBot.api.getGuildById(args[0]).getAudioManager().setConnectionListener(usl);
    }
	@Override
	public String usage() {
		return null;
	}

	@Override
	public String descripition() {
		return "Joins the guild ID in args[0]";
	}

	@Override
	public Boolean called(String[] args) {
		return true;
	}

	@Override
	public void executed(boolean success) {
		System.out.println("Ended current process");
	}
}