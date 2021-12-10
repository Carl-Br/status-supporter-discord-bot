package com.grow.Database;

import java.util.Date;

public class Status {
    //guildId , supportStatus, timeAdded
    public long guildId;
    public String supporterStatus;
    public Date timeAdded;
    public int id;

    public Status(int id,long guildId, String supporterStatus, Date timeAdded){
        this.id=id;
        this.guildId = guildId;
        this. supporterStatus = supporterStatus;
        this.timeAdded = timeAdded;
    }
}
