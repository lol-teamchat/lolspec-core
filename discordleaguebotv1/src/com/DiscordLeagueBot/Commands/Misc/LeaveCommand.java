package com.DiscordLeagueBot.Commands.Misc;

import com.DiscordLeagueBot.Commands.Command;
import com.DiscordLeagueBot.DiscordLeagueBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class LeaveCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
    	DiscordLeagueBot.in_vc = false;
        if (args.length != 0) {
            DiscordLeagueBot.sendMessage(e.getChannel(), DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).prefix + usage());
            return;
        }

        if (!e.getGuild().getAudioManager().isConnected()) {
            DiscordLeagueBot.sendMessage(e.getChannel(), "I am not in a channel!");
            return;
        }

        //write out previous channel's audio if autoSave is on
        if (DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).autoSave)
            DiscordLeagueBot.writeToFile(e.getGuild());

        DiscordLeagueBot.leaveVoiceChannel(e.getGuild().getAudioManager().getConnectedChannel());

    }

    @Override
    public String usage() {
        return "leave";
    }

    @Override
    public String descripition() {
        return "Force the bot to leave it's current channel";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }

	@Override
	public Boolean called2(String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void action2(String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executed2(boolean success) {
		// TODO Auto-generated method stub
		
	}
}
