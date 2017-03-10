package com.DiscordLeagueBot;

import com.DiscordLeagueBot.Commands.*;
import com.DiscordLeagueBot.Commands.Misc.JoinidCommand;
import com.DiscordLeagueBot.Commands.Misc.LeaveCommand;
import com.DiscordLeagueBot.Commands.Misc.SaveidCommand;
import com.DiscordLeagueBot.Configuration.ServerSettings;
import com.DiscordLeagueBot.Listeners.AudioReceiveListener;
import com.DiscordLeagueBot.Listeners.EventListener;
import com.google.gson.Gson;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;

import javax.security.auth.login.LoginException;
import javax.sound.sampled.AudioFormat;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.*;

public class DiscordLeagueBot
{
	public static JDA api;
    public static HashMap<String, ServerSettings> serverSettings = new HashMap<>();

    public static void main(String[] args)
    {
    	
    	//Allows control of the discord bot via the special token in "token_loc" file
        try
        {
            FileReader fr = new FileReader("token_loc");
            BufferedReader br = new BufferedReader(fr);
            String token = br.readLine();

            api = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .addListener(new EventListener())
                    .buildBlocking();
            br.close();
            fr.close();
            
            CommandHandler.commands.put("leave", new LeaveCommand());
            CommandHandler.commands.put("joinid", new JoinidCommand());
            CommandHandler.commands.put("saveid", new SaveidCommand());
            
            //print the process id
            System.out.println(ManagementFactory.getRuntimeMXBean().getName());
            
            //starts ConsoleCommandListener, which listens for system.in commands
            com.ConsoleCommand.ConsoleCommandListener.instance.start();
            //this line is required to know when the bot finishes starting up in cmdHandler.js
            System.out.println("Ended current process");
        }
        
        catch (InterruptedException ex)
        {
            //Should never occur, but if it does there is an error with  buildBlocking
            ex.printStackTrace();
        }
        
        catch (RateLimitedException ex)
        {
            //If you hit cap on login attempts; wait a few minutes and this should go away
            ex.printStackTrace();
        }
        
        catch (LoginException er)
        {
            //Occurs if there is any error with authenticating account
            er.printStackTrace();
        }
        
        catch (Exception ex) {
        	
        	//all other exceptions
            ex.printStackTrace();
        }


    }
    

    /*----------------------------- Utility Functions -----------------------------*/

    public static void writeToFile(Guild guild) {
        AudioReceiveListener ah = (AudioReceiveListener) guild.getAudioManager().getReceiveHandler();
        if (ah == null) {
        	System.out.println("There was no audio listener when trying to write!");
            return;
        }
        File dest;
        try {

            if (new File("recording/").exists()){
            	if (!(new File ("recording/" + ah.saveloc + "/").exists())){
            		File newdir = new File ("recording/" + ah.saveloc + "/");
            		newdir.mkdir();
            	}
            	dest = new File("recording/" + ah.saveloc + "/" + "audio.mp3");
            }
            else 
                dest = new File("recording/" + ah.saveloc + "/" + "audio.mp3");


            byte[] voiceData;
            ah.canReceive = false;

            voiceData = ah.getVoiceData();

            FileOutputStream fos = new FileOutputStream(dest);
            fos.write(voiceData);
            fos.close();

            System.out.format("Saving audio file '%s' from %s on %s of size %f MB\n",
                    dest.getName(), guild.getAudioManager().getConnectedChannel().getName(), guild.getName(), (double) dest.length() / 1024 / 1024);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    

    public static void writeUserGuildsJson() {
        try {
        	System.out.println("writing settings: " + DiscordLeagueBot.serverSettings );
            Gson gson = new Gson();
            String json = gson.toJson(DiscordLeagueBot.serverSettings);

            FileWriter fw = new FileWriter("userGuilds.json");
            fw.write(json);
            fw.flush();
            fw.close();

        } catch (Exception ex) {}
    }

    public static byte[] encodePcmToMp3(byte[] pcm) {
        LameEncoder encoder = new LameEncoder(new AudioFormat(48000.0f, 16, 2, true, true), 128, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);
        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        byte[] buffer = new byte[encoder.getPCMBufferSize()];

        int bytesToTransfer = Math.min(buffer.length, pcm.length);
        int bytesWritten;
        int currentPcmPosition = 0;
        while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
            currentPcmPosition += bytesToTransfer;
            bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);

            mp3.write(buffer, 0, bytesWritten);
        }

        encoder.close();

        return mp3.toByteArray();
    }

