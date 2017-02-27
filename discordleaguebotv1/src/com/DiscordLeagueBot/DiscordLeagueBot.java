package com.DiscordLeagueBot;

import com.DiscordLeagueBot.Commands.*;
import com.DiscordLeagueBot.Commands.Audio.ClipCommand;
import com.DiscordLeagueBot.Commands.Audio.SaveCommand;
import com.DiscordLeagueBot.Commands.Misc.HelpCommand;
import com.DiscordLeagueBot.Commands.Misc.JoinCommand;
import com.DiscordLeagueBot.Commands.Misc.JoinidCommand;
import com.DiscordLeagueBot.Commands.Misc.LeaveCommand;
import com.DiscordLeagueBot.Configuration.ServerSettings;
import com.DiscordLeagueBot.Listeners.AudioReceiveListener;
import com.DiscordLeagueBot.Listeners.AudioSendListener;
import com.DiscordLeagueBot.Listeners.EventListener;
import com.google.gson.Gson;
import net.dv8tion.jda.core.EmbedBuilder;
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
import java.awt.*;
import java.io.*;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;

import static java.lang.Thread.sleep;

public class DiscordLeagueBot
{
	public static JDA api;
    public static HashMap<String, ServerSettings> serverSettings = new HashMap<>();

    public static void main(String[] args)
    {
    	
    	//Allows control of the discord bot via the special token in "token_loc" 
        try
        {
            FileReader fr = new FileReader("token_loc");
            BufferedReader br = new BufferedReader(fr);
            String token = br.readLine();

            api = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .addListener(new EventListener())
                    .buildBlocking();
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

        /*
         * Tells the bot which commands it has, they need to be listed here to
         * work correctly and to be included in the "help" command
         */
        
        CommandHandler.commands.put("help", new HelpCommand());
        CommandHandler.commands.put("join", new JoinCommand());
        CommandHandler.commands.put("leave", new LeaveCommand());
        CommandHandler.commands.put("save", new SaveCommand());
        CommandHandler.commands.put("clip", new ClipCommand());
        CommandHandler.commands.put("joinid", new JoinidCommand());

    }
    

    /*----------------------------- Utility Functions -----------------------------*/

    //determines largest channel
    public static VoiceChannel largestChannel(List<VoiceChannel> vcs) {
        int size = 0;
        VoiceChannel largest = null;

        for (VoiceChannel v : vcs) {
            if (voiceChannelSize(v) > size) {
                if (voiceChannelSize(v) >= DiscordLeagueBot.serverSettings.get(v.getGuild().getId()).autoJoinSettings.get(v.getId())) {
                    largest = v;
                    size = voiceChannelSize(v);
                }
            }
        }
        return largest;
    }

    //determines users in the provided voice channel
    public static int voiceChannelSize(VoiceChannel v) {
        if (v == null) return 0;

        int i = 0;
        for (Member m : v.getMembers()){
            if(!m.getUser().isBot()) i++;
        }
        return i;
    }

    public static void writeToFile(Guild guild) {
        writeToFile(guild, -1, null);
    }

    public static void writeToFile(Guild guild, TextChannel tc) {
        writeToFile(guild, -1, tc);
    }

    public static void writeToFile(Guild guild, int time, TextChannel tc) {
        if (tc == null)
            tc = guild.getTextChannelById(serverSettings.get(guild.getId()).defaultTextChannel);
        
        AudioReceiveListener ah = (AudioReceiveListener) guild.getAudioManager().getReceiveHandler();
        if (ah == null) {
            DiscordLeagueBot.sendMessage(tc, "I wasn't recording!");
            return;
        }

        File dest;
        try {

            if (new File("C:/Users/Evan Green/Desktop/recording/").exists())
                dest = new File("C:/Users/Evan Green/Desktop/recording/" + getRandString() + ".mp3");
            else
                dest = new File("C:/Users/Evan Green/Desktop/recording/" + getRandString() + ".mp3");

            byte[] voiceData;
            ah.canReceive = false;

            if (time > 0 && time <= ah.PCM_MINS * 60 * 2) {
                voiceData = ah.getUncompVoice(time);
                voiceData = encodePcmToMp3(voiceData);

            } else {
                voiceData = ah.getVoiceData();
            }

            FileOutputStream fos = new FileOutputStream(dest);
            fos.write(voiceData);
            fos.close();

            System.out.format("Saving audio file '%s' from %s on %s of size %f MB\n",
                    dest.getName(), guild.getAudioManager().getConnectedChannel().getName(), guild.getName(), (double) dest.length() / 1024 / 1024);

            if (dest.length() / 1024 / 1024 < 8) {
                final TextChannel channel = tc;
                tc.sendFile(dest, null).queue(null, (Throwable) -> {
                    channel.sendMessage("I don't have permissions to send files here!").queue();
                });

                new Thread(() -> {
                    try { sleep(1000 * 20); } catch (Exception ex) {}    //20 second life for files set to discord (no need to save)

                    dest.delete();
                    System.out.println("\tDeleting file " + dest.getName() + "...");

                }).start();

            } else {
                DiscordLeagueBot.sendMessage(tc, "C:/Users/Evan Green/Desktop/recording/" + dest.getName());

                new Thread(() -> {
                    try { sleep(1000 * 60 * 60); } catch (Exception ex) {}    //1 hour life for files stored on web server

                    dest.delete();
                    System.out.println("\tDeleting file " + dest.getName() + "...");

                }).start();
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            if (tc != null)
                DiscordLeagueBot.sendMessage(tc, "Unknown error sending file");
            else
                DiscordLeagueBot.sendMessage(guild.getTextChannelById(serverSettings.get(guild.getId()).defaultTextChannel), "Unknown error sending file");

        }
    }


    public static void writeSettingsJson() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(DiscordLeagueBot.serverSettings);

            FileWriter fw = new FileWriter("settings.json");
            fw.write(json);
            fw.flush();
            fw.close();

        } catch (Exception ex) {}
    }

