package com.DiscordLeagueBot.Commands.Audio;

import com.DiscordLeagueBot.Commands.Command;
import com.DiscordLeagueBot.DiscordLeagueBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class SaveCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length > 1) {
            DiscordLeagueBot.sendMessage(e.getChannel(), DiscordLeagueBot.serverSettings.get(e.getGuild().getId()).prefix + usage());
            return;
        }

        if(e.getGuild().getAudioManager().getConnectedChannel() == null) {
            DiscordLeagueBot.sendMessage(e.getChannel(), "I wasn't recording!");
            return;
        }

        if (args.length == 0)
            DiscordLeagueBot.writeToFile(e.getGuild(), e.getChannel());

        else if (args.length == 1) {
            if (e.getGuild().getTextChannelsByName(args[0], true).size() == 0) {
                DiscordLeagueBot.sendMessage(e.getChannel(), "Cannot find specified voice channel");
                return;
            }
            DiscordLeagueBot.writeToFile(e.getGuild(), e.getGuild().getTextChannelsByName(args[0], true).get(0));
        }
    }

    @Override
    public String usage() {
        return "save | save [text channel output]";
    }

    @Override
    public String descripition() {
        return "Saves the current recording and outputs it to the current or specified text chats (caps at 16MB)";
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
