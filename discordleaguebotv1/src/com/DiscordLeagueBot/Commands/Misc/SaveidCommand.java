package com.DiscordLeagueBot.Commands.Misc;

import com.DiscordLeagueBot.Commands.Command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.OffsetDateTime;

import com.DiscordLeagueBot.DiscordLeagueBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class SaveidCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
    		DiscordLeagueBot.in_vc = false;
            DiscordLeagueBot.writeToFile(DiscordLeagueBot.api.getGuildById(args[0]));
    }
    
    public void action2(String[] args) {
    		DiscordLeagueBot.in_vc = false;
            DiscordLeagueBot.writeToFile(DiscordLeagueBot.api.getGuildById(args[0]));
            
            File dest = null;
            try {

                if (new File("C:/Users/Evan Green/Desktop/recording/").exists())
                    dest = new File("C:/Users/Evan Green/Desktop/recording/timestamp" + ".txt");
                else
                    dest = new File("C:/Users/Evan Green/Desktop/recording/timestamp" + ".txt");
                
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            try(  PrintWriter outfile = new PrintWriter(new FileOutputStream (dest,true))  ){
                outfile.append("Guild: " + DiscordLeagueBot.api.getGuildById(args[0]) + " ");
                outfile.append("Time_Left_Channel: " + OffsetDateTime.now());
            }
            
            catch (Exception ex) {
                ex.printStackTrace();
            }
            
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
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }

	@Override
	public Boolean called2(String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executed2(boolean success) {
		// TODO Auto-generated method stub
		
	}
}
