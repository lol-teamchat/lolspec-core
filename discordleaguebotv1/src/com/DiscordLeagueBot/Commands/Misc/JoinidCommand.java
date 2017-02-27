package com.DiscordLeagueBot.Commands.Misc;

import java.io.IOException;

import com.DiscordLeagueBot.DiscordLeagueBot;
import com.DiscordLeagueBot.Commands.Command;
import com.DiscordLeagueBot.Listeners.EventListener;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class JoinidCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }
  
    public void action(String[] args, GuildMessageReceivedEvent e) {
        
    	DiscordLeagueBot.joinVoiceChannel(DiscordLeagueBot.api.getVoiceChannelById(args[0]),true);

    		
    }
    	
	@Override
	public String usage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String descripition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		// TODO Auto-generated method stub
		
	}
}