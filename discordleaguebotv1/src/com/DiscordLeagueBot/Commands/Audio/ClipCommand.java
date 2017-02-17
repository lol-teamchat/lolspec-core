package com.DiscordLeagueBot.Commands.Audio;

import com.DiscordLeagueBot.Commands.Command;
import com.DiscordLeagueBot.DiscordLeagueBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class ClipCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 1 && args.length != 2) {
            DiscordLeagueBot.sendMessage(e.getChannel(), DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).prefix + usage());
            return;
        }

        if(e.getGuild().getAudioManager().getConnectedChannel() == null) {
            DiscordLeagueBot.sendMessage(e.getChannel(), "I wasn't recording!");
            return;
        }

        if (args.length == 2 && e.getGuild().getTextChannelsByName(args[1], true).size() == 0) {
            DiscordLeagueBot.sendMessage(e.getChannel(), "Cannot find specified voice channel");
            return;
        }

        int time;
        try {
            time = Integer.parseInt(args[0]);
        } catch (Exception ex) {
            DiscordLeagueBot.sendMessage(e.getChannel(), DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).prefix + usage());
            return;
        }

        if (time <= 0) {
            DiscordLeagueBot.sendMessage(e.getChannel(), "Time must be greater than 0");
            return;
        }

        if (args.length == 2)
            DiscordLeagueBot.writeToFile(e.getGuild(), time, e.getGuild().getTextChannelsByName(args[1], true).get(0));
        else
            DiscordLeagueBot.writeToFile(e.getGuild(), time, e.getChannel());

    }

    @Override
    public String usage() {
        return "clip [seconds] | clip [seconds] [text channel output]";
    }

    @Override
    public String descripition() {
        return "Saves a clip of the specified length and outputs it in the current or specified text channel (max 120 seconds)";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
