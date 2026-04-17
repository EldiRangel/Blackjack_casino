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
                        
                        guardarEnHistorial("PERDIO (Se paso)", total);
                    } else {
                        out.println("SCORE|" + total);
                    }
                } 
                else if (request.equals("STAND")) {
                    int playerTotal = playerHand.getTotal();
                    
                    // Turno del Dealer
                    Hand dealerHand = new Hand();
                    dealerHand.add(sharedDeck.draw());
                    dealerHand.add(sharedDeck.draw());
                    
                    // La casa pide hasta llegar a 17
                    while (dealerHand.getTotal() < 17) {
                        dealerHand.add(sharedDeck.draw());
                    }
                    
                    int dealerTotal = dealerHand.getTotal();
                    
                    out.println("OPPONENT|" + dealerTotal); 
                    
                    String resultadoFinal;
                    if (dealerTotal > 21) {
                        resultadoFinal = "¡GANASTE! Dealer voló con " + dealerTotal;
                    } else if (dealerTotal > playerTotal) {
                        resultadoFinal = "PERDISTE. Dealer tiene " + dealerTotal;
                    } else if (dealerTotal < playerTotal) {
                        resultadoFinal = "¡GANASTE! Dealer sacó " + dealerTotal;
                    } else {
                        resultadoFinal = "EMPATE. Ambos tienen " + playerTotal;
                    }

                    out.println("RESULT|" + resultadoFinal + "|" + playerTotal);
                    BlackjackServer.broadcast("CHAT|La casa terminó su turno con " + dealerTotal);
                }
            }
        } catch (IOException e) { 
            mesa.remove(this); 
            BlackjackServer.broadcast("CHAT|Un jugador abandonó la mesa.");
            System.out.println("Jugador desconectado. Quedan: " + mesa.size());
        }
    }
    private void guardarEnHistorial(String resultado, int puntaje) {
        try {
            PostgresAdapter dbReal = new PostgresAdapter();
            dbReal.connect("jdbc:postgresql://localhost:5432/blackjack_db", "postgres", "123456789");
            
            String sql = "INSERT INTO historial (resultado, puntaje) VALUES ('" + resultado + "', " + puntaje + ")";
            dbReal.query(sql, null);
            
            System.out.println("Partida guardada en DB: " + resultado + " | Puntaje: " + puntaje);
        } catch (Exception e) {
            System.out.println("Error guardando en DB: " + e.getMessage());
        }
    }
}