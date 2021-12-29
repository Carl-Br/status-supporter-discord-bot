package com.grow.bot.commands.server;

import com.grow.Database.Database;
import com.grow.Database.GuildRole;
import com.grow.Database.Status;
import com.grow.bot.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Collections;
import java.util.List;

public class ServerInfo extends SlashCommand {
    public ServerInfo(String name, String description) {
        super(name, description);
    }

    @Override
    public void run(SlashCommandEvent event) throws Exception {

        EmbedBuilder embed = new EmbedBuilder();
        embed .setTitle("server info");
        long guildId = event.getGuild().getIdLong();
        //get amount of status supporter
        int statusSupporterCount = Database.getStatusSupporterCount(guildId);
        embed.addField("Amount of status supporter :",String.valueOf(statusSupporterCount)+"\n\n------------------",true);

        //get every status
        List<Status> statusList = Database.getStatusList(guildId);
        Collections.reverse(statusList);
        //current status = statusList.get(0);
        String latestStatus="";
        if(statusList.size()!=0){
            latestStatus=statusList.get(0).supporterStatus;
        }
        embed.addField("current status support message :",latestStatus,true);

        //outdated status = statusList (without the first one)

        //roles
        List<GuildRole> roleStatus = Database.getGuildRoles(guildId);
        embed.addField("Roles :","",false);
        String rolenames ="";
        String days ="";
        for(GuildRole g : roleStatus){

            Role role = event.getGuild().getRoleById(g.roleId);
            //The role doesn't exist anymore
            if(role==null){
                //delete the role from the database
                Database.deleteRole(guildId,g.roleId);
                continue;
            }

            rolenames+=role.getName()+"\n";
            days += g.days+" days\n";
        }
        embed.addField("name",rolenames,true);
        embed.addField("required streak",days,true);

        event.replyEmbeds(embed.build()).queue();
    }
}
