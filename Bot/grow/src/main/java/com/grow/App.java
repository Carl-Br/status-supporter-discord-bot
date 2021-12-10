package com.grow;


import com.grow.Database.Database;

public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        Database.connect();
        Database.clearServerTable();
        Database.closeCon();
    }
}
