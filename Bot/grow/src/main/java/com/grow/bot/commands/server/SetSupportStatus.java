package com.grow.bot.commands.server;

import com.grow.Database.Database;
import com.grow.bot.commands.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateVerifiedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class SetSupportStatus extends SlashCommand {
    public SetSupportStatus(String name, String description) {
        super(name, description);
        this.setOptionDataCollection(Collections.singletonList(
            new OptionData(OptionType.STRING, "status", "The status you want your members to have. (max 128 char)", true)));
    }

    @Override
    public void run(SlashCommandEvent event) {
        //check permission
        if(!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)){
            event.reply("you don't have the permission to manage the server").setEphemeral(true).queue();
            return;
        }
        String status = Objects.requireNonNull(event.getOption("status")).getAsString();
        long guildId = Objects.requireNonNull(event.getGuild()).getIdLong();
        if(status.length()>128){
            event.reply("The status can't be longer than 128 characters!").setEphemeral(true).queue();
            return;
        }
        //set the new status to the Database
        event.deferReply().queue(); // Tell discord we received the command, send a thinking... message to the user
        Database.addSupportStatus(guildId,status);
        if(Database.getStatusList(guildId).size()>1){
            event.getHook().sendMessage("The supporter status has successfully been change to :\n\n "+status+" \n\nThe members now have 48 hours to" +
                " change their status. Otherwise they will lose their status supporter roles.").queue();
        }else{
            event.getHook().sendMessage("The status has successfully been change to :\n\n "+status).queue();
        }

    }


}
