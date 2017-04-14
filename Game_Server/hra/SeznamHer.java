/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hra;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trieda SeznamHer obsahuje zoznam hier. Star sa hladanie, vytvaranie a
 * ukoncovanie hier
 * @author Miro
 * @version 1.0
 */
public class SeznamHer {
    private static final int POCET = 5; //Maximalny pocet aktivnych hier
    private static Hra[] hra = new Hra[POCET];
    
    public SeznamHer() {
       
    }
    /**
     * Funkcia prechadza zoznam her a hlada prazdnu hru. V pripade ze ziadna
     * prazdna hra neexistuje, tak vytvori novu a prida uzivatela
     * @param socket socket uzivatela
     * @param users zoznam hracov
     * @param user hrac
     */
    public synchronized void najdiHru(Socket socket, SeznamUser users, String user){
        boolean connected = false;
        
        Logger logger = LoggerFactory.getLogger("hra.SeznamHer");
        
        try{
                
        for(int i = 0; i < POCET; i++){
                    if(hra[i] != null){
                        if(hra[i].isFree()){
                            hra[i].addPlayer(user, socket);
                            logger.debug(user + " joined game#"+i);
                            connected = true;
                            break;
                        }
                    }
                }
                
                //Ak stale sa neprihlasil do hry najdem null hru a vytvorim
                if(!connected){
                    for(int i = 0; i < POCET; i++){
                        if(hra[i] == null){                            
                            hra[i] = new Hra(i,this, users);
                            hra[i].start();
                            logger.debug("Game#"+i+" created");
                            hra[i].addPlayer(user, socket);    
                            logger.debug(user + " joined game#"+i);
                            connected = true;
                            break;
                        }
                    }
                }
                
                //Ak je server plny tak uzivatela odhalsim a prerusim spojenie
                if(!connected){
                    logger.debug("Server is full");
                    users.logout(user);
                    new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true).println("serverfull");
                    socket.close();
                }
        }catch(IOException ex){
        }
        
    }
    
    /**
     * Funkcia vymaze zo zoznamu ukoncenu hru
     * @param id identifikator hry
     */
    public synchronized void odoberHru(int id){
        Logger logger = LoggerFactory.getLogger("hra.SeznamHer");
        hra[id]=null;
        logger.debug("Game#"+id+" destroyed");
    }   
    
}
