package com.DiscordLeagueBot.Commands.Misc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;

import com.DiscordLeagueBot.DiscordLeagueBot;
import com.DiscordLeagueBot.Listeners.*;
import com.DiscordLeagueBot.Commands.Command;
import com.DiscordLeagueBot.Configuration.ServerSettings;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;


public class JoinidCommand implements Command {

 	@Override
 	public void action(String[] args){
 		
 		//convert summoner id to discord id via our SQL database
 		String discordid = summ_to_disc(args[0]);
 		
 		//determine which voicechannel the summoner is in
 		if (discordid.equals("no such user")){
 			return;
 		}
 		
 		VoiceChannel vc = locateUser(discordid);
 		
 		//join that voicechannel
 		if (vc == null){
 			return;
 		}
		DiscordLeagueBot.joinVoiceChannel(vc,true,args[1]);
		
		//say janna's intro to alert players she is recording
		greetUsers(vc);
		
 	}
    
	private String summ_to_disc(String summid) {
		
		FileReader fr;
        String url = null;
        String user = null;
        String password = null;
        User curr = null;
        String discid = null;
        String discname = null;
        String discnum = null;
        
		try {
			fr = new FileReader("secret");
	        BufferedReader br = new BufferedReader(fr);

			url = br.readLine();
	        user = br.readLine();
	        password = br.readLine();
	        
	        br.close();
	        fr.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
        System.out.println("Connecting to database...");

        //tries to find a discord id matching that summoner id. if it cant, then look for discord name / discriminator instead.
        try (java.sql.Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Database connected!");
            PreparedStatement ps1 = connection.prepareStatement("SELECT * FROM `players` WHERE `players`.`summoner_id` = ?");
            ps1.setString(1,summid);
            ResultSet rs = ps1.executeQuery();
            if(rs.next()){
            discid = rs.getString(4);
            discname = rs.getString(5);
            discnum = rs.getString(6);
            System.out.println("got past first query");
            }
            if (discid != null){
            	return discid;
            }
            
            else{           	
            	
            	for (Guild g : DiscordLeagueBot.api.getGuilds()){
	            	 PreparedStatement ps = connection.prepareStatement("UPDATE players SET discord_id = ? WHERE discord_display_name = ? AND discord_discriminator = ?");
	                 Iterator<Member> iter = g.getMembers().iterator();
	                 //while there is another guild, look for that member in it
	                 while (iter.hasNext()){
	                 	curr = iter.next().getUser();
	                 	if (curr.getName().equalsIgnoreCase(discname)){
	                 		if (curr.getDiscriminator().equals(discnum)){
	                 			ps.setString(1,curr.getId());
	                 			ps.setString(2,curr.getName());
	                 			ps.setString(3,curr.getDiscriminator());
	                 			 System.out.println("got past 2 query");
	                 			ps.executeUpdate();
	                 			 System.out.println("got past 3 query");
	                 		    ps.close();
	                 		    
	                 		    //overwrite the old key if it already exists
	                 		    if (DiscordLeagueBot.serverSettings.containsKey(curr.getId())){
	                 		    	DiscordLeagueBot.serverSettings.replace(curr.getId(), new ServerSettings(g));
	                 		    	DiscordLeagueBot.writeUserGuildsJson();
	                 		    }
	                 		    
	                 		    //make a new key if not
	                 		    else{
		                 		    DiscordLeagueBot.serverSettings.put(curr.getId(), new ServerSettings(g));
		                 	        DiscordLeagueBot.writeUserGuildsJson();
	                 		    }
	                 		    
		                 	    return curr.getId();
	                 		}
	                 	}
	                 }
            	}
            }
            
            
        } catch (java.sql.SQLException e1) {
            throw new IllegalStateException("Cannot connect the database!", e1);
        }
        
        return "no such user";
	}

	public VoiceChannel locateUser(String discid){
		
		VoiceChannel vc = null;

        //looks to see if the user is in the guild that they were in last time
        if(DiscordLeagueBot.serverSettings.containsKey(discid)){
	        for (VoiceChannel commonVc : DiscordLeagueBot.api.getGuildById(DiscordLeagueBot.serverSettings.get(discid).lastGuildId).getVoiceChannels()){
	        	for (Member mem : commonVc.getMembers()){
	        		if (mem.getUser().getId().equals(discid)){
	        			return commonVc;
	        		}        		
	        	}        	
	        }
        }
        
        //Goes here if they did not have a default guild, assigns one and joins it
        for (Guild g : DiscordLeagueBot.api.getGuilds()){
            for (VoiceChannel newVc :g.getVoiceChannels()){
	        	for (Member mem : newVc.getMembers()){
	        		if (mem.getUser().getId().equals(discid)){
	        			
	        			//Makes this guild the new default guild for that id
	        			if (DiscordLeagueBot.serverSettings.containsKey(discid)){
	        				DiscordLeagueBot.serverSettings.replace(discid, new ServerSettings(g));
	        				DiscordLeagueBot.writeUserGuildsJson();
	        			}
           		    
	        			//make a new key if they are not already in the settings somehow (would only happen if
	        			//they added the bot to their server while it was offline
	           		    else{
		           		    DiscordLeagueBot.serverSettings.put(discid, new ServerSettings(g));
		           	        DiscordLeagueBot.writeUserGuildsJson();
	           		    }
	        			return newVc;
	        		}        		
	        	}        	
	        }	
        }

		System.err.println("user was not in a voice channel when they joined");
		return vc;
	}
	
	public void greetUsers(VoiceChannel vc){
		
		AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioPlayer player = playerManager.createPlayer();
		TrackScheduler trackscheduler = new TrackScheduler(player);
		player.addListener(trackscheduler);
		playerManager.loadItem("http://teamchat.lol/core-server/recording/intro_mp3/JannaIntro.mp3", new AudioLoadResultHandler() {
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
	DiscordLeagueBot.api.getGuildById(vc.getGuild().getId()).getAudioManager().setSendingHandler(asl);
	AudioReceiveListener ah = (AudioReceiveListener) DiscordLeagueBot.api.getGuildById(vc.getGuild().getId()).getAudioManager().getReceiveHandler();
	UserSpeakingListener usl = new UserSpeakingListener(vc, ah.saveloc);
	DiscordLeagueBot.api.getGuildById(vc.getGuild().getId()).getAudioManager().setConnectionListener(usl);
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