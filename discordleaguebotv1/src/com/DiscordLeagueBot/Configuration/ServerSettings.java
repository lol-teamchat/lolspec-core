package com.DiscordLeagueBot.Configuration;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerSettings {


	//public String [] names; will use this later for better functionality
    public String prefix;
    public double volume;

    public ServerSettings(Guild g) {
        //this.names = new HashMap<>(g.getMembers().size());
      //  this.autoLeaveSettings = new HashMap<>(g.getVoiceChannels().size());

        for (VoiceChannel vc : g.getVoiceChannels()) {
           // this.autoJoinSettings.put(vc.getId(), Integer.MAX_VALUE);
            //this.autoLeaveSettings.put(vc.getId(), 1);
        }

        
        this.prefix = "!";
        this.volume = 0.8;



    }
}
