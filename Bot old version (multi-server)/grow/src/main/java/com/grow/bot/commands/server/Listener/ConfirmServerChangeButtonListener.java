package com.grow.bot.commands.server.Listener;

import com.grow.Database.Database;
import com.grow.Database.GuildRole;
import com.grow.Database.Status;
import com.grow.bot.Bot;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfirmServerChangeButtonListener extends ListenerAdapter {

    public static Button getServerChangeConfirmationButtons(long userId, long guildId){

        return Button.success("confirmServerChange %s %s %s".formatted(true,userId,guildId), Emoji.fromUnicode("✔️"));
    }
    @Override
    public void onButtonClick(ButtonClickEvent event) {

        try{
            long userid = event.getUser().getIdLong();

            //if component id starts with confirmServerChange
            String componentId = event.getComponentId();
            if (event.getComponentId().startsWith("confirmServerChange")) {
                String[] response = componentId.split(" ");
                boolean changeServer = Boolean.parseBoolean(response[1]);
                long userId = Long.parseLong(response[2]);
                long guildId = Long.parseLong(response[3]);

                if(userId != event.getUser().getIdLong() || guildId!=event.getGuild().getIdLong()){
                    event.reply("Something went wrong!").setEphemeral(true).queue();
                    return;
                }

                if(changeServer){
                    // try to approve user:

                    event.deferReply().setEphemeral(true).queue(); // Tell discord we received the command, send a thinking... message to the user

                    //get Server supporter status list
                    List<Status> serverStatusList = Database.getStatusList(guildId);
                    if(serverStatusList.isEmpty()){
                        //the Mods haven't set up a supporter status yet.
                        event.getHook().sendMessage("the Mods haven't set up a supporter status yet").setEphemeral(true).queue();
                        return;
                    }

                    //check if the member is already a status supporter
                    //get the member from the database

                    // if member is in database
                    try{
                        if(Database.userIsInDB(userId)) {
                /*if(member is status supporter in this guild)
                    msg: you are already a status supporter in this guild
                 */
                            long guildIdFromUser = Database.getGuildIdFromUser(userId);
                            if(guildIdFromUser== guildId){
                                event.getHook().sendMessage("You are a status supporter in this server.").queue();
                                return;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }



                    //get user Status
                    String userStatus = null;
                    for(Activity a : event.getMember().getActivities()){
                        if(a.getType().equals( Activity.ActivityType.CUSTOM_STATUS)){
                            userStatus = a.getName();
                        }
                    }
                    if(userStatus==null){
                        event.getHook().sendMessage("You don't have any user status. Make sure that you are not set to offline.").setEphemeral(true).queue();
                        return;
                    }


                    Guild guild = event.getGuild();

                    List<GuildRole> guildRoles = Database.getGuildRoles(guildId);
                    String latestServerStatus = Database.getLatestStatus(guildId).supporterStatus;//the status the member has to apply

                    //check if userStatus starts with Server supporter status
                    if(userStatus.equals(latestServerStatus)){
                        Database.addOrUpdateUser(userid,guildId);
                        event.getHook().sendMessage("You are now a Status Supporter!").queue();
                        System.out.println(guildRoles.size());
                        //give roles
                        for(GuildRole g : guildRoles){
                            if(g.days==0){
                                Role role = guild.getRoleById(g.roleId);
                                //The role doesn't exist anymore
                                if(role==null){
                                    //delete the role from the database
                                    try {
                                        Database.deleteRole(guildId,g.roleId);
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                        throw new RuntimeException(throwables);
                                    }
                                    continue;
                                }
                                event.getGuild().addRoleToMember(event.getMember(),role).queue();
                                event.getUser().openPrivateChannel().queue(channel -> { // this is a lambda expression
                                    // the channel is the successful response
                                    channel.sendMessage("You now got the role %s in the server : %s".formatted(role .getName(),guild.getName())).queue();
                                });
                            }
                        }
                        return;
                    }

                    //else error message: you customizable user status: \"%s/" doesn't start with [server support status]
                    event.getHook().sendMessage("ERROR: you customizable user status is not equal to "+latestServerStatus).setEphemeral(true).queue();

                }
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
