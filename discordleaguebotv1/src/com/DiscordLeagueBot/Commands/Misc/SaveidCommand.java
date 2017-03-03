package com.DiscordLeagueBot.Commands.Misc;

import com.DiscordLeagueBot.Commands.Command;
import com.DiscordLeagueBot.DiscordLeagueBot;

public class SaveidCommand implements Command {
    @Override
    public void action(String[] args) {
    		DiscordLeagueBot.in_vc = false;
            DiscordLeagueBot.writeToFile(DiscordLeagueBot.api.getGuildById(args[0]));
            DiscordLeagueBot.leaveVoiceChannel(DiscordLeagueBot.api.getGuildById(args[0]).getAudioManager().getConnectedChannel());
            
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
	public Boolean called(String[] args) {
		return null;
	}

	@Override
	public void executed(boolean success) {
		System.out.println("Ended current process");
		
	}
}
