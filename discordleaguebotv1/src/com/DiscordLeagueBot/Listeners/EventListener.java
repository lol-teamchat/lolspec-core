package com.DiscordLeagueBot.Listeners;

import com.DiscordLeagueBot.DiscordLeagueBot;
import com.DiscordLeagueBot.Configuration.ServerSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.HashMap;

import static java.lang.Thread.sleep;


public class EventListener extends ListenerAdapter {


    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        DiscordLeagueBot.serverSettings.put(e.getGuild().getId(), new ServerSettings(e.getGuild()));
        DiscordLeagueBot.writeSettingsJson();
        System.out.format("Joined new server '%s', connected to %s guilds\n", e.getGuild().getName(), e.getJDA().getGuilds().size());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        DiscordLeagueBot.serverSettings.remove(e.getGuild().getId());
        DiscordLeagueBot.writeSettingsJson();
        System.out.format("Left server '%s', connected to %s guilds\n", e.getGuild().getName(), e.getJDA().getGuilds().size());
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
    	//THE BOT'S ID
    	//System.out.println(e.getMember().getUser().getId());
        if(e.getMember().getUser().getId().equals("279413450784899072")){
    		File dest = null;
            try {

                if (new File("recording/").exists())
                    dest = new File("recording/timestamp" + ".txt");
                else
                    dest = new File("recording/timestamp" + ".txt");
                
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            
            try(  PrintWriter outfile = new PrintWriter(dest)  ){
                outfile.print("Guild: " + e.getGuild().toString() + " ");
                outfile.println("Time_Joined_Channel: " + OffsetDateTime.now());
            }
           
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return;
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
    	
    	//bot's ID
        if(e.getMember().getUser().getId().equals("279413450784899072")){
    		File dest = null;
	    	try {
	
	            if (new File("recording/").exists())
	                dest = new File("recording/timestamp" + ".txt");
	            else
	                dest = new File("recording/timestamp" + ".txt");
	            
	        }
	        catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        try(  PrintWriter outfile = new PrintWriter(new FileOutputStream (dest,true))  ){
	            outfile.append("Guild: " + e.getGuild() + " ");
	            outfile.append("Time_Left_Channel: " + OffsetDateTime.now());
	        }
	        
	        catch (Exception ex) {
	            ex.printStackTrace();
	        }
        }
        return;
    }

    @Override
    public void onGuildVoiceMove (GuildVoiceMoveEvent e) {
      return;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        return;
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
    	return;
    }

    @Override
    public void onReady(ReadyEvent e){
        e.getJDA().getPresence().setGame(new Game() {
            @Override
            public String getName() {
                return "http://xddddd.ddns.net/lolspec/";
            }

            @Override
            public String getUrl() {
                return "http://xddddd.ddns.net/lolspec/";
            }

            @Override
            public GameType getType() {
                return GameType.DEFAULT;
            }
        });

        try {
            System.out.format("ONLINE: Connected to %s guilds!\n", e.getJDA().getGuilds().size(), e.getJDA().getVoiceChannels().size());

            Gson gson = new Gson();

            FileReader fileReader = new FileReader("settings.json");
            BufferedReader buffered = new BufferedReader(fileReader);

            Type type = new TypeToken<HashMap<String, ServerSettings>>(){}.getType();

            DiscordLeagueBot.serverSettings = gson.fromJson(fileReader, type);

            if (DiscordLeagueBot.serverSettings == null)
                DiscordLeagueBot.serverSettings = new HashMap<>();

            buffered.close();
            fileReader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        for (Guild g : e.getJDA().getGuilds()) {    //validate settings files
            if (!DiscordLeagueBot.serverSettings.containsKey(g.getId())) {
                DiscordLeagueBot.serverSettings.put(g.getId(), new ServerSettings(g));
                DiscordLeagueBot.writeSettingsJson();
            }
        }
        
        //used to delete files after a certain amount of time
        
        /*
        File dir = new File("recording/");
        if (!dir.exists())
            dir = new File("recording/");

        for (File f : dir.listFiles()) {
            if (f.getName().substring(f.getName().lastIndexOf('.'), f.getName().length()).equals(".mp3")) {
                new Thread (() -> {

                    try { sleep(1000 * 60 * 30); } catch (Exception ex){}

                    f.delete();
                    System.out.println("\tDeleting file " + f.getName() + "...");

                }).start();
            }
        }
        */

    }
}
