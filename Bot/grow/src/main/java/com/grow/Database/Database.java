package com.grow.Database;

import net.dv8tion.jda.api.entities.Guild;

import java.lang.constant.Constable;
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
          System.out.println("connected to database");

          statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (userId INTEGER PRIMARY KEY,timeAdded TEXT);");
          statement.executeUpdate("CREATE TABLE IF NOT EXISTS roles (roleId INTEGER PRIMARY KEY , days INTEGER);");
          statement.executeUpdate("CREATE TABLE IF NOT EXISTS supportStatus (id INTEGER PRIMARY KEY AUTOINCREMENT, supportStatus TEXT, timeAdded TEXT);");
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
          System.out.println("\nDatabase connection closed");
        }
      }
      catch(SQLException e)
      {
        // connection close failed.
        System.err.println(e.getMessage());
      }
    }

    //für die Methode addSupportStatus() , clearServerTable() und addUser()
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

    //USER

    public static List<User> getAllUsers() throws SQLException, ParseException {
        List<User> userList = new ArrayList<User>();
        ResultSet rs;
        //get allUsersFromUsers
        rs = statement.executeQuery("select * from users");

        while(rs.next()){
            userList.add(new User(rs.getLong("userId"),simpleDateFormat.parse(rs.getString("timeAdded"))));
            System.out.println(rs.getLong("userId")+"    Database.java line 67, getAllUsers()");
        }
        return userList;
    }
    public static void addOrUpdateUser(long userId) throws SQLException{
        String currentDate = simpleDateFormat.format(new Date());
      try {
        statement.executeUpdate("INSERT OR REPLACE INTO users (userId,timeAdded) VALUES(%s,'%s');".formatted(userId,currentDate));
      } catch (SQLException e) {
        e.printStackTrace();
        throw e;
      }
    }
    public static void removeUser(long userId)throws SQLException{
      try {
        statement.executeUpdate("DELETE FROM users WHERE userId = %s".formatted(userId));
      } catch (SQLException e) {
        e.printStackTrace();
        throw e;
      }
    }
    public static boolean userIsInDB(long userId) throws SQLException {
        ResultSet rs=null;
        try {
            rs = statement.executeQuery("select * from users WHERE userId = %s ".formatted(userId));

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    public static int getStatusSupporterCount() throws SQLException {
        ResultSet rs;
        try {
            rs = statement.executeQuery("select * from users");
            int count = 0;
            while(rs.next())
            {
                count++;
            }
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }
    /** Return null if there is no user in the database with such an userId*/

    //STATUS

    public static void addSupportStatus(String supportStatus)throws SQLException{
      String currentDate = simpleDateFormat.format(new Date());
      try {
        statement.executeUpdate("INSERT  INTO supportStatus (supportStatus,timeAdded) VALUES('%s','%s');".formatted(supportStatus,currentDate));
      } catch (SQLException e) {
        e.printStackTrace();
        throw e;
      }
    }

    /**Deletes every Dataset in the table, which is older than 48 hours
     * except for the newest one ( which is older than 48 hours)*/
    public static void clearServerTable(){

      ResultSet rs;
      try {
        rs = statement.executeQuery("select * from supportStatus");

        long twoDaysMs = 1000L*60*60*24*2;
        List<Long> clearedGuilds = new ArrayList<Long>();
        while(rs.next())
        {
          long currentMs = new Date().getTime();//Ms since 1 jan 1970

            // Delete all dataset, which should get deleted, with the current guildId

          long guildId = rs.getInt("guildId");
          //if the guildId hasn't been added to the List of cleared ids:
          if(!clearedGuilds.contains(guildId)){
            //add this GuildId to the List of cleared ids
            clearedGuilds.add(guildId);

            //get a list with every status dataset from this server
            List<Status> statusList = getStatusList();

            //remove every dateset from the last 48 hours
            for (int i = 0; i<statusList.size();i++) {
              if(statusList.get(i).timeAdded.getTime()>(currentMs-twoDaysMs)){
                System.out.println("removed "+statusList.get(i).timeAdded+ "from the list of datasets to remove because it's from the last 48 hours");
                statusList.remove(i);
                i--;//because we removed one element from the list;
              }
            }


            //There are no outdated datasets
            if(statusList.size()==0){
              break;
            }

            //find the latest dataset from the current list
            Status latesttatus = null;
            for(Status s : statusList){
              if(latesttatus==null) {
                  latesttatus = s;
              } else if (s.timeAdded.getTime()>latesttatus.timeAdded.getTime()){
                  System.out.println(s.timeAdded +" war nach "+latesttatus.timeAdded);
                  latesttatus = s;
              }else{System.out.println(s.timeAdded +" war vor "+latesttatus.timeAdded);}
            }

            System.out.print("latest Status: "+latesttatus.timeAdded);

            //remove the latest dataset from the list
            statusList.remove(latesttatus);

              //There are no outdated datasets
            if(statusList.size()==0){
              break;
            }


            //remove every remaining status in the list from the database
            for(Status s : statusList){
              try {
                statement.executeUpdate("DELETE FROM supportStatus WHERE id = %s ;".formatted(s.id));
              } catch (SQLException e) {
                e.printStackTrace();
              }
            }


          }

        }
      } catch (SQLException | ParseException e) {
        e.printStackTrace();
      }

    }

    public static List<Status> getStatusList()throws SQLException , ParseException{
      List<Status> statusList = new ArrayList<Status>();

      ResultSet rs;
      try {
        rs = statement.executeQuery("select * from supportStatus");

        while(rs.next()){
          int id = rs.getInt("id");
          String status = rs.getString("supportStatus");
          Date timeAdded = simpleDateFormat.parse(rs.getString("timeAdded"));
          statusList.add(new Status(id, status, timeAdded));
        }
      } catch (SQLException | ParseException e) {
        throw e;
      }

      return statusList;
    }

    public static Status getLatestStatus() throws SQLException , ParseException  {
        try{
            List<Status> statusList = getStatusList();
            if(statusList.isEmpty()) return null;
            return statusList.get(statusList.size()-1);
        }catch (SQLException | ParseException e){
            throw e;
        }
    }

    //ROLES

    public static void addRole( long roleId, int days) throws SQLException {
      //checks if the role has already been added
      List<GuildRole> guildRoles = getGuildRoles();
      for(GuildRole g : guildRoles){
        if(g.roleId == roleId){
          break;
        }
      }

      try{
        statement.executeUpdate("INSERT INTO roles (roleId, days) VALUES (%s,%s)".formatted(roleId,days));
      }catch(SQLException e) {
        e.printStackTrace();
          throw e;
      }

    }

    public static List<GuildRole> getGuildRoles() throws SQLException {
      List<GuildRole> list= new ArrayList<GuildRole>();
      ResultSet rs;
      try {
        rs = statement.executeQuery("select * from roles");

        while(rs.next())
        {
          list.add(new GuildRole(rs.getLong("roleId"), rs.getInt("days")));
        }
      } catch (SQLException e) {
        e.printStackTrace();
          throw e;
      }

      return list;

    }

    public static void updateDays(long roleId, int days) throws SQLException {
        if(roleIsInDatabase(roleId)){
            try {
                statement.executeUpdate("UPDATE roles SET days = %s WHERE roleId = %s;".formatted(days,roleId));
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public static boolean roleIsInDatabase(long roleId) throws SQLException {
        ResultSet rs=null;
        try {
            rs = statement.executeQuery("select * from roles WHERE roleId = %s".formatted(roleId));

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void deleteRole(long roleId)throws SQLException{
      try {
        statement.executeUpdate("DELETE FROM roles WHERE roleId = %s;".formatted(roleId));
      } catch (SQLException e) {
        e.printStackTrace();
        throw e;
      }
    }

}
