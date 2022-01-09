package com.grow.bot.commands.server;

import com.grow.bot.Bot;
import com.grow.bot.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Help extends SlashCommand  {
    public Help(String name, String description) {
        super(name, description);
    }

    @Override
    public void run(SlashCommandEvent event) throws Exception {
        String description = "Everything about the Bot: ";
        EmbedBuilder embed = Bot.getReplyEmbed("Help", description);
    }
}
