package com.DiscordLeagueBot.Commands;

import java.util.HashMap;

public class CommandHandler {
    public static final CommandParser parser = new CommandParser();
    public static HashMap<String, Command> commands = new HashMap<String, Command>();
    
    public static void handleCommand(CommandParser.CommandContainer cmd){
        if (commands.containsKey(cmd.invoke)) {
            Boolean safe = true;

            if (safe) {         
                commands.get(cmd.invoke).action(cmd.args);
                commands.get(cmd.invoke).executed(safe);
            }
            
            else {
            	System.err.println("invalid command, please use !joinid ###, !saveid ###, or !leave ###");
                commands.get(cmd.invoke).executed(safe);
            }
        }
    }
}
