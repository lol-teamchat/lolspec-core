package com.ConsoleCommand;

import java.util.Scanner;

import com.DiscordLeagueBot.*;
import com.DiscordLeagueBot.Commands.*;

public class ConsoleCommand1 implements Runnable {

    public static ConsoleCommand1 instance;

    static {
        // This will make it so that there may only be 1 ConsoleCommandImplementation.
        instance = new ConsoleCommand1();
    }

    private Thread thread;
    private Scanner scanner;

    private ConsoleCommand1() {
        // Init the thread.
        this.thread = new Thread(this);
        // Init the system input scanner.
        scanner = new Scanner(System.in);
    }

    public void start() {
        // Start the thread if it isn't currently running.
        if(!thread.isAlive()) {
            thread.start();
        }
    }

    public void stop() {
        // TODO: Check if you have a BotInstance.running and return if it isn't running.
        try {
            thread.join(1000);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void run() {
        String line;
        // It would be better to change this to like BotInstance.running or something of the sorts.
        while (thread.isAlive()) {
        	if(scanner.hasNext()){
            if((line = scanner.nextLine()) != null) {
            if (line.contains("save")){
            	SaveidCommand(line);
            }
            else if (line.contains("join")){
            	System.out.println("input to stdin:" + line);
            	JoinidCommand(line);
            }
            }
        	}
        }
    }
    public void SaveidCommand(String id){
    	CommandHandler.handleCommand2(CommandHandler.parser.parse(id.toLowerCase()));
    }
    public void JoinidCommand(String id){
    	CommandHandler.handleCommand2(CommandHandler.parser.parse(id.toLowerCase()));
    }
}