package com.grow.Database;

import java.util.Date;

public class Status {
    //guildId , supportStatus, timeAdded
    public String supporterStatus;
    public Date timeAdded;
    public int id;

    public Status(int id, String supporterStatus, Date timeAdded){
        this.id=id;
        this. supporterStatus = supporterStatus;
        this.timeAdded = timeAdded;
    }
}
