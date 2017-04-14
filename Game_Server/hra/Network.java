/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vlákno obsluhy siete. Vytorí socket a pocuva. Po pripojeni klietna
 * vytovri vlakno spracovania
 * @author Miro
 * @version 1.0
 */
public class Network extends Thread {

    private final int PORT = 55555;

    public Network() {
    }

    @Override
    public void run() {
        Logger logger = LoggerFactory.getLogger("hra.Network");
        logger.debug("Creating pexeso server"); 
        SeznamUser users = new SeznamUser();
        SeznamHer seznamHer = new SeznamHer();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            boolean connected = false;
            while (true) {
                logger.debug("Server is ready...");
                Socket socket = serverSocket.accept();
                logger.debug("Client connected..("+ socket.getRemoteSocketAddress().toString() +")");

                Thread matchMaking = new Spracuj(socket, seznamHer, users);
                matchMaking.start();
            }

        } catch (IOException ex) {
            logger.debug("Server failed");
        }
    }

}
