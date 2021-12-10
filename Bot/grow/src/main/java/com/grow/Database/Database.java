package com.grow.Database;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database {

  public static Connection connection = null;
  public static Statement statement = null;
    public static void connect()
    {
        try
        {

          // create a database connection
          connection = DriverManager.getConnection("jdbc:sqlite:growBotDatabase.db");
          statement = connection.createStatement();

          statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (userId INTEGER PRIMARY KEY, guildId INTEGER);");
          statement.executeUpdate("CREATE TABLE IF NOT EXISTS roles (id INTEGER PRIMARY KEY AUTOINCREMENT, guildId INTEGER, roleId INTEGER, days INTEGER);");
          statement.executeUpdate("CREATE TABLE IF NOT EXISTS supportStatus (id INTEGER PRIMARY KEY AUTOINCREMENT, guildId  INTEGER, supportStatus TEXT, timeAdded TEXT);");
        }
        catch(SQLException e)
        {
          // if the error message is "out of memory",
          // it probably means no database file is found
          System.err.println(e.getMessage());
        }
    }
    public static void closeCon(){
      try
      {
        if(connection != null){
          connection.close();
          System.out.println("Database connection closed");
        }
      }
      catch(SQLException e)
      {
        // connection close failed.
        System.err.println(e.getMessage());
      }
    }

    public static void addUser(long userId, long guildId){
      try {
        statement.executeUpdate("INSERT OR REPLACE INTO users (userId,guildId) VALUES(%s,%s);".formatted(userId,guildId));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    public static void removeUser(long userId){
      try {
        statement.executeUpdate("DELETE FROM users WHERE userId = %s".formatted(userId));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    
    //für die Methode addSupportStatus() und clearServerTable()
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

    public static void addSupportStatus(long guildId, String supportStatus){
      String currentDate = simpleDateFormat.format(new Date());
      try {
        statement.executeUpdate("INSERT  INTO supportStatus (guildId,supportStatus,timeAdded) VALUES(%s,'%s','%s');".formatted(guildId,supportStatus,currentDate));
      } catch (SQLException e) {
        e.printStackTrace();
      }    
    }

    
    /**Löscht jeden Datensatz  in der Server Tabelle, der älter als 48 Stunden, bis auf den neusten, der älter als 48 Stunden ist.*/
    public static void clearServerTable(){

      ResultSet rs;
      try {
        rs = statement.executeQuery("select * from supportStatus");

        long twoDaysMs =1000l*60*60*24*2; 
        List<Long> clearedGuilds = new ArrayList<Long>();
        while(rs.next())
        {
          long currentMs = new Date().getTime();//Ms since 1 jan 1970 

          //wenn der datensatz  älter als 48 Stunden ist, dann lösche alle datensätze mit dieser guild Id, bis auf den neusten, der äler als 48 Stunden ist

          long guildId = rs.getInt("guildId");
          //(wenn die guildId noch nicht in der List, der geclearten ids ist)
          if(!clearedGuilds.contains(guildId)){
            //füge die guildid hinzu
            clearedGuilds.add(guildId);
            
            //hol dir eine Liste mit allen Statussen von diesem Server
            List<Status> statusList = getStatusList(guildId);

            //entferne aus dieser Liste alle datensätze, die nicht älter als 48 Stunden sind
            for (int i = 0; i<statusList.size();i++) {
              if(statusList.get(i).timeAdded.getTime()>(currentMs-twoDaysMs)){
                statusList.remove(i);
              }
            }


            //es gibt keine veralteten Datensätze
            if(statusList.size()==0){
              break;
            }

            //finde den neusten datensatz aus der jetzigen Liste
            Status neusterStatus = null;
            for(Status s : statusList){
              if(neuerStatus==null){
                neusterStatus = s;
              }
              else(s.timeAdded.getTime()>neusterStatus.timeAdded.getTime()){
                neusterStatus = s;
              }
            }

            System.out.print("neuster Status: "+neusterStatus.timeAdded);

            //entferne den jetzt neuesten Datensatz aus der Liste
            statusList.remove(neusterStatus);

            //es gibt keine veralteten Datensätze
            if(statusList.size()==0){
              break;
            }

            
            //entferne alle Datensätze in der Datenbank, die noch in der Liste sind
            for(Status s : statusList){
              try {
                statement.executeUpdate("DELETE FROM supportStatus WHERE id = %s ;".formatted(s.id));
              } catch (SQLException e) {
                e.printStackTrace();
              }
            }

            
          }
          
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }

    }

    public static List<Status> getStatusList(long guildId){
      List<Status> statusList = new ArrayList<Status>();

      ResultSet rs;
      try {
        rs = statement.executeQuery("select * from supportStatus where guildId = %s;".formatted(guildId));

        while(rs.next()){
          int id = rs.getInt("id");
          String status = rs.getString("supportStatus");
          Date timeAdded = simpleDateFormat.parse(rs.getString("timeAdded"));
          statusList.add(new Status(id,guildId, status, timeAdded));
        }
        
      } catch (SQLException | ParseException e) {
        //TODO: handle exception
      }

      return statusList;
    }

    public static void addRole(long guildId, long roleId, int days){
      //checks if the role has already been added
      List<GuildRole> guildRoles = getGuildRoles(guildId);
      for(GuildRole g : guildRoles){
        if(g.roleId == roleId){
          break;
        }
      }


      try{
        statement.executeUpdate("INSERT INTO roles (guildId, roleId, days) VALUES (%s,%s,%s)".formatted(guildId,roleId,days));
      }catch(SQLException e) {
        e.printStackTrace();
      }
        
    }
    public static void addRole(long guildId, long roleId){

      //checks if the role has already been added
      List<GuildRole> guildRoles = getGuildRoles(guildId);
      for(GuildRole g : guildRoles){
        if(g.roleId == roleId){
          break;
        }
      }


      try{
        statement.executeUpdate("INSERT INTO roles (guildId, roleId) VALUES (%s,%s)".formatted(guildId,roleId));
      }catch(SQLException e) {
        e.printStackTrace();
      }
        
    }

    public static List<GuildRole> getGuildRoles(long guildId){
      List<GuildRole> list= new ArrayList<GuildRole>();
      ResultSet rs;
      try {
        rs = statement.executeQuery("select * from roles WHERE guildId = %s".formatted(guildId));

        while(rs.next())
        {
          list.add(new GuildRole(guildId, rs.getInt("roleId"), rs.getInt("days")));
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }

      return list;

    }

    public static void deleteRole(long guildId, long roleId){
      try {
        statement.executeUpdate("DELETE FROM roles WHERE guildId = %s AND roleId = %s;".formatted(guildId,roleId));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
}
