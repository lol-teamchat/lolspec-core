package com.DiscordLeagueBot.Commands;

import com.DiscordLeagueBot.DiscordLeagueBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import java.util.ArrayList;

public class CommandParser {
    
	public CommandContainer parse(String id) {
		
        ArrayList<String> split = new ArrayList<>();
        String prefix = "!";
        String raw = id;
        String beheaded = raw.replaceFirst(prefix, "");
        String[] splitBeheaded = beheaded.split(" ");
        for(String s : splitBeheaded) {split.add(s);}
        String invoke = split.get(0);
        String[] args = new String[split.size() - 1];
        split.subList(1,split.size()).toArray(args);
        GuildMessageReceivedEvent e = null;

        //System.out.println("raw = " + raw + "beheaded = " + beheaded + "splitbeheaded = " + splitBeheaded + "invoke = " + invoke + "args = " + args);
        return new CommandContainer(raw, beheaded, splitBeheaded, invoke, args, e);
	}

    public class CommandContainer {
        public final String raw;
        public final String beheaded;
        public final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final GuildMessageReceivedEvent e;

        public CommandContainer(String raw, String beheaded, String[] splitBeheaded, String invoke, String[] args, GuildMessageReceivedEvent e){

            this.raw = raw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.e = e;
        }
    }

}
