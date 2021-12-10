package com.grow;

import com.grow.Database.Database;

public final class App {

    public static void main(String[] args) {
        Database.connect();
        Database.clearServerTable();
        Database.closeCon();
    }
}
