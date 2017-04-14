/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hra;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trieda SeznamUser obsahuje zoznam uzivatelov, ktory maju pristup k hre
 * @author Miro
 * @version 1.0
 */
public class SeznamUser {
    
    private static List<User> seznamUser = new ArrayList<>();
    
    /**
     * Konsturktor triedy SeznamUser. Vytvori uzivatelov
     */
    public SeznamUser(){
        seznamUser.add(new User("adam", "heslo"));
        seznamUser.add(new User("ivan", "heslo"));
        seznamUser.add(new User("jan", "heslo"));
        seznamUser.add(new User("jozef", "heslo"));
        seznamUser.add(new User("miro", "heslo"));
        seznamUser.add(new User("peto", "heslo"));
        seznamUser.add(new User("martin", "heslo"));
        seznamUser.add(new User("riso", "heslo"));
        seznamUser.add(new User("rado", "heslo"));
        seznamUser.add(new User("marek", "heslo"));
        seznamUser.add(new User("kubo", "heslo"));
    }
    
    /**
     * Meto skontorulje meno a heslo pouzivatela a prihlasy ho
     * @param name meno pouizvatela
     * @param pass heslo pouzivatela
     * @return 
     */
    public synchronized boolean login(String name, String pass){
        for (int i = 0; i < seznamUser.size(); i++){
            User user = seznamUser.get(i);
            if(user.getNick().matches(name)&&user.getPass().matches(pass)&&!user.isLogedIn()){
                user.logIn(true);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Odhlasi pouzivatela
     * @param name meno pouzivatela
     */
    public synchronized void logout(String name){
        Logger logger = LoggerFactory.getLogger("hra.SeznamUser");
        for (int i = 0; i < seznamUser.size(); i++){
            User user = seznamUser.get(i);
            if(user.getNick().matches(name)&&user.isLogedIn()){
                user.logIn(false);
                logger.debug(user.getNick() + " loged out");               
            }
        }
    }
    
}
