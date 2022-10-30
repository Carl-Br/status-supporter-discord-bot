package com.grow.bot.commands.server;

import com.grow.Database.Database;
import com.grow.Database.GuildRole;
import com.grow.Database.Status;
import com.grow.bot.Bot;
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


        //get amount of status supporter
        int statusSupporterCount = Database.getStatusSupporterCount();

        //get every status
        List<Status> statusList = Database.getStatusList();
        Collections.reverse(statusList);
        //current status = statusList.get(0);
        String latestStatus="";
        if(statusList.size()!=0){
            latestStatus=statusList.get(0).supporterStatus;
        }

        //outdated status = statusList (without the first one)

        //roles
        List<GuildRole> roleStatus = Database.getGuildRoles();
        String rolesOutput ="";
        int i = 0;
        for(GuildRole g : roleStatus){
            Role role = event.getGuild().getRoleById(g.roleId);
            //The role doesn't exist anymore
            if(role==null){
                //delete the role from the database
                Database.deleteRole(g.roleId);
                continue;
            }
            i++;
            rolesOutput+="** "+i+". "+role.getName()+"**";
            if(g.days!=0){
                if(g.days==1)
                    rolesOutput+=" (requires a streak of 1 day)";
                else
                    rolesOutput+=" (requires a streak of %s days)".formatted(g.days);
            }
            rolesOutput+="\n";
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("╔════════╗\n╟» server Info\n╚════════╝");//<------ contains 2 invisible unicode chars
        embed.setDescription("• [test/support server](https://discord.gg/9gWBUpvfvj)\n• [github docs](https://github.com/Carl-Br/status-supporter-discord-bot)\n\n┌─────────────\n├» Status supporter : %s\n└─────────────\n".formatted(statusSupporterCount));
        embed.addField("┌──────────┐ \n├» support status\n└──────────┘\n","**\"** "+latestStatus+" **\"**",true);
        embed.addField("⠀\n⠀\n┌─────┐\n├» Roles \n└─────┘\n",rolesOutput,false);//<------ contains 2 invisible unicode chars to make empty lines
        embed.setColor(Bot.embdedColor);

        event.replyEmbeds(embed.build()).queue();
    }
}
