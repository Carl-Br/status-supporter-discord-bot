package com.grow.bot.commands.server;

import com.grow.Database.Database;
import com.grow.Database.Status;
import com.grow.bot.Bot;
import com.grow.bot.commands.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.Objects;

public class SetSupportStatus extends SlashCommand {
    public SetSupportStatus(String name, String description) {
        super(name, description);
        this.setOptionDataCollection(Collections.singletonList(
            new OptionData(OptionType.STRING, "status", "The status you want your members to have. (max 128 char)", true)));
    }

    @Override
    public void run(SlashCommandEvent event) throws Exception{

        //event.getGuild().getOwnerId();
        //check permission
        if(!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)){
            event.replyEmbeds(Bot.getReplyEmbed("error",
                "You don't have the permission to manage the server.").build()).setEphemeral(true).queue();
            return;
        }
        String status = Objects.requireNonNull(event.getOption("status")).getAsString();

        //remove every double spaces from the status because you can't use double spaces in discord
        while(status.contains("  ")){
            status = status.replaceAll("  "," ");
        }

        long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
        if(status.length()>128){
            event.replyEmbeds(Bot.getReplyEmbed("error",
                "The status can't be longer than 128 characters!").build()).setEphemeral(true).queue();
            return;
        }
        //set the new status to the Database
        Status s = Database.getLatestStatus();
        if(s!=null){
            if(s.supporterStatus.equals(status)){
                event.replyEmbeds(Bot.getReplyEmbed("error",
                    "This is already the server status.").build()).setEphemeral(true).queue();
                return;
            }
        }
        Database.addSupportStatus(status);
        if(Database.getStatusList().size()>1){
            event.replyEmbeds(Bot.getReplyEmbed("success",
                "The supporter status has successfully been change to: "+status+"" +
                    "\nThe members now have 48 hours to  change their status. Otherwise they will lose their status supporter roles. Make sure to tell them!")
                .build()).setEphemeral(true).queue();

        }else{
            event.replyEmbeds(Bot.getReplyEmbed("success",
                "The status has successfully been change to: "+status+".").build()).setEphemeral(true).queue();
            return;
        }

    }


}
