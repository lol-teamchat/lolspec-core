package com.DiscordLeagueBot.Commands.Misc;

import com.DiscordLeagueBot.Commands.Command;
import com.DiscordLeagueBot.DiscordLeagueBot;
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

        DiscordLeagueBot.joinVoiceChannel(e.getMember().getVoiceState().getChannel(), true);
    }

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
