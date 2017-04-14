/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hra;

import ch.qos.logback.classic.LoggerContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Server GUI
 * Vytvorenie okna serveru.
 * @author Miro
 * @version 1.0
 */
public class Main extends Application {

    private TextArea textArea;

    @Override
    public void start(Stage primaryStage) {
        
        Logger logger = LoggerFactory.getLogger("hra.Main");
        logger.debug("Server is starting"); 
        
        Thread network = new Network();
        network.setDaemon(true);
        network.start();

        
        Label ipData = new Label();
        try {
            ipData.setText("IP Adresa: "+InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            
        }
        ipData.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        textArea = new TextArea();
        textArea.setDisable(true);
        
        
        VBox ipVbox = new VBox(2);
        ipVbox.getChildren().addAll(ipData, textArea);
        ipVbox.setPadding(new Insets(5));
        

        BorderPane root = new BorderPane();
        root.setTop(ipVbox);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }    
}
