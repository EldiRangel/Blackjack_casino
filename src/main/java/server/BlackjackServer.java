package server;

import db.DbComponent;
import db.PostgresAdapter;
import java.net.*;

public class BlackjackServer {
    public static void main(String[] args) {
        DbComponent<PostgresAdapter> db = null;
        try {
           
            db = new DbComponent<>(PostgresAdapter.class, "jdbc:postgresql://localhost:5432/blackjack", "postgres", "password", "queries.json");
        } catch (Exception e) { System.err.println("Sin DB"); }

        try (ServerSocket server = new ServerSocket(5000)) {
            while (true) {
                new Thread(new BlackjackSession(server.accept(), db)).start();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}