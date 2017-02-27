package com.DiscordLeagueBot.Commands.Misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.OffsetDateTime;
import java.util.List;

import com.DiscordLeagueBot.DiscordLeagueBot;
import com.DiscordLeagueBot.Commands.Command;
import com.DiscordLeagueBot.Listeners.AudioReceiveListener;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.audio.AudioConnection;
import net.dv8tion.jda.core.audio.hooks.*;


public class JoinidCommand implements Command {
	
	public VoiceChannel findCorrectChannel(String g) throws IOException{
        int j = 0;
        int [] arr = new int [DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().size()+1];
        for (int e : arr){
        	arr[e]=0;
        }
        
		FileReader fr = new FileReader("names");
        BufferedReader br = new BufferedReader(fr);
        String name;
        while((name = br.readLine()) != null){
        	j = 0;
	        for (VoiceChannel i : DiscordLeagueBot.api.getGuildById(g).getVoiceChannels()){
	        	for (net.dv8tion.jda.core.entities.Member k : i.getMembers()){
		        	if(k.getUser().getName().equals(name)){
			        	arr[j]++;
		        	}
	        	}
	        	j++;
	        }
        }
        br.close();
		
        VoiceChannel vc = DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().get(0);
        int biggest = arr[0];
        for(int i = 0; i < DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().size()+1; i++){
        	if(arr[i] > biggest){
        		vc = DiscordLeagueBot.api.getGuildById(g).getVoiceChannels().get(i);
        		biggest = arr[i];
        	}
        }
        System.out.println(vc.getName() + " has the most matching members as on website team");
        return vc;
	}
	
    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }
  
    public void action(String[] args, GuildMessageReceivedEvent e) {
        
    	VoiceChannel vc=null;
		try {
			vc = findCorrectChannel(args[0]);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		File dest = null;
        try {

            if (new File("recording/").exists())
                dest = new File("recording/timestamp" + ".txt");
            else
                dest = new File("recording/timestamp" + ".txt");
            
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        try(  PrintWriter outfile = new PrintWriter(dest)  ){
            outfile.print("Guild: " + DiscordLeagueBot.api.getGuildById(args[0]) + " ");
            outfile.println("Time_Joined_Channel: " + OffsetDateTime.now());
        }
        
        catch (Exception ex) {
            ex.printStackTrace();
        }
		
		DiscordLeagueBot.joinVoiceChannel(vc,true);
		DiscordLeagueBot.in_vc = true;
		AudioReceiveListener ah = (AudioReceiveListener) DiscordLeagueBot.api.getGuildById(args[0]).getAudioManager().getReceiveHandler();
		//while(DiscordLeagueBot.in_vc)
		
		//System.out.println(ah.compVoiceData);

		//}
		//	List<Member> mems = vc.getMembers();
		//	for (Member i : mems){
		//		if(lx.onUserSpeaking((User)mems[i], true)
			//}
			//	if(i.getVoiceState() != null){
				//System.out.println("state: ");
				//System.out.print("what");
		//		}
		//	}
		//}
	//	}
    }
    	
    
 public void action2(String[] args) {
        
    	VoiceChannel vc=null;
		try {
			vc = findCorrectChannel(args[0]);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		File dest = null;
        try {

            if (new File("recording/").exists())
                dest = new File("recording/timestamp" + ".txt");
            else
                dest = new File("recording/timestamp" + ".txt");
            
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        try(  PrintWriter outfile = new PrintWriter(dest)  ){
            outfile.print("Guild: " + DiscordLeagueBot.api.getGuildById(args[0]) + " ");
            outfile.println("Time_Joined_Channel: " + OffsetDateTime.now());
        }
       
        catch (Exception ex) {
            ex.printStackTrace();
        }
		
		DiscordLeagueBot.joinVoiceChannel(vc,true);
		DiscordLeagueBot.in_vc = true;
		AudioReceiveListener ah = (AudioReceiveListener) DiscordLeagueBot.api.getGuildById(args[0]).getAudioManager().getReceiveHandler();
		//while(DiscordLeagueBot.in_vc)
		
		//System.out.println(ah.compVoiceData);

		//}
		//	List<Member> mems = vc.getMembers();
		//	for (Member i : mems){
		//		if(lx.onUserSpeaking((User)mems[i], true)
			//}
			//	if(i.getVoiceState() != null){
				//System.out.println("state: ");
				//System.out.print("what");
		//		}
		//	}
		//}
	//	}
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