    public static void alert(VoiceChannel vc) {
        for (Member m : vc.getMembers()) {
            if(m.getUser() == vc.getJDA().getSelfUser()) continue;
            if (!serverSettings.get(vc.getGuild().getId()).alertBlackList.contains(m.getUser().getId()) && !m.getUser().isBot()) {

                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor("Discord LeagueBot", "https://github.com/adamschachne/league-replay", vc.getJDA().getSelfUser().getAvatarUrl());
                embed.setColor(Color.PINK);
                embed.setTitle("Your audio is now being recorded in '" + vc.getName() + "' on '" + vc.getGuild().getName() + "'");
                embed.setDescription("Cancel recording with ``!leave``");
                embed.setThumbnail("http://i.imgur.com/gNBibKi.png");
                embed.setTimestamp(OffsetDateTime.now());

                m.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(embed.build()).queue());

            }
        }
    }


    public static String getRandString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 13) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
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

        AudioSendListener sh = (AudioSendListener) g.getAudioManager().getSendingHandler();
        if (sh != null) {
            sh.canProvide = false;
            sh.voiceData = null;
            g.getAudioManager().setSendingHandler(null);
        }

        System.out.println("Destroyed audio handlers for " + g.getName());
        System.gc();
    }

    public static void sendMessage(TextChannel tc, String message) {
        tc.sendMessage(message).queue(null, (Throwable) -> {
            tc.getGuild().getPublicChannel().sendMessage("I don't have permissions to send messages there!").queue();
        });
    }

    public static void joinVoiceChannel(VoiceChannel vc, boolean warning) {
        System.out.format("Joining '%s' voice channel in %s\n", vc.getName(), vc.getGuild().getName());

        try {
            vc.getGuild().getAudioManager().openAudioConnection(vc);
        } catch (Exception e) {
            if (warning)
                sendMessage(vc.getGuild().getPublicChannel(), "I don't have permission to join that voice channel!");
        }

        DiscordLeagueBot.alert(vc);
        double volume = DiscordLeagueBot.serverSettings.get(vc.getGuild().getId()).volume;
        vc.getGuild().getAudioManager().setReceivingHandler(new AudioReceiveListener(volume));

    }
    public static void leaveVoiceChannel(VoiceChannel vc) {
        System.out.format("Leaving '%s' voice channel in %s\n", vc.getGuild(), vc.getGuild().getName());

        vc.getGuild().getAudioManager().closeAudioConnection();
        DiscordLeagueBot.killAudioHandlers(vc.getGuild());
    }
}
