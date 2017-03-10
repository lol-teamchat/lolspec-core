package com.DiscordLeagueBot.Configuration;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerSettings {


	//public String [] names; will use this later for better functionality
    public String lastGuildId;

    public ServerSettings(Guild g) {

        this.lastGuildId = g.getId();

    }
}
