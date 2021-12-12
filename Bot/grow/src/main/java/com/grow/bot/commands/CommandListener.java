package com.grow.bot.commands;


import com.grow.bot.Bot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        for (SlashCommand slashCommand: Bot.commandList) {
            if(event.getName().equals(slashCommand.name)){
                slashCommand.run(event);
            }
        }
    }
}
