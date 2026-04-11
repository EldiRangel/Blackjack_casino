module com.uru.blackjack {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.uru.blackjack to javafx.fxml; 
    exports com.uru.blackjack;
    
    exports client;
    exports model;
    exports db;
    exports server;
}