package client;

import com.uru.blackjack.PrimaryController;
import java.io.*;
import java.net.Socket;

public class BlackjackClient {
    private PrimaryController controller;
    private PrintWriter out;

    public BlackjackClient(PrimaryController c) { this.controller = c; }

    public void connect() {
        new Thread(() -> {
            try {
                Socket s = new Socket("localhost", 5000);
                out = new PrintWriter(s.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String msg;
                while ((msg = in.readLine()) != null) {
                    String[] p = msg.split("\\|");
                    if (p[0].equals("SCORE")) controller.updateUI(p[1]);
                    if (p[0].equals("RESULT")) controller.showResult(p[1]);
                }
            } catch (Exception e) { }
        }).start();
    }

    public void sendCommand(String cmd) { if (out != null) out.println(cmd); }
}