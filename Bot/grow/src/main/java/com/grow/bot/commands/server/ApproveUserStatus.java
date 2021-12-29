package com.grow.bot.commands.server;

    import com.grow.Database.Database;
    import com.grow.Database.GuildRole;
    import com.grow.Database.Status;
    import com.grow.bot.Bot;
    import com.grow.bot.commands.SlashCommand;
    import net.dv8tion.jda.api.EmbedBuilder;
    import net.dv8tion.jda.api.entities.*;
    import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
    import net.dv8tion.jda.api.interactions.components.Button;

    import java.util.List;

public class ApproveUserStatus extends SlashCommand {
    public ApproveUserStatus(String name, String description) {
        super(name, description);
    }

    @Override
    public void run(SlashCommandEvent event) throws Exception{

        long userid = event.getUser().getIdLong();


        //get Server supporter status list
        List<Status> serverStatusList = Database.getStatusList();
        if(serverStatusList.isEmpty()){
            //the Mods haven't set up a supporter status yet.
            event.replyEmbeds(Bot.getReplyEmbed("error","The mods haven't set up a supporter status yet.").build()).setEphemeral(true).queue();
            return;
        }

        //check if the member is already a status supporter
            //get the member from the database

           // if member is in database
        if(Database.userIsInDB(userid)){
            event.replyEmbeds(Bot.getReplyEmbed("success",
                "You are a status supporter in this server.").build()).queue();
            return;
        }

        //get user Status
        String userStatus = null;
        for(Activity a : event.getMember().getActivities()){
            if(a.getType().equals( Activity.ActivityType.CUSTOM_STATUS)){
                userStatus = a.getName();
            }
        }
        if(userStatus==null){
            event.replyEmbeds(Bot.getReplyEmbed("error",
                "You don't have any user status. Make sure that you are not set to offline.").build()).setEphemeral(true).queue();
            return;
        }

        //get the guild
        Guild guild = event.getGuild();

        // get the supporter roles
        List<GuildRole> guildRoles = Database.getGuildRoles();
        String latestServerStatus = Database.getLatestStatus().supporterStatus;//the status the member has to apply

        //check if userStatus starts with Server supporter status
        if(userStatus.equals(latestServerStatus)){
            Database.addOrUpdateUser(userid);


            event.replyEmbeds(Bot.getReplyEmbed("success",
                "You are now a Status Supporter!").build()).queue();

            //give roles
            for(GuildRole g : guildRoles){
                if(g.days==0){
                    Role role = guild.getRoleById(g.roleId);

                    //The role doesn't exist anymore
                    if(role==null){
                        //delete the role from the database
                        Database.deleteRole(g.roleId);
                        continue;
                    }

                    event.getGuild().addRoleToMember(event.getMember(),role).queue();
                    event.getUser().openPrivateChannel().queue(channel -> { // this is a lambda expression
                        // the channel is the successful response
                        channel.sendMessage("You now got the role %s in the server : %s.".formatted(role .getName(),guild.getName())).queue();
                    });
                }
            }
               return;
        }

        //else error message: you customizable user status: \"%s/" doesn't start with [server support status]

        event.replyEmbeds(Bot.getReplyEmbed("error",
            "Your customizable user status is not equal to: "+latestServerStatus+".").build()).setEphemeral(true).queue();
    }
}
