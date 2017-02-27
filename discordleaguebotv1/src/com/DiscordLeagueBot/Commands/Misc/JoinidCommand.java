package com.DiscordLeagueBot.Commands.Misc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Member;
import java.util.List;

import com.DiscordLeagueBot.DiscordLeagueBot;
import com.DiscordLeagueBot.Commands.Command;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class JoinidCommand implements Command {
	
	public VoiceChannel findCorrectChannel(String g) throws IOException{
        int j=0;
        int [] arr = new int [DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().size()+1];
        for (int e : arr){
        	arr[e]=0;
        	System.out.println(e+ ", " + arr[e]);
        }
        System.out.println(g);
        
		FileReader fr = new FileReader("names");
        BufferedReader br = new BufferedReader(fr);
        String name;
        while((name = br.readLine()) != null){
        	j=0;
	        for (VoiceChannel i : DiscordLeagueBot.api.getGuildById(g).getVoiceChannels()){
	        	System.out.println(i);
	        	for (net.dv8tion.jda.core.entities.Member k : i.getMembers()){
		        	if(k.getUser().getName().equals(name)){
			        	System.out.println(k.getUser().getName() + "in channel" + i);
			        	arr[j]++;
			        	System.out.println(j + "," + arr[j]);
		        	}
	        	}
	        	j++;
	        }
        }
		
        VoiceChannel vc = DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().get(0);
        int biggest = arr[0];
        System.out.println("pre for, " + vc);
        for(int i = 0; i < DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().size()+1; i++){
        	if(arr[i] > biggest){
        		vc = DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().get(i);
        		biggest = arr[i];
        	}
        	System.out.println("after for, " + arr[i] + ", " + vc);
        }
        System.out.println("largest vc is" + vc.getName());
        return vc;
	}
	
    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }
  
    public void action(String[] args, GuildMessageReceivedEvent e) {
        
    	try {
			DiscordLeagueBot.joinVoiceChannel(findCorrectChannel(args[0]),true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
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