package com.grow.bot.commands.server;

    import com.grow.Database.Database;
    import com.grow.Database.GuildRole;
    import com.grow.Database.Status;
    import com.grow.bot.Bot;
    import com.grow.bot.commands.SlashCommand;
    import com.grow.bot.commands.server.Listener.ConfirmServerChangeButtonListener;
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
        long guildId = event.getGuild().getIdLong();
        event.deferReply().setEphemeral(true).queue(); // Tell discord we received the command, send a thinking... message to the user

        //get Server supporter status list
        List<Status> serverStatusList = Database.getStatusList(guildId);
        if(serverStatusList.isEmpty()){
            //the Mods haven't set up a supporter status yet.
            event.getHook().sendMessage("the Mods haven't set up a supporter status yet").setEphemeral(true).queue();
            return;
        }
        long guildIdOfUserFromDatabase = Database.getGuildIdFromUser(userid);

        //check if the member is already a status supporter
            //get the member from the database

           // if member is in database
        if(Database.userIsInDB(userid)){
                /*if(member is status supporter in this guild)
                    msg: you are already a status supporter in this guild
                 */
            long guildIdFromUser = Database.getGuildIdFromUser(userid);
            if(guildIdFromUser== guildId){
                event.getHook().sendMessage("You are a status supporter in this server.").queue();
                return;
            }
            else{
                /*else:
                    //msg: you are still a status supporter in the server :....., do you really want to lose all your
                    //status supporter roles in this server and become a status member here?
                    Button yes no
                 */
                String currentStatusSupporterServerName = Bot.jda.getGuildById(guildIdFromUser).getName();
                event.getHook().sendMessage("You are still a status supporter in the server: "+currentStatusSupporterServerName +
                    ", do you really want to lose all your\nstatus supporter roles in "+currentStatusSupporterServerName+" and become a status member here?")
                    .addActionRow(
                        ConfirmServerChangeButtonListener.getServerChangeConfirmationButtons(userid, guildId)
                    ).queue();
                return;
            }
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

        List<GuildRole> guildRoles = Database.getGuildRoles(guild .getIdLong());

        //check if userStatus starts with Server supporter status
        for(Status s : serverStatusList){
            if(userStatus.equals(s.supporterStatus)){
                Database.addUser(userid,guildId);
                event.getHook().sendMessage("You are now a Status Supporter!").queue();
                //give roles
                for(GuildRole g : guildRoles){
                    if(g.days==0){
                        Role role = guild.getRoleById(g.roleId);
                        assert role != null;
                        event.getGuild().addRoleToMember(event.getMember(),role);
                        event.getUser().openPrivateChannel().queue(channel -> { // this is a lambda expression
                            // the channel is the successful response
                            channel.sendMessage("You now got the role %s in the server : %s".formatted(role .getName(),guild.getName())).queue();
                        });
                    }
                }
                return;
            }
        }
        //else error message: you customizable user status: \"%s/" doesn't start with [server support status]
        event.getHook().sendMessage("ERROR: you customizable user status is not equal to "+serverStatusList.get(0).supporterStatus).setEphemeral(true).queue();
    }
}
