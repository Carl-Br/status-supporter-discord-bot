package com.grow.Database;

import java.util.Date;

public class User {
    public long userId;
    public Date timeAdded;
    public User(long userId, Date timeAdded){
        this.userId = userId;
        this.timeAdded = timeAdded;
    }
}
