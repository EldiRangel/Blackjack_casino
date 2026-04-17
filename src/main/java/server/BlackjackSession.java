package server;

import model.*;
import db.DbComponent;
import db.PostgresAdapter;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class BlackjackSession implements Runnable {
    private Socket socket;
    private DbComponent<PostgresAdapter> db;
    private PrintWriter out;
    private BufferedReader in;
    private Hand playerHand;
    
    // AQUÍ ESTÁ LA MAGIA: La baraja estática compartida por toda la mesa
    private static Deck sharedDeck = new Deck(); 
    
    private List<BlackjackSession> mesa;

    public BlackjackSession(Socket socket, DbComponent<PostgresAdapter> db, List<BlackjackSession> mesa) {
        this.socket = socket;
        this.db = db;
        this.mesa = mesa;
    }

    // Método para recibir mensajes del servidor general (Multijugador)
    public void enviarMensaje(String msg) { 
        if (out != null) {
            out.println(msg); 
        }
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request;

            while ((request = in.readLine()) != null) {
                if (request.equals("START")) {
                    // LÓGICA: Reiniciamos la mano cada vez que inicia partida
                    playerHand = new Hand(); 
                    
                    // Si quedan pocas cartas en la mesa, sacamos una baraja nueva
                    if (sharedDeck.getSize() < 10) {
                        sharedDeck = new Deck(); 
                    }
                    
                    playerHand.add(sharedDeck.draw());
                    playerHand.add(sharedDeck.draw());
                    
                    out.println("SCORE|" + playerHand.getTotal());
                    
                    // MULTIJUGADOR: Avisar a los demás que alguien empezó
                    BlackjackServer.broadcast("CHAT|Un jugador ha iniciado una nueva partida.");
                } 
                else if (request.equals("HIT")) {
                    playerHand.add(sharedDeck.draw());
                    int total = playerHand.getTotal();
                    
                    if (total > 21) {
                        // LÓGICA: Límite de 21
                        out.println("RESULT|PERDISTE (Te pasaste con " + total + ")|" + total);
                        BlackjackServer.broadcast("CHAT|Un jugador se ha pasado de 21 y perdió.");
                    } else {
                        out.println("SCORE|" + total);
                    }
                } 
                else if (request.equals("STAND")) {
                    out.println("RESULT|Te plantaste con " + playerHand.getTotal() + "|" + playerHand.getTotal());
                    BlackjackServer.broadcast("CHAT|Un jugador se plantó con " + playerHand.getTotal() + ".");
                }
            }
        } catch (IOException e) { 
            // MULTIJUGADOR: Si alguien cierra la ventana, lo sacamos de la mesa
            mesa.remove(this); 
            BlackjackServer.broadcast("CHAT|Un jugador abandonó la mesa.");
            System.out.println("Jugador desconectado. Quedan: " + mesa.size());
        }
    }
}