package com.DiscordLeagueBot.Commands.Misc;

import com.DiscordLeagueBot.Commands.Command;

import net.dv8tion.jda.core.entities.User;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.DiscordLeagueBot.DiscordLeagueBot;

public class SaveidCommand implements Command {
    @Override
    public void action(String[] args) {
    	
    	FileReader fr;
        String url = null;
        String user = null;
        String password = null;
        User curr = null;
        String discid = null;
        String discname = null;
        String discnum = null;
        
		try {
			fr = new FileReader("secret");
	        BufferedReader br = new BufferedReader(fr);

			url = br.readLine();
	        user = br.readLine();
	        password = br.readLine();
	        
	        br.close();
	        fr.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
        System.out.println("Connecting to database...");

        //tries to find a discord id matching that summoner id. if it cant, then look for discord name / discriminator instead.
        try (java.sql.Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Database connected!");
            PreparedStatement ps1 = connection.prepareStatement("SELECT * FROM `players` WHERE `players`.`summoner_id` = ?");
            ps1.setString(1,args[0]);
            ResultSet rs = ps1.executeQuery();
            if(rs.next()){
            discid = rs.getString(4);
            discname = rs.getString(5);
            discnum = rs.getString(6);
            System.out.println("got past first query");
            }
            if (discid == null){
            	return;
            }
    	
            DiscordLeagueBot.writeToFile(DiscordLeagueBot.api.getGuildById(DiscordLeagueBot.serverSettings.get(discid).lastGuildId));       
            DiscordLeagueBot.leaveVoiceChannel(DiscordLeagueBot.api.getGuildById(DiscordLeagueBot.serverSettings.get(discid).lastGuildId).getAudioManager().getConnectedChannel());   

              
        }
        catch (java.sql.SQLException e1) {
            throw new IllegalStateException("Cannot connect the database!", e1);
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
	public Boolean called(String[] args) {
		return null;
	}

	@Override
	public void executed(boolean success) {
		System.out.println("Ended current process");
		
	}
}
