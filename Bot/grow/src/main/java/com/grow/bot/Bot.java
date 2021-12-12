package com.grow.bot;

import com.grow.bot.commands.CommandListener;
import com.grow.bot.commands.SlashCommand;
import com.grow.bot.commands.server.SetSupportStatus;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class Bot {
    static JDA jda= null;
    static boolean debug = true;
    public static List<SlashCommand> commandList = new ArrayList<SlashCommand>();
    public static void start() throws LoginException, InterruptedException {
        commandList.add(new SetSupportStatus("status", "Sets the supporter Status for this server"));

        // Note: It is important to register your ReadyListener before building
        jda = JDABuilder.createDefault("OTE3ODkxOTkxNjg4MzM5NDU2.Ya_TiA.htrqnJ0ywi6Y6ETka3YjBG2u9mE")
            .addEventListeners(new CommandListener())
            .build();


        // optionally block until JDA is ready
        jda.awaitReady();

        /*refresh all Commands ( only for debug)
        jda.updateCommands().queue();
        jda.getGuildById(817346279771340851L).updateCommands().queue();
        */

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
        }
    }

}
