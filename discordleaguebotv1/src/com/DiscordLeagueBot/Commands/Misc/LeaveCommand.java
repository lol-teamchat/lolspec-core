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
    	 try{
         	DiscordLeagueBot.leaveVoiceChannel(DiscordLeagueBot.api.getGuildById(DiscordLeagueBot.serverSettings.get(args[0]).lastGuildId).getAudioManager().getConnectedChannel());   
         }
         catch(Exception e1){
         	System.err.println("I can't leave channel that I am not in! Or the serverSettings hashtable was set incorrectly");
         }         
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
