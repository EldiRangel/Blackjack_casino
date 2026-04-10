package server;

import model.*;
import db.DbComponent;
import db.PostgresAdapter;
import java.io.*;
import java.net.Socket;

public class BlackjackSession implements Runnable {
    private Socket socket;
    private DbComponent<PostgresAdapter> db;
    private PrintWriter out;
    private BufferedReader in;
    private Deck deck;
    private Hand playerHand;

    public BlackjackSession(Socket socket, DbComponent<PostgresAdapter> db) {
        this.socket = socket;
        this.db = db;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request;

            while ((request = in.readLine()) != null) {
                switch (request) {
                    case "START":
                        deck = new Deck();
                        playerHand = new Hand();
                        playerHand.add(deck.draw());
                        playerHand.add(deck.draw());
                        sendState();
                        break;
                    case "HIT":
                        playerHand.add(deck.draw());
                        if (playerHand.getTotal() > 21) {
                            out.println("RESULT|PERDISTE (Bust)");
                        } else {
                            sendState();
                        }
                        break;
                    case "STAND":
                        out.println("RESULT|Te plantaste con " + playerHand.getTotal());
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado.");
        }
    }

    private void sendState() {
        out.println("SCORE|" + playerHand.getTotal());
    }
}