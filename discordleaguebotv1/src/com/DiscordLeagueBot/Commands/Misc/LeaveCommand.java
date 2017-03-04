package com.DiscordLeagueBot.Commands.Misc;

import com.DiscordLeagueBot.Commands.Command;
import com.DiscordLeagueBot.DiscordLeagueBot;

public class LeaveCommand implements Command {

    @Override
    public Boolean called(String[] args){
        return true;
    }

    @Override
    public void action(String[] args) {
    	DiscordLeagueBot.in_vc = false;
        DiscordLeagueBot.leaveVoiceChannel(DiscordLeagueBot.api.getGuildById(args[0]).getAudioManager().getConnectedChannel());
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
    public void executed(boolean success){
    	System.out.println("Ended current process");
    }
    
}