    public static void killAudioHandlers(Guild g) {
        AudioReceiveListener ah = (AudioReceiveListener) g.getAudioManager().getReceiveHandler();
        if (ah != null) {
            ah.canReceive = false;
            ah.compVoiceData = null;
            g.getAudioManager().setReceivingHandler(null);
        }

        //AudioSendListener sh = (AudioSendListener) g.getAudioManager().getSendingHandler();
        //if (sh != null) {
        //    sh.canProvide = false;
         //   sh.voiceData = null;
            g.getAudioManager().setSendingHandler(null);
       // }

        System.out.println("Destroyed audio handlers for " + g.getName());
        System.gc();
    }

    public static void sendMessage(TextChannel tc, String message) {
        tc.sendMessage(message).queue(null, (Throwable) -> {
            tc.getGuild().getPublicChannel().sendMessage("I don't have permissions to send messages there!").queue();
        });
    }

    public static void joinVoiceChannel(VoiceChannel vc, boolean warning, String saveFolder) {
        System.out.format("Joining '%s' voice channel in %s\n", vc.getName(), vc.getGuild().getName());

        try {
            vc.getGuild().getAudioManager().openAudioConnection(vc);
        } catch (Exception e) {
            if (warning)
                sendMessage(vc.getGuild().getPublicChannel(), "Please give me permission to join your voice channel!");
        }
        double volume = 0.8;
        vc.getGuild().getAudioManager().setReceivingHandler(new AudioReceiveListener(volume));
        AudioReceiveListener ah = (AudioReceiveListener) vc.getGuild().getAudioManager().getReceiveHandler();
        ah.saveloc = saveFolder;
        
		File dest = null;
        try {

            if (new File("recording/").exists()){
            	if (!(new File ("recording/" + ah.saveloc + "/").exists())){
            		File newdir = new File ("recording/" + ah.saveloc + "/");
            		newdir.mkdir();
            	}
            	dest = new File("recording/" + ah.saveloc + "/" + "timestamp" + ".txt");
            }
            else 
                dest = new File("recording/" + ah.saveloc + "/" + "timestamp" + ".txt");
            
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        try(  PrintWriter outfile = new PrintWriter(dest)  ){
            outfile.println("Guild: " + vc.getGuild().toString() + " ");
            outfile.println("Time_Joined_Channel: " + Instant.now().toEpochMilli());
            outfile.close();
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        

    }
    public static void leaveVoiceChannel(VoiceChannel vc) {
        System.out.format("Leaving '%s' voice channel in %s\n", vc.getGuild(), vc.getGuild().getName());
        AudioReceiveListener ah = (AudioReceiveListener) vc.getGuild().getAudioManager().getReceiveHandler();
        File dest = null;
        try {

            if (new File("recording/").exists()){
            	if (!(new File ("recording/" + ah.saveloc + "/").exists())){
            		File newdir = new File ("recording/" + ah.saveloc + "/");
            		newdir.mkdir();
            	}
            	dest = new File("recording/" + ah.saveloc + "/" + "timestamp" + ".txt");
            }
            else 
                dest = new File("recording/" + ah.saveloc + "/" + "timestamp" + ".txt");
            
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    	
        try(  PrintWriter outfile = new PrintWriter(new FileOutputStream (dest,true))  ){
            outfile.println("Guild: " + vc.getGuild() + " ");
            outfile.append("Time_Left_Channel: " + Instant.now().toEpochMilli());
            outfile.close();
        }
        
        catch (Exception ex) {
            ex.printStackTrace();
        }

        vc.getGuild().getAudioManager().closeAudioConnection();
        DiscordLeagueBot.killAudioHandlers(vc.getGuild());
    }
}
