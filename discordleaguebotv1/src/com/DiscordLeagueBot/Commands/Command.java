package com.DiscordLeagueBot.Commands;

public interface Command {
    Boolean called(String[] args);
    void action(String[] args);
    String usage();
    String descripition();
    void executed(boolean success);
}
