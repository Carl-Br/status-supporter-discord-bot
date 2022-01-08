package com.grow.bot.commands.server;

import com.grow.Database.Database;
import com.grow.bot.Bot;
import com.grow.bot.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Date;

public class MyStreak extends SlashCommand {
    public MyStreak(String name, String description) {
        super(name, description);
    }

    @Override
    public void run(SlashCommandEvent event) throws Exception {

        long userStatusGotApprovedMs =  Database.getUser(event.getUser().getIdLong()).timeAdded.getTime();
        long currentMs = new Date().getTime();

        long streakInMs = currentMs-userStatusGotApprovedMs;

        int streakInDays = (int)(streakInMs / (1000*60*60*24));

        EmbedBuilder embed = Bot.getReplyEmbed("You have a Streak of: ",String.valueOf(streakInDays)+" days");
        event.replyEmbeds(embed.build()).queue();

    }
}
