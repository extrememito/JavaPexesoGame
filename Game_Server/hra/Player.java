/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hra;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Vlakno pocuva vstup hraca a vracia prikazy na spracovanie
 * @author Miro
 * @version 1.0
 */
public class Player extends Thread{
    
        private final BufferedReader in;
        private final String ident;
        private Hra hra;
        
    /**
     * Konstruktor triedy Player. 
     * @param in vstup hraca
     * @param hra hra, v ktorej je hrac
     * @param ident identifikator hraca
     */
    public Player(BufferedReader in, Hra hra, String ident){
        this.in = in;
        this.hra = hra;
        this.ident = ident;
    }

    @Override
    public void run() {
        try{
        while(true){
            String message = in.readLine();
            hra.spracujPrikaz(ident+":"+message);            
        }
        }catch(IOException e){
            hra.setDisconnected(ident);            
        }
    
    }
    
    
    
}
