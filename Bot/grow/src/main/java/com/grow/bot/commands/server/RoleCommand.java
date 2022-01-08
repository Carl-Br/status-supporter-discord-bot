package com.grow.bot.commands.server;

import com.grow.Database.Database;
import com.grow.bot.Bot;
import com.grow.bot.commands.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.*;

public class RoleCommand extends SlashCommand {

    public RoleCommand(String name, String description) {
        super(name, description);
        this.setOptionDataCollection(Arrays.asList(
            new OptionData(OptionType.STRING, "action", "Add role, remove role, edit day", true)
            .addChoice("add","add")
            .addChoice("remove","remove")
                .addChoice("edit","edit"),
            new OptionData(OptionType.ROLE, "role", "The role you would like to give people.", true),
            new OptionData(OptionType.INTEGER, "required_streak_of_days", "Supporters need a streak of this amount of days to get this role. ")));
    }


    @Override
    public void run(SlashCommandEvent event) throws Exception {


        //check permission
        if(!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)){

            event.replyEmbeds(Bot.getReplyEmbed("error",
                "You don't have the permission to manage the server.").build()).setEphemeral(true).queue();
            return;
        }

        //else

        String action = Objects.requireNonNull(event.getOption("action")).getAsString();
        if(action.equals("add")){
            addRole(event);
        }else if(action.equals("remove")){
            removeRole(event);
        }else if(action.equals("edit")){
            updateDays(event);
        }


    }

    void addRole(SlashCommandEvent event) throws Exception {
        Role role= event.getOption("role").getAsRole();
        int days = 0;
        OptionMapping  requiredStreakOption = event.getOption("required_streak_of_days");
        if( requiredStreakOption!=null){
            if( requiredStreakOption.getAsLong()>5000 |  requiredStreakOption.getAsLong()<0){

                event.replyEmbeds(Bot.getReplyEmbed("error",
                    "The number of days must be between 0 and 5000.").build()).setEphemeral(true).queue();
                return;
            }else{
                days = Integer.parseInt( requiredStreakOption.getAsString());
            }
        }
        if(role.getPosition()>Bot.guild.getBotRole().getPosition()) {
            event.replyEmbeds(Bot.getReplyEmbed("error",
                "This role is higher the my role! So I can't give it to anyone.".formatted(role.getName())).build()).setEphemeral(true).queue();
            return;
        }



        //check if the role is already used --> error message
        if(Database.roleIsInDatabase(role.getIdLong())){

            event.replyEmbeds(Bot.getReplyEmbed("error",
                "The role %s has already been added.".formatted(role.getName())).build()).setEphemeral(true).queue();

        }else{
            //add role
            Database.addRole(role.getIdLong(),days);
            event.replyEmbeds(Bot.getReplyEmbed("success",
                "The role %s has been added to status supporter roles list.".formatted(role.getName())).build()).setEphemeral(true).queue();
        }
    }

    void removeRole(SlashCommandEvent event) throws Exception {
        Role role= event.getOption("role").getAsRole();

        //if  the role is not in the status supporter role list
        if(!Database.roleIsInDatabase(role.getIdLong())){

            event.replyEmbeds(Bot.getReplyEmbed("error",
                "The role %s is not in the status supporter roles list".formatted(role.getName())).build()).setEphemeral(true).queue();

        }else{
            Database.deleteRole(role.getIdLong());
            event.replyEmbeds(Bot.getReplyEmbed("success",
                "The role %s hast been removed from the status supporter roles list.".formatted(role.getName())).build()).setEphemeral(true).queue();
        }


    }

    void updateDays(SlashCommandEvent event) throws Exception {
        Role role= event.getOption("role").getAsRole();

        OptionMapping  requiredStreakOption = event.getOption("required_streak_of_days");

        if(!Database.roleIsInDatabase(role.getIdLong())){
            event.replyEmbeds(Bot.getReplyEmbed("error",
                "The role %s is not in the status supporter roles list.".formatted(role.getName())).build()).setEphemeral(true).queue();
            return;
        }
        int days = 0;
        if(requiredStreakOption==null){
            event.replyEmbeds(Bot.getReplyEmbed("error",
                "Please provide the required_streak_of_days parameter to update the required streak of days to get this role a role.".formatted(role.getName())).build()).setEphemeral(true).queue();
            return;
        }

        if(requiredStreakOption.getAsLong()>5000 | requiredStreakOption.getAsLong()<0){

            event.replyEmbeds(Bot.getReplyEmbed("error",
                "The number of required_streak_of_days must be between 0 and 5000.").build()).setEphemeral(true).queue();

        }else{
            days = Integer.parseInt((requiredStreakOption).getAsString());
            Database.updateDays(role.getIdLong(),days);
            event.replyEmbeds(Bot.getReplyEmbed("success",
                "The the required streak of days to get the role %s has been updated to %s.".formatted(role.getName(),days)).build()).setEphemeral(true).queue();
        }

    }

}
