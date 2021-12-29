package com.grow.bot.commands;


import com.grow.bot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        for (SlashCommand slashCommand: Bot.commandList) {
            if(event.getName().equals(slashCommand.name)){
                try {
                    slashCommand.run(event);
                }catch (Exception e){
                    e.printStackTrace();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("ayayayayyy :( .... An error occurred!");
                    embed.setColor(Color.RED);
                    embed.addField("Exception",e.getMessage(),true);
                    embed.setDescription("Please join the support server here ....  and tell us the Exception! So we can fix this as fast as possible!");
                    event.replyEmbeds(embed.build()).queue();
                }
            }
        }
    }
}
