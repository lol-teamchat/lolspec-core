package com.DiscordLeagueBot.Listeners;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.Instant;

import net.dv8tion.jda.core.audio.hooks.ListenerProxy;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class UserSpeakingListener extends ListenerProxy
{
	private VoiceChannel vc;
	private File dest;
	
	public UserSpeakingListener(VoiceChannel voice, String saveloc){
		vc = voice;
		
        try {

            if (new File("recording/").exists()){
            	if (!(new File ("recording/" + saveloc + "/").exists())){
            		File newdir = new File ("recording/" + saveloc + "/");
            		newdir.mkdir();
            	}
            	dest = new File("recording/" + saveloc + "/" + "timestamp" + ".txt");
            }
            else 
                dest = new File("recording/" + saveloc + "/" + "timestamp" + ".txt");
            
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
		
		return;
	}
	@Override
	public void onUserSpeaking(User arg0, boolean arg1) {
		if (vc.getMembers().toString().contains(arg0.getName())){

			try(  PrintWriter outfile = new PrintWriter(new FileOutputStream (dest,true))  ){
                outfile.println(arg0.toString() + " speaking = " + arg1 + " " + Instant.now().toEpochMilli() + '\n');
                outfile.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
			
		}
	}
}