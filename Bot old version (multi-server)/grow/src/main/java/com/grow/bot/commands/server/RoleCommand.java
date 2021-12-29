package com.grow.bot.commands.server;

import com.grow.Database.Database;
import com.grow.bot.commands.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.xml.crypto.Data;
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
            new OptionData(OptionType.INTEGER, "days", "Supporters need a streak of this amount of days to get this role. ")));
    }


    @Override
    public void run(SlashCommandEvent event) throws Exception {

        //check permission
        if(!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)){
            event.reply("you don't have the permission to manage the server").setEphemeral(true).queue();
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
        if(event.getOption("days")!=null){
            if(Objects.requireNonNull(event.getOption("days")).getAsLong()>5000
                | Objects.requireNonNull(event.getOption("days")).getAsLong()<0){

                event.reply("The number of days must be between 0 and 5000").setEphemeral(true).queue();
                return;
            }else{
                days = Integer.parseInt(String.valueOf(Objects.requireNonNull(event.getOption("days")).getAsLong()));
            }
        }


        //check if the role is already used --> error message
        if(Database.roleIsInDatabase(event.getGuild().getIdLong(), role.getIdLong())){
            event.reply("The role %s has has already been added.".formatted(role.getName())).setEphemeral(true).queue();
        }else{
            //add role
            Database.addRole(event.getGuild().getIdLong(),role.getIdLong(),days);
            event.reply("The role %s has been added to status supporter roles list".formatted(role.getName())).queue();
        }
    }

    void removeRole(SlashCommandEvent event) throws Exception {
        Role role= event.getOption("role").getAsRole();
        long guildId = event.getGuild().getIdLong();

        //if  the role is not in the status supporter role list
        if(!Database.roleIsInDatabase(guildId,role.getIdLong())){
            event.reply("The role %s is not in the status supporter roles list".formatted(role.getName())).setEphemeral(true).queue();

        }else{
            Database.deleteRole(guildId,role.getIdLong());
            event.reply("The role %s hast been removed from the status supporter roles list".formatted(role.getName())).queue();
        }


    }

    void updateDays(SlashCommandEvent event) throws Exception {
        Role role= event.getOption("role").getAsRole();
        long guildId = event.getGuild().getIdLong();

        if(!Database.roleIsInDatabase(guildId,role.getIdLong())){
            event.reply("The role %s is not in the status supporter roles list".formatted(role.getName())).setEphemeral(true).queue();
            return;
        }
        int days = 0;
        if(event.getOption("days")==null){
            event.reply("Please provide the days parameter to update the days of a role".formatted(role.getName())).setEphemeral(true).queue();
        }
        else{
            if(event.getOption("days").getAsLong()>5000
                | event.getOption("days").getAsLong()<0){

                event.reply("The number of days must be between 0 and 5000").setEphemeral(true).queue();
            }else{
                days = Integer.parseInt(String.valueOf(Objects.requireNonNull(event.getOption("days")).getAsLong()));
                Database.updateDays(guildId,role.getIdLong(),days);
                event.reply("The Number of days of the role %s has been updated to %s".formatted(role.getName(),days)).queue();
            }
        }
    }

}
