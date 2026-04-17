package com.uru.blackjack;

import client.BlackjackClient; 
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PrimaryController {

    @FXML private Label labelPuntaje; // Muestra "Cartas: X"
    @FXML private Label labelEstado;
    @FXML private Label labelOponente;
    @FXML private Button btnPedir;
    @FXML private Button btnPlantarse;
    @FXML private Button btnNuevaPartida;

    private BlackjackClient client; 

    @FXML
    public void initialize() {
        client = new BlackjackClient(this); 
        client.connect();
        
        btnPedir.setDisable(true);
        btnPlantarse.setDisable(true);
        labelEstado.setText("");
    }

    // Actualiza tus cartas
    public void updateUI(String cardCount) {
        Platform.runLater(() -> {
            labelPuntaje.setText("Cartas: " + cardCount);
            labelEstado.setText("TU TURNO");
        });
    }

    // Actualiza cartas del oponente
    public void updateOpponent(String count) {
        Platform.runLater(() -> {
            labelOponente.setText("Oponente: " + count + " cartas");
        });
    }

    public void showResult(String result) {
        Platform.runLater(() -> {
            labelEstado.setText(result.toUpperCase());
            btnPedir.setDisable(true);
            btnPlantarse.setDisable(true);
            btnNuevaPartida.setDisable(false);
        });
    }

    public void procesarRespuesta(String response) {
        if (response.startsWith("OPPONENT|")) {
            updateOpponent(response.split("\\|")[1]);
        }
        // Agrega aquí otros comandos si el servidor los manda
    }

    @FXML
    private void iniciarPartida() {
        client.sendCommand("START");
        labelPuntaje.setText("Cartas: 0");
        labelEstado.setText("ESPERANDO...");
        btnPedir.setDisable(false);
        btnPlantarse.setDisable(false);
        btnNuevaPartida.setDisable(true);
    }

    @FXML
    private void pedirCarta() {
        client.sendCommand("HIT");
    }

    @FXML
    private void plantarse() {
        client.sendCommand("STAND");
    }
}