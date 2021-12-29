package com.grow.bot;

import com.grow.Database.Database;
import com.grow.Database.User;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;


public class StatusChecker {

    JDA jda =  null;
    public StatusChecker(JDA jda){
        this.jda =  jda;
    }
    public  void start() throws SQLException, ParseException, InterruptedException {

        //while true don't stop checking
        while(true){
        //iterate threw all the users
            List<User> usersList = Database.getAllUsers();
            //for each user
                for(int i = 0 ; i<usersList.size();i++) {
                    //not more than 10 req/sec
                    if(i%40==0){
                        Thread.sleep(950);
                    }


                    //get the current status from the user with only one request!
                    List<Activity> membersActivity = jda.getGuildById(usersList.get(i).guildId).getMemberById(usersList.get(i).userId).getActivities();

                    //if they are not offline:
                    for(Activity a : membersActivity){
                        if(a.getType().equals( Activity.ActivityType.)){
                            userStatus = a.getName();
                        }
                    }


                    //check if they have one of the status their are supposed to have the status they are supposed to have
                    //true: check if they should get another role

                    //false: remove all their status supporter roles
                    //delete the User from the database
                    /*send a message to the user, that he is not a status supporter anymore and that he lost all his roles
                       because he changed his status or the server changed the status and he didn't change his status with in
                       48 hours*/
                }

        }
    }
}
