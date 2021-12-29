package com.grow.Database;

import java.util.Date;

public class User {
    public long userId;
    public long guildId;
    public Date timeAdded;
    public User(long userId, long guildId, Date timeAdded){
        this.userId = userId;
        this.guildId = guildId;
        this.timeAdded = timeAdded;
    }
}
