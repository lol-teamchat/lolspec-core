package com.ConsoleCommand;

import java.util.Scanner;
import com.DiscordLeagueBot.Commands.*;

public class ConsoleCommandListener implements Runnable {

    public static ConsoleCommandListener instance;

    static {
        // This will make it so that there may only be 1 ConsoleCommandImplementation.
        instance = new ConsoleCommandListener();
    }

    private Thread thread;
    private Scanner scanner;

    private ConsoleCommandListener() {
        // Initialize the thread
        this.thread = new Thread(this);
        // Initialize the system input scanner
        scanner = new Scanner(System.in);
    }

    public void start() {
        // Start the thread if it isn't currently running
        if(!thread.isAlive()) {
            thread.start();
        }
    }

    public void stop() {
        try {
            thread.join(1000);
        } 
        catch (InterruptedException ignored) {
        }
    }

    @Override
    public void run() {
        String line;
        // It would be better to change this to something like BotInstance.running
        while (thread.isAlive()) {
        	if(scanner.hasNext()){
        		if((line = scanner.nextLine()) != null) {
        			if (line.contains("saveid") || line.contains("joinid") || line.contains("leave")){
        				CommandHandler.handleCommand(CommandHandler.parser.parse(line));
        			}
        		}
        	}

        }
    }
}