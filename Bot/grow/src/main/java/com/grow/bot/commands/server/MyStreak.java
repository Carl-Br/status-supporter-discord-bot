package com.grow.bot.commands.server;

import com.grow.Database.Database;
import com.grow.Database.User;
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

        User user = Database.getUser(event.getUser().getIdLong());

        //if the user is not in the Database
        if(user==null){
            EmbedBuilder embed = Bot.getReplyEmbed("You are not a status supporter!","Do /help on how to become a status supporter");
            event.replyEmbeds(embed.build()).queue();
            return;
        }

        long userStatusGotApprovedMs =  user.timeAdded.getTime();
        long currentMs = new Date().getTime();

        long streakInMs = currentMs-userStatusGotApprovedMs;

        int streakInDays = (int)(streakInMs / (1000*60*60*24));

        EmbedBuilder embed = Bot.getReplyEmbed("You have a Streak of: ",String.valueOf(streakInDays)+" days");
        event.replyEmbeds(embed.build()).queue();

    }
}
