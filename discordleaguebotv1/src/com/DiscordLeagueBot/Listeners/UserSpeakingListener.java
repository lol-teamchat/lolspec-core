package com.DiscordLeagueBot.Listeners;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.OffsetDateTime;

import net.dv8tion.jda.core.audio.hooks.ListenerProxy;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class UserSpeakingListener extends ListenerProxy
{
	private VoiceChannel vc;
	private File dest;
	
	public UserSpeakingListener(VoiceChannel voice, String rand){
		vc = voice;
		
		try {

            if (new File("recording/" + vc.getGuild().getId().toString() + "/").exists())
                dest = new File("recording/" + vc.getGuild().getId().toString() + "/" + rand + "_timestamp" + ".txt");
            else
                dest = new File("recording/" + vc.getGuild().getId().toString() + "_" + rand + "_timestamp" + ".txt");
            
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
                outfile.println(arg0.toString() + " speaking = " + arg1 + " " + OffsetDateTime.now().toOffsetTime() + '\n');
                outfile.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
			
		}
	}
}