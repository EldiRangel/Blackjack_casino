package client; 

import com.uru.blackjack.PrimaryController;
import javafx.application.Platform;
import java.io.*;
import java.net.Socket;

public class BlackjackClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private PrimaryController controller;

    public BlackjackClient(PrimaryController controller) {
        this.controller = controller;
    }

    public void connect() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 5000);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    final String response = line;

                    Platform.runLater(() -> {
                        if (response.startsWith("SCORE|")) {
                            controller.updateUI(response.split("\\|")[1]);
                        } else if (response.startsWith("RESULT|")) {
                            controller.showResult(response.split("\\|")[1]);
                        } else if (response.startsWith("OPPONENT|")) {
                            controller.updateOpponent(response.split("\\|")[1]);
                        } else {
                            controller.procesarRespuesta(response);
                        }
                    });
                }
            } catch (IOException e) {
                Platform.runLater(() -> controller.showResult("Error: Sin conexión"));
            }
        }).start();
    }

    public void sendCommand(String cmd) {
        if (out != null) out.println(cmd);
    }
}