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
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Vlakno hra spravuje hru. Vytvori hru a spracovava prikazy pouzivatelov.
 * @author Miro
 * @version 1.0
 */
public class Hra extends Thread {

    private boolean free = true;
    private final int id;
    private SeznamHer seznam;
    private SeznamUser users;

    private final int POCET = 16; //parne cislo * 2 EX: 4, 8, 16, 32
    private String rozlozenie;
    private boolean koniec = false;

    private boolean p1connected = false;
    private boolean p2connected = false;
    private boolean disconnected = false;

    private String p1 = "";
    private String p2 = "";

    private Socket p1soc;
    private Socket p2soc;

    private PrintWriter p1output;
    private BufferedReader p1input;

    private PrintWriter p2output;
    private BufferedReader p2input;
    
    /**
     * Kontruktor hry.
     * @param id identifikator hry
     * @param seznam zoznam hier
     * @param users zoznam pouzivatelov
     */

    public Hra(int id, SeznamHer seznam, SeznamUser users) {
        this.id = id;
        this.seznam = seznam;
        this.users = users;
        this.rozlozenie = novaHra(POCET);

    }

    @Override
    public void run() {

        while (!disconnected) {
            try {
                this.sleep(500);
            } catch (InterruptedException ex) {

            }
        }

        try {
            if (p1connected) {
                p1output.println("quit");
                p1soc.close();
                users.logout(p1);
            }
            if (p2connected) {
                p2output.println("quit");
                p2soc.close();
                users.logout(p2);
            }
        } catch (IOException ex) {

        }

        seznam.odoberHru(id);

    }
    
    /**
     * Funkcia spracovava prikaz pouzivatela
     * @param prikaz prikaz na spracovanie
     */
    public synchronized void spracujPrikaz(String prikaz) {
        //System.err.println("Hra"+id+" Sprava od playera: "+ prikaz);
        if (!(p1connected && p2connected)) {
            return;
        }
        //prikazy od hraca1 
        if (prikaz.matches("Hrac1:.+")) {
            String prikazHrac1 = prikaz.substring(6).trim();
            //prikazy pre hru
            if (prikazHrac1.matches("Hra:.+")) {
                String prikazHrac1Hra = prikazHrac1.substring(4).trim();
                String selected1 = "";
                if (prikazHrac1Hra.matches("selected1:.+")) {
                    String idTile = prikazHrac1Hra.substring(10).trim();
                    selected1 = idTile;
                    p2output.println("Hra:selected1:" + idTile);
                }
                if (prikazHrac1Hra.matches("selected2:.+")) {
                    String idTile = prikazHrac1Hra.substring(10).trim();
                    p2output.println("Hra:selected2:" + idTile);
                    p2output.println("Hra:yourturn");
                }
                if (prikazHrac1Hra.matches("newgame")) {
                    rozlozenie = novaHra(POCET);
                    p1output.println("Hra:" + rozlozenie);
                    p2output.println("Hra:" + rozlozenie);
                }
            }

            //prikazy pre chat
            if (prikazHrac1.matches("Message:.+")) {
                p2output.println(prikazHrac1);
            }
        }
        
        if (prikaz.matches("Hrac2:.+")) {
            String prikazHrac2 = prikaz.substring(6).trim();
            //prikazy pre hru
            if (prikazHrac2.matches("Hra:.+")) {
                String prikazHrac2Hra = prikazHrac2.substring(4).trim();
                if (prikazHrac2Hra.matches("selected1:.+")) {
                    String idTile = prikazHrac2Hra.substring(10).trim();
                    p1output.println("Hra:selected1:" + idTile);
                }
                if (prikazHrac2Hra.matches("selected2:.+")) {
                    String idTile = prikazHrac2Hra.substring(10).trim();
                    p1output.println("Hra:selected2:" + idTile);
                    p1output.println("Hra:yourturn");
                }
                if (prikazHrac2Hra.matches("newgame")) {
                    rozlozenie = novaHra(POCET);
                    p1output.println("Hra:" + rozlozenie);
                    p2output.println("Hra:" + rozlozenie);
                }
            }

            //prikazy pre chat
            if (prikazHrac2.matches("Message:.+")) {
                p1output.println(prikazHrac2);
            }
        }

    }
    /**
     * Funkcia po ukonci spojenia jedneho z hracov, hraca odhlasi a nastavi 
     * hodnotu disconnected na true
     * @param player identifikator hraca, ktory sa odpojil
     */
    public synchronized void setDisconnected(String player) {
        if (player.matches("Hrac1")) {
            p1connected = false;
            users.logout(p1);
            try {
                p1soc.close();
            } catch (IOException ex) {
            }
        }
        if (player.matches("Hrac2")) {
            p2connected = false;
            users.logout(p2);
            try {
                p2soc.close();
            } catch (IOException ex) {
            }
        }
        disconnected = true;
    }
    
    /**
     * Vracia hodntou, ci je hra volna pre dalsieho hraca
     * @return 
     */
    public synchronized boolean isFree() {
        return free;
    }
    
    /**
     * Nastavuje, ci je hra plna alebo volna
     * @param free ture/false 
     */
    private synchronized void setFree(boolean free) {
        this.free = free;
    }
    
    /**
     * Pridava hraca do hry
     * @param player identifikator hraca
     * @param socket socket hraca
     * @throws IOException 
     */
    public void addPlayer(String player, Socket socket) throws IOException {
        if (p1.matches("")) {
            p1connected = true;
            this.p1 = player;
            this.p1soc = socket;
            this.p1output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            this.p1input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            Thread player1 = new Player(p1input, this, "Hrac1");
            player1.start();
            p1output.println("Hra:" + rozlozenie);
            p1output.println("Hra:Message:Čakám na súpera...");

        } else {
            p2connected = true;
            this.p2 = player;
            this.p2soc = socket;
            this.p2output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            this.p2input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            Thread player2 = new Player(p2input, this, "Hrac2");
            player2.start();
            p2output.println("Hra:" + rozlozenie);
            p1output.println("Hra:Message:Súper sa pripojil...");
            p1output.println("Hra:yourturn");
            setFree(false);
        }
    }
    
    /**
     * Vytvorenie parametrov hry
     * @param pocet pocet kociek 
     * @return 
     */
    private String novaHra(int pocet) {
        String rozlozenie = "inic:";
        char c = 'A';
        List<Character> tiles = new ArrayList<>();
        for (int i = 0; i < pocet / 2; i++) {
            tiles.add(c);
            tiles.add(c);
            c++;
        }
        Collections.shuffle(tiles);
        for (char ch : tiles) {
            rozlozenie += String.valueOf(ch);
        }
        return rozlozenie;
    }

}
