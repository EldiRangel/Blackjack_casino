package server;

import db.DbComponent;
import db.PostgresAdapter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BlackjackServer {
    // Lista de jugadores conectados a la mesa
    private static List<BlackjackSession> mesaJugadores = new ArrayList<>();
    private static DbComponent<PostgresAdapter> db;

    public static void main(String[] args) {
        try {
            // Configuración de DB (la que ya tiene Luis)
            db = new DbComponent<>(PostgresAdapter.class, 
                "jdbc:postgresql://localhost:5432/blackjack_db", "postgres", "123456789", "queries.json");
            
            ServerSocket server = new ServerSocket(5000);
            System.out.println("Servidor Multijugador iniciado en puerto 5000...");

            while (true) {
                Socket socket = server.accept();
                BlackjackSession session = new BlackjackSession(socket, db, mesaJugadores);
                mesaJugadores.add(session); // Lo agregamos a la mesa
                new Thread(session).start();
                System.out.println("Nuevo jugador se ha unido a la mesa. Total: " + mesaJugadores.size());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Método para avisar a todos en la mesa (Requisito multijugador)
    public static void broadcast(String mensaje) {
        for (BlackjackSession s : mesaJugadores) {
            s.enviarMensaje(mensaje);
        }
    }
}