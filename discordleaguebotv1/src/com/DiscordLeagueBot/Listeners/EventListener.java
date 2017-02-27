package com.DiscordLeagueBot.Listeners;

import com.DiscordLeagueBot.DiscordLeagueBot;
import com.DiscordLeagueBot.Commands.CommandHandler;
import com.DiscordLeagueBot.Configuration.ServerSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
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
import java.io.FileReader;
import java.lang.reflect.Type;
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
        if(e.getMember().getUser().isBot())
            return;

        if (e.getGuild().getAudioManager().isConnected()) {

            int newSize = DiscordLeagueBot.voiceChannelSize(e.getChannelJoined());
            int botSize = DiscordLeagueBot.voiceChannelSize(e.getGuild().getAudioManager().getConnectedChannel());
            int min = DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).autoJoinSettings.get(e.getChannelJoined().getId());

            if (newSize >= min && botSize < newSize) {  //check for tie with old server
                if (DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).autoSave)
                    DiscordLeagueBot.writeToFile(e.getGuild());  //write data from voice channel it is leaving

                DiscordLeagueBot.joinVoiceChannel(e.getChannelJoined(), false);
            }

        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
        if(e.getMember().getUser().isBot())
            return;

        int min = DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).autoLeaveSettings.get(e.getChannelLeft().getId());
        int size = DiscordLeagueBot.voiceChannelSize(e.getChannelLeft());

        if (size <= min && e.getGuild().getAudioManager().getConnectedChannel() == e.getChannelLeft()) {

            if (DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).autoSave)
                DiscordLeagueBot.writeToFile(e.getGuild());  //write data from voice channel it is leaving

            DiscordLeagueBot.leaveVoiceChannel(e.getGuild().getAudioManager().getConnectedChannel());
        }
    }

    @Override
    public void onGuildVoiceMove (GuildVoiceMoveEvent e) {
        if(e.getMember().getUser().isBot())
            return;

        //Check if bot needs to join newly joined channel
        

        if (e.getGuild().getAudioManager().isConnected()) {

            int newSize = DiscordLeagueBot.voiceChannelSize(e.getChannelJoined());
            int botSize = DiscordLeagueBot.voiceChannelSize(e.getGuild().getAudioManager().getConnectedChannel());
            int min = DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).autoJoinSettings.get(e.getChannelJoined().getId());

            if (newSize >= min && botSize < newSize) {  //check for tie with old server
                if (DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).autoSave)
                    DiscordLeagueBot.writeToFile(e.getGuild());  //write data from voice channel it is leaving

                DiscordLeagueBot.joinVoiceChannel(e.getChannelJoined(), false);
            }

        }

        //Check if bot needs to leave old channel
        int min = DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).autoLeaveSettings.get(e.getChannelLeft().getId());
        int size = DiscordLeagueBot.voiceChannelSize(e.getChannelLeft());

        if (size <= min && e.getGuild().getAudioManager().getConnectedChannel() == e.getChannelLeft()) {

            if (DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).autoSave)
                DiscordLeagueBot.writeToFile(e.getGuild());  //write data from voice channel it is leaving

            DiscordLeagueBot.leaveVoiceChannel(e.getGuild().getAudioManager().getConnectedChannel());;

            VoiceChannel largest = DiscordLeagueBot.largestChannel(e.getGuild().getVoiceChannels());
            if (largest != null) {
                DiscordLeagueBot.joinVoiceChannel(e.getChannelJoined(), false);
            }
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if(e.getMember().getUser().isBot())
            return;

        String prefix = DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).prefix;
        if (e.getMessage().getContent().startsWith(prefix) && !e.getMessage().getAuthor().isBot()) {
            System.out.println("--- " + e.getAuthor().getName() + ": " + e.getMessage().getContent());
            CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContent().toLowerCase(), e));
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getMessage().getContent().equals("!alerts off")) {
            for (Guild g : e.getJDA().getGuilds()) {
                if (g.getMember(e.getAuthor()) != null) {
                    DiscordLeagueBot.serverSettings.get(g.getId()).alertBlackList.add(e.getAuthor().getId());
                }
            }
            e.getChannel().sendMessage("Alerts now off, message `!alerts on` to re-enable at any time").queue();
            DiscordLeagueBot.writeSettingsJson();
        } else if (e.getMessage().getContent().equals("!alerts on")) {
            for (Guild g : e.getJDA().getGuilds()) {
                if (g.getMember(e.getAuthor()) != null) {
                    DiscordLeagueBot.serverSettings.get(g.getId()).alertBlackList.remove(e.getAuthor().getId());
                }
            }
            e.getChannel().sendMessage("Alerts now on, message `!alerts off` to disable at any time").queue();
            DiscordLeagueBot.writeSettingsJson();
        }
    }

    @Override
    public void onReady(ReadyEvent e){
        e.getJDA().getPresence().setGame(new Game() {
            @Override
            public String getName() {
                return "!help | https://github.com/adamschachne/league-replay";
            }

            @Override
            public String getUrl() {
                return "https://github.com/adamschachne/league-replay";
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

        File dir = new File("/var/www/html/");
        if (!dir.exists())
            dir = new File("recordings/");

        for (File f : dir.listFiles()) {
            if (f.getName().substring(f.getName().lastIndexOf('.'), f.getName().length()).equals(".mp3")) {
                new Thread (() -> {

                    try { sleep(1000 * 60 * 30); } catch (Exception ex){}

                    f.delete();
                    System.out.println("\tDeleting file " + f.getName() + "...");

                }).start();
            }
        }

    }
}
