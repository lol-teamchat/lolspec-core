package com.DiscordLeagueBot.Commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
    Boolean called(String[] args, GuildMessageReceivedEvent e);
    Boolean called2(String[] args);
    void action(String[] args, GuildMessageReceivedEvent e);
    void action2(String[] args);
    String usage();
    String descripition();
    void executed(boolean success, GuildMessageReceivedEvent e);
    void executed2(boolean success);
}
