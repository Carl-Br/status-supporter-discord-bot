package com.grow;

import com.grow.Database.Database;
import com.grow.bot.Bot;

import javax.security.auth.login.LoginException;
import java.util.Locale;
import java.util.Scanner;

public final class App {

    public static void main(String[] args) {

        try {
            Bot.start();
            Database.connect();
        } catch (LoginException |InterruptedException e) {
            e.printStackTrace();
        }

        //wait for console commands
        Scanner userInput = new Scanner(System.in);
        while(true) {
            System.out.println("Ready for a new command sir.");

            String input = userInput.nextLine();
            if(input.toLowerCase().equals("shutdown")){
                Bot.shutdown();
                Database.closeCon();
                break;
            }
            else{
                System.out.println("No command found!");
            }
        }

    }
}
