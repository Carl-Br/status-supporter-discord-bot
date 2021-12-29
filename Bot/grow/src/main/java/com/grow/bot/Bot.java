package com.grow.bot;

import com.grow.Database.Database;
import com.grow.bot.commands.CommandListener;
import com.grow.bot.commands.SlashCommand;
import com.grow.bot.commands.server.ApproveUserStatus;
import com.grow.bot.commands.server.RoleCommand;
import com.grow.bot.commands.server.ServerInfo;
import com.grow.bot.commands.server.SetSupportStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.util.Locale;

public class Bot {
    public static long guildId = 0;
    public static Color embdedColor = Color.blue;
    public static Guild guild = null;
    public static JDA jda= null;
    public static List<SlashCommand> commandList = new ArrayList<SlashCommand>();
    public static void start() throws LoginException, InterruptedException {
        // Note: It is important to register your ReadyListener before building
        jda = JDABuilder.createDefault("OTE3ODkxOTkxNjg4MzM5NDU2.Ya_TiA.htrqnJ0ywi6Y6ETka3YjBG2u9mE", GatewayIntent.GUILD_PRESENCES,GatewayIntent.GUILD_MEMBERS)
            .addEventListeners(new CommandListener())
            .enableCache(CacheFlag.ACTIVITY)
            .build();
            //https://discord.com/developers/applications/917891991688339456/oauth2/url-generator


        // optionally block until JDA is ready
        jda.awaitReady();

        //add commands
        commandList.add(new SetSupportStatus("set_server_status", "Sets the supporter Status for this server"));
        commandList.add(new RoleCommand("role","This command lets you manage all the roles."));
        commandList.add(new ApproveUserStatus("approve_status","Approves your status"));
        commandList.add(new ServerInfo("info","Shows: status supporter count, current status supporter message, the roles "));

        /*refresh all Commands ( only for debug)
        jda.updateCommands().queue();
        */

        //jda.getGuildById(817346279771340851L).updateCommands().queue();

        //set guild Id
        guildId=817346279771340851L;

        //set all my Commands from the commandList
        updateCommands();
        guild = jda.awaitReady().getGuildById(guildId);
        System.out.println("The discord Bot is now online");


        /*constantly check the status of the users and either give them roles or remove all the roles.
        new Thread(() -> {
            StatusChecker statusChecker = new StatusChecker(jda);
            try{
                statusChecker.start();
            }catch (Exception e){
                System.out.println("\n\nThere was an exception while performing the statusChecker.start() Methode :\n");
                e.printStackTrace();
                System.out.println("\n\n");
                try {
                    Thread.sleep(60000);
                    System.out.println("Waiting 60 seconds...");
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }).start();
        */


        //delete old datasets
        //new Thread(Database::clearServerTable).start();
    }
    public  static void shutdown(){
        jda.shutdown();
        System.out.println("The discord Bot is now offline");
    }
    public static void updateCommands(){
        //jda.getGuildById(guildId).updateCommands().queue();
        for (SlashCommand slashCommand:commandList) {
            jda.getGuildById(guildId).upsertCommand(slashCommand.getCommandData()).queue();
        }
    }

    public static EmbedBuilder getReplyEmbed(String title, String description){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(embdedColor);
        embed.setTitle(title);
        embed.setDescription(description+="\n\n[support server](https://discord.gg/9gWBUpvfvj)");


        if(title.toLowerCase().contains("success"))
            embed.setThumbnail("https://cdn.discordapp.com/attachments/817346280250540034/925874866257801256/hook-1727484.png");
        else if(title.toLowerCase().contains("error"))
            embed.setThumbnail("https://cdn.discordapp.com/attachments/817346280250540034/925875821971922944/false-2061132.png");

        return embed;
    }

}
