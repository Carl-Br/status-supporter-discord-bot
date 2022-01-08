package com.grow.bot;

import com.grow.Database.Database;
import com.grow.Database.GuildRole;
import com.grow.Database.Status;
import com.grow.Database.User;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;


public class StatusChecker {

    public static int availableRequests = 0;

    JDA jda = null;

    public StatusChecker(JDA jda) {
        this.jda = jda;
    }

    public static void checkMembers() throws SQLException, ParseException, InterruptedException {


        //iterate threw all the users
        List<User> usersList = Database.getAllUsers();

        if(usersList.isEmpty()){
            Thread.sleep(10000);
            return;
        }
        //for each user
        for (int i = 0; i < usersList.size(); i++) {


            waitUntilReqAvailable();
            Member member = Bot.guild.getMemberById(usersList.get(i).userId);//req 1

            waitUntilReqAvailable();
            //if member is offline
            if (member.getOnlineStatus().name().equals("OFFLINE")) //req 2
                break;

            waitUntilReqAvailable();
            List<Activity> membersActivity = Bot.guild.getMemberById(usersList.get(i).userId).getActivities();

            String userStatus = "";
            for (Activity a : membersActivity) {
                if (a.getType().equals(Activity.ActivityType.CUSTOM_STATUS)) {
                    userStatus = a.getName();
                }
            }

            //check if they have one of the status their are supposed to have

            boolean memberHasCorrectStatus = false;
            for(Status s : Database.getStatusList()){
                if(s.supporterStatus.equals(userStatus)){
                    memberHasCorrectStatus = true;
                    break;
                }
            }

            //The Member has a correct status
            if( memberHasCorrectStatus){
                //check if they should get another role
                waitUntilReqAvailable();
                List<Role> memberRoles = member.getRoles();
                for(GuildRole role : Database.getGuildRoles()){

                    //check if the member already has the role
                    boolean alreadyHasRole = false;
                   for(Role r : memberRoles){
                       if(r.getIdLong()==role.roleId){
                           alreadyHasRole = true;//Member already has this role
                       }
                   }
                   if(alreadyHasRole)continue;

                   //check if the member has the user status long enough to get this role
                    if(usersList.get(i).timeAdded.getTime()>new Date().getTime()-(1000L*60*60*24*role.days)) {//if [the time the user set the status] > [required seconds -  current timestamp]
                        //add the role to the user
                        waitUntilReqAvailable();
                        Bot.guild.addRoleToMember(member, Bot.guild.getRoleById(role.roleId)).queue();
                    }
                }
            }else{
                //remove all their status supporter roles
                for(GuildRole role : Database.getGuildRoles()){
                    waitUntilReqAvailable();
                    Bot.guild.removeRoleFromMember(member, Bot.guild.getRoleById(role.roleId)).queue();
                }
                //delete the User from the database
                Database.removeUser(member.getIdLong());
            }


        }

    }

    static boolean alreadyStartedReqManagerThread = false;
    public static void startRequestManagerThread(){
        if(!alreadyStartedReqManagerThread){//only execute this thread once!
            alreadyStartedReqManagerThread  = true;
            new Thread(()->{
                while (true){
                    try {
                        Thread.sleep(1000);
                        availableRequests = Bot.maxReqPerSecondStatusChecker;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static void waitUntilReqAvailable() {
        while(availableRequests <= 0){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        availableRequests-=1;
        //System.out.println("ready for req");
    }
}
