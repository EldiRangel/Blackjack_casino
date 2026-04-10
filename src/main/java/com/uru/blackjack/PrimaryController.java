package com.uru.blackjack;

import client.BlackjackClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class PrimaryController {
    @FXML private HBox hbPlayerCards;
    @FXML private Label lblPlayerScore, lblStatus;
    @FXML private Button btnHit, btnStand;
    
    private BlackjackClient client;

    @FXML
    public void initialize() {
        client = new BlackjackClient(this);
        client.connect();
    }

    @FXML private void startGame() {
        client.sendCommand("START");
        btnHit.setDisable(false);
        btnStand.setDisable(false);
    }

    @FXML private void handleHit() { client.sendCommand("HIT"); }
    @FXML private void handleStand() { client.sendCommand("STAND"); }

    public void updateUI(String score) {
        Platform.runLater(() -> lblPlayerScore.setText("Puntaje: " + score));
    }

    public void showResult(String msg) {
        Platform.runLater(() -> {
            lblStatus.setText(msg);
            btnHit.setDisable(true);
            btnStand.setDisable(true);
        });
    }
}