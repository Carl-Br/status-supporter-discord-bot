package com.grow.Database;


public class GuildRole{
    public long guildId;
    public long roleId;
    public int days;

    public GuildRole(long guildId, long roleId, int days){
        this.guildId = guildId;
        this.roleId = roleId;
        this.days = days;
    }
}
