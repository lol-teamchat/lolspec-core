package com.DiscordLeagueBot.Commands.Misc;

import com.DiscordLeagueBot.Commands.Command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.time.OffsetDateTime;

import com.DiscordLeagueBot.DiscordLeagueBot;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class JoinCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 0) {
            DiscordLeagueBot.sendMessage(e.getChannel(), DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).prefix + usage());
            return;
        }

        if (e.getGuild().getAudioManager().getConnectedChannel() != null &&
                e.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(e.getMember())) {
            DiscordLeagueBot.sendMessage(e.getChannel(), "I am already in your channel!");
            return;
        }

        if (e.getMember().getVoiceState().getChannel() == null) {
            DiscordLeagueBot.sendMessage(e.getChannel(), "You need to be in a voice channel to use this command!");
            return;
        }

        //write out previous channel's audio if autoSave is on
        if (e.getGuild().getAudioManager().isConnected() && DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).autoSave)
            DiscordLeagueBot.writeToFile(e.getGuild());

        System.out.println(e.getMember().getVoiceState().getChannel());
        DiscordLeagueBot.joinVoiceChannel(e.getMember().getVoiceState().getChannel(), true);
        
        File dest = null;
        try {

            if (new File("C:/Users/Evan Green/Desktop/recording/").exists())
                dest = new File("C:/Users/Evan Green/Desktop/recording/timestamp" + ".txt");
            else
                dest = new File("C:/Users/Evan Green/Desktop/recording/timestamp" + ".txt");
            
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        try(  PrintWriter outfile = new PrintWriter(dest)  ){
            outfile.println(e.getGuild());
            outfile.println( OffsetDateTime.now());
        }
        
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    	
    	
    	
    	
    	/*
    	String url = "GET/api/channels/{" + channel + "}";
    	URLConnection connection = new URL(url + "?" + query).openConnection();
    	connection.setRequestProperty("Accept-Charset", charset);
    	InputStream response = connection.getInputStream();
    	// ...
    	GetMethod get = new GetMethod("http://httpcomponents.apache.org");
    	// execute method and handle any error responses.
    	...
    	InputStream in = get.getResponseBodyAsStream();
    	// Process the data from the input stream.
    	get.releaseConnection();
    	GET/api/channels/{channel}
    	VoiceChannel vc = new VoiceChannel;
        DiscordLeagueBot.joinVoiceChannel( GuildMessageReceivedEvent e.getMember().getVoiceState().getChannel(), true);
        
        File dest = null;
        try {

            if (new File("C:/Users/Evan Green/Desktop/recording/").exists())
                dest = new File("C:/Users/Evan Green/Desktop/recording/timestamp" + ".txt");
            else
                dest = new File("C:/Users/Evan Green/Desktop/recording/timestamp" + ".txt");
            
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        try(  PrintWriter outfile = new PrintWriter(dest)  ){
            outfile.println(e.getGuild());
            outfile.println( OffsetDateTime.now());
        }
        
        catch (Exception ex) {
            ex.printStackTrace();
        }
        */
    
    
    
    @Override
    public String usage() {
        return "join";
    }

    @Override
    public String descripition() {
        return "Force the bot to join and record your current channel";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
