package com.grow.bot;

import com.grow.bot.commands.CommandListener;
import com.grow.bot.commands.SlashCommand;
import com.grow.bot.commands.server.ApproveUserStatus;
import com.grow.bot.commands.server.Listener.ConfirmServerChangeButtonListener;
import com.grow.bot.commands.server.RoleCommand;
import com.grow.bot.commands.server.SetSupportStatus;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class Bot {
    public static JDA jda= null;
    static boolean debug = true;
    public static List<SlashCommand> commandList = new ArrayList<SlashCommand>();
    public static void start() throws LoginException, InterruptedException {
        // Note: It is important to register your ReadyListener before building
        jda = JDABuilder.createDefault("OTE3ODkxOTkxNjg4MzM5NDU2.Ya_TiA.htrqnJ0ywi6Y6ETka3YjBG2u9mE", GatewayIntent.GUILD_PRESENCES)
            .addEventListeners(new CommandListener())
            .addEventListeners(new ConfirmServerChangeButtonListener())
            .enableCache(CacheFlag.ACTIVITY)
            .build();
            //https://discord.com/developers/applications/917891991688339456/oauth2/url-generator


        // optionally block until JDA is ready
        jda.awaitReady();

        //add commands
        commandList.add(new SetSupportStatus("server_status", "Sets the supporter Status for this server"));
        commandList.add(new RoleCommand("role","This command lets you manage all the roles."));
        commandList.add(new ApproveUserStatus("approve_status","Approves your status"));

        /*refresh all Commands ( only for debug)
        jda.updateCommands().queue();
        */

        //jda.getGuildById(817346279771340851L).updateCommands().queue();

        //set all my Commands from the commandList
        updateCommands();
        System.out.println("The discord Bot is now online");
    }
    public  static void shutdown(){
        jda.shutdown();
        System.out.println("The discord Bot is now offline");
    }
    public static void updateCommands(){
        for (SlashCommand slashCommand:commandList) {
            if(!debug){
                jda.upsertCommand(slashCommand.getCommandData()).queue();
            }
            jda.getGuildById(817346279771340851L).upsertCommand(slashCommand.getCommandData()).queue();
            jda.getGuildById(922950132381667328L).upsertCommand(slashCommand.getCommandData()).queue();
        }
    }

}
