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
        String description = "• [test/support server](https://discord.gg/9gWBUpvfvj)\n• [github docs](https://github.com/Carl-Br/status-supporter-discord-bot)";
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription(description);
        embed.setTitle("Help");
        embed.setColor(Bot.embdedColor);
        event.replyEmbeds(embed.build()).queue();
    }
}
