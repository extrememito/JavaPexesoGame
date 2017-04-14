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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Vlakno hry obsluhuje chod hry. Pripoji sa na server a spracuvava prikazy od
 * server a od uzivatela
 *
 * @author Miro
 * @version 1.0
 */
public class Hra extends Thread {

    private final int PORT = 55555;
    private final String IP;

    private final Main main;
    PrintWriter output;

    private final String name;
    private final String pass;

    List<Tile> tiles = new ArrayList<>();

    private boolean myTurn;
    private Tile selected1;
    private Tile selected2;
    private int skore = 0;

    private Tile enemySelected1;
    private Tile enemySelected2;
    private int enemySkore = 0;

    /**
     * Vytvorenie vlakna hry
     *
     * @param main GUI hry
     * @param name meno hraca
     * @param pass heslo hraca
     * @param ip ip serveru
     */
    public Hra(Main main, String name, String pass, String ip) {
        this.name = name;
        this.pass = pass;
        this.main = main;
        this.IP = ip;
    }

    @Override
    public void run() {
        System.out.println("Connecting...");
        try (Socket socket = new Socket(IP, PORT)) {
            //Nadviazanie spojenia  
            System.out.println("Connected to the server");
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            main.setHra(this);

            output.println("User:" + name);
            //
            //
            //Citanie a spracovanie prikazov
            String in = input.readLine();
            while (in != null) {

                //Prikazy pre hru 
                if (in.matches("Hra:.+")) {
                    String prikaz = in.substring(4).trim();
                    if (prikaz.matches("inic:.+")) {
                        skore = 0;
                        enemySkore = 0;
                        tiles = new ArrayList<>();
                        String stringGame = prikaz.substring(5).trim();
                        int i = 0;
                        for (char ch : stringGame.toCharArray()) {
                            Tile tile = new Tile(i, ch, this);
                            tiles.add(new Tile(i, ch, this));
                            i += 1;
                        }
                        Platform.runLater(() -> {
                            main.zobrazSpravu("Nová hra");
                            main.disabeInput(false);
                            main.disableButton(false);
                            main.UpdateMojeSkore(Integer.toString(skore));
                            main.UpdateSuperSkore(Integer.toString(enemySkore));
                            main.zobrazDosku(tiles);
                        });
                    }

                    if (prikaz.matches("yourturn")) {
                        this.setMyTrun(true);
                    }
                    if (prikaz.matches("selected1:.+")) {
                        enemySelected1 = tiles.get(Integer.parseInt(prikaz.substring(10).trim()));
                        enemySelected1.open(() -> {
                        });

                    }
                    if (prikaz.matches("selected2:.+")) {
                        enemySelected2 = tiles.get(Integer.parseInt(prikaz.substring(10).trim()));
                        enemySelected2.open(() -> {
                            if (enemySelected1.jeRovnaka(enemySelected2)) {
                                enemySkore++;
                                Platform.runLater(() -> {
                                    main.UpdateSuperSkore(Integer.toString(enemySkore));
                                });
                            } else {
                                enemySelected1.close();
                                enemySelected2.close();
                            }
                        });

                    }
                    if (prikaz.matches("Message:.+")) {
                        String message = prikaz.substring(8).trim();
                        Platform.runLater(() -> {
                            main.zobrazSpravu(message);
                        });
                    }
                }
                //Prikazy pre chat
                if (in.matches("Message:.+")) {
                    String message = in.substring(8).trim();
                    Platform.runLater(() -> {
                        main.zobrazSpravu("Super: " + message);;
                    });

                }

                //Vseobecne prikazy
                if (in.matches("quit")) {
                    Platform.runLater(() -> {
                        main.zobrazSpravu("Súper sa odpojil");
                    });
                    break;
                }

                if (in.matches("serverfull")) {
                    Platform.runLater(() -> {
                        main.setInfo("Server je plný");
                    });
                    break;
                }

                if (in.matches("USER:OK")) {
                    output.println("Pass:" + pass);
                }
                
                /*
                if (in.matches("PASS:OK")) {
                }

                if (in.matches("SUCCESS")) {

                }
                */
                
                if (in.matches("FAILED")) {
                    Platform.runLater(() -> {
                        main.setInfo("Chyba pri prihlásení");
                    });
                    break;
                }
                in = input.readLine();
            }
            Platform.runLater(() -> {
                main.disabeInput(true);
                main.disableButton(true);
                main.zobrazSpravu("Ukončenie spojenia... Budete odhlásený");
            });

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex1) {
            }
            if (main.isGameScene()) {
                Platform.runLater(() -> {
                    main.clearInfo();
                    main.setSceneUvod();
                });
            }
            socket.close();
            System.out.println("Disconnected");
        } catch (IOException ex) {
            System.out.println("Disconnected");
            Platform.runLater(() -> {
                main.disableButton(true);
                main.disabeInput(true);
                main.zobrazSpravu("Ukončenie spojenia... Bude následovať odhlásenie");
            });
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex1) {
            }
            if (main.isGameScene()) {
                Platform.runLater(() -> {
                    main.clearInfo();
                    main.setSceneUvod();
                });
            }
        }
    }

    /**
     * Odosle spravu na server
     *
     * @param data sprava
     */
    public void sendData(String data) {
        output.println(data);
    }

    /**
     * Vrati 1. zobrazenu kocku
     *
     * @return 1.zobrazena kocka
     */
    public Tile getSelected1() {
        return selected1;
    }

    /**
     * Nastavi 1. zobraznu kocku
     *
     * @param selected1 1.zobrazena kocka
     */
    public void select1(Tile selected1) {
        output.println("Hra:selected1:" + selected1.getTileID());
        this.selected1 = selected1;
    }

    /**
     * Vymaze 1. zobrazenu kocku
     */
    public void deselect1() {
        this.selected1 = null;
    }

    /**
     * Vrati 2. zobrazenu kocku
     *
     * @return 2.zobrazena kocka
     */
    public Tile getSelected2() {
        return selected2;
    }

    /**
     * Nastavi 2. zobrazenu kocku
     *
     * @param selected2 2.zobrazena kocka
     */
    public void select2(Tile selected2) {
        output.println("Hra:selected2:" + selected2.getTileID());
        this.selected2 = selected2;
    }

    /**
     * Vymaze 2. zobrazen kocku
     */
    public void deselect2() {
        this.selected2 = null;
    }

    /**
     * Nastavuje boolean true/false podla toho ci je hrac na tahu
     *
     * @param turn hracov tah true/false
     */
    public synchronized void setMyTrun(boolean turn) {
        myTurn = turn;
    }

    /**
     * Zistuje hracov tah
     *
     * @return hracov tah true/false
     */
    public boolean isMyTurn() {
        return myTurn;
    }

    /**
     * Zvysi hracove skore o 1 a updatuje GUI
     */
    public void updateMojeSkore() {
        skore++;
        main.UpdateMojeSkore(Integer.toString(skore));
    }
}
