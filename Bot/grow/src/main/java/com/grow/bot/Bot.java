package com.grow.bot;

import com.grow.Database.Database;
import com.grow.bot.commands.CommandListener;
import com.grow.bot.commands.SlashCommand;
import com.grow.bot.commands.server.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.security.auth.login.LoginException;
import java.io.FileReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.util.Locale;

public class Bot {

    //config
    private static String token = "";
    public static int maxReqPerSecondStatusChecker = 2;
    public static long guildId = 0;
    public static Color embdedColor = Color.blue;

    public static Thread statusCheckerThread;
    public static Thread clearDatabaseThread;

    public static Guild guild = null;
    public static JDA jda= null;
    public static List<SlashCommand> commandList = new ArrayList<SlashCommand>();
    public static void start() throws LoginException, InterruptedException {

        //gets the config from the config.json
        setupConfig();

        // Note: It is important to register your ReadyListener before building
        jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_PRESENCES,GatewayIntent.GUILD_MEMBERS)
            .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
            .addEventListeners(new CommandListener())
            .enableCache(CacheFlag.ACTIVITY)
            //.setHttpClientBuilder and setHttpClient
            .build();
            //https://discord.com/developers/applications/917891991688339456/oauth2/url-generator


        // optionally block until JDA is ready
        jda.awaitReady();

        //add commands
        commandList.add(new SetSupportStatus("set_server_status", "Sets the supporter Status for this server"));
        commandList.add(new RoleCommand("role","This command lets you manage all the roles."));
        commandList.add(new ApproveUserStatus("approve_status","Approves your status"));
        commandList.add(new ServerInfo("info","Shows: status supporter count, current status supporter message, the roles "));
        commandList.add(new MyStreak("my_streak","The streak of days on how long you have been a status supporter"));
        commandList.add(new Help("help","help"));

        /*refresh all Commands ( only for debug)
        jda.updateCommands().queue();
        */


        //set all my Commands from the commandList
        guild = jda.awaitReady().getGuildById(guildId);
        updateCommands();
        System.out.println("The discord Bot is now online");


        //constantly check the status of the users and either give them roles or remove all the roles.

        StatusChecker.startRequestManagerThread();

        statusCheckerThread = new Thread(() -> {

            System.out.println("started status checker thread");

            while (true){
                try {
                    StatusChecker.checkMembers();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException ( The Thread didn't wait a sec after checking the table )");
                }
            }
        });
        statusCheckerThread.start();





        //delete old datasets
        clearDatabaseThread = new Thread(()->{

            System.out.println("started clearDatabase thread");

            while (true){
                Database.clearServerTable();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("InterruptedException ( The Thread didn't wait a sec after checking the table )");
                }
            }
        });
        clearDatabaseThread.start();
    }
    public  static void shutdown(){
        jda.shutdown();
        System.out.println("The discord Bot is now offline");
    }
    public static void updateCommands(){
        //jda.getGuildById(guildId).updateCommands().queue();
        for (SlashCommand slashCommand:commandList) {
           guild.upsertCommand(slashCommand.getCommandData()).queue();
        }
    }

    public static EmbedBuilder getReplyEmbed(String title, String description){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(embdedColor);
        embed.setTitle(title);
        embed.setDescription(description+="\n\n• [test/support server](https://discord.gg/9gWBUpvfvj)\n• [github docs](https://github.com/Carl-Br/status-supporter-discord-bot)");


        if(title.toLowerCase().contains("success"))
            embed.setThumbnail("https://cdn.discordapp.com/attachments/817346280250540034/925874866257801256/hook-1727484.png");
        else if(title.toLowerCase().contains("error"))
            embed.setThumbnail("https://cdn.discordapp.com/attachments/817346280250540034/925875821971922944/false-2061132.png");

        return embed;
    }

    public static void setupConfig(){
        JSONParser parser = new JSONParser();
        try{
            JSONObject jsonObject = (JSONObject)parser.parse(new FileReader(".//config.json"));
            token = (String)jsonObject.get("token");
            System.out.println("token: "+token);

            guildId = Long.parseLong((String)jsonObject.get("guildId"));
            System.out.println("guildId: "+guildId);


            String[] rgb = jsonObject.get("embedColor").toString().split(",");
            embdedColor = new Color(Integer.parseInt(rgb[0]),Integer.parseInt(rgb[1]),Integer.parseInt(rgb[2]));

            maxReqPerSecondStatusChecker = Integer.parseInt((String)jsonObject.get("maxReqPerSecondStatusChecker"));

            if(maxReqPerSecondStatusChecker >14 || maxReqPerSecondStatusChecker <=0){
                throw new Exception("maxReqPerSecondStatusChecker has to be between 1 and 14");
            }

        }catch(Exception e){
            e.printStackTrace();
            shutdown();
        }


    }

}
