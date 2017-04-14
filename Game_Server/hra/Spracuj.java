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
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Vlakno spracuj obsluzi klienta. Prihlasi ho a prida do hry
 * @author Miro
 * @version 1.0
 */
public class Spracuj extends Thread{
    
    Socket socket;
    SeznamHer seznamHer;
    SeznamUser users;
    
    /**
     * Konstruktor triedy Spracuj
     * @param socket - socket noveho klienta
     * @param seznamHer - zoznam hier
     * @param users - zoznam pouzivatelov
     */
    public Spracuj(Socket socket, SeznamHer seznamHer, SeznamUser users){
        this.socket = socket;
        this.seznamHer = seznamHer;
        this.users = users;
    }
    
    @Override
    public void run(){
        Logger logger = LoggerFactory.getLogger("hra.Spracuj");
        
        String name = "";
        String pass = "";
        try {
            PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String in = input.readLine();
            if(in.matches("User:.+")){
                output.println("USER:OK");
                name = in.substring(5).trim();
            }else{
                logger.debug("Client has failed to log in"+ socket.getRemoteSocketAddress().toString());
                output.println("FAILED");
                socket.close();
                return;
            }
            in = input.readLine();
            
            if(in.matches("Pass:.+")){
                pass = in.substring(5).trim();
                output.println("PASS:");
            }else{
                logger.debug("User: "+ name + " has failed to log in"+ socket.getRemoteSocketAddress().toString());
                output.println("FAILED");
                socket.close();
                return;
            }
            
            if(users.login(name, pass)){
                logger.debug("User: "+ name + " has logged in"+ socket.getRemoteSocketAddress().toString());
                output.println("SUCCESS");
            }else{
                logger.debug("User: "+ name + " has failed to log in"+ socket.getRemoteSocketAddress().toString());
                output.println("FAILED");
                socket.close();
                return;
            }
            
        } catch (IOException ex) {
            users.logout(name);
        }
        
        seznamHer.najdiHru(socket, users, name);
    }
}
