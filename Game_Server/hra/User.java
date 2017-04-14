/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hra;

/**
 * Trieda pre vytovernie objeku pouzivatela. Uklada meno, heslo a cije 
 * pouzivatel prihlaseny
 * @author Miro
 * @version 1.0
 */
public class User {
    
        private final String nick;
        private final String pass;
        private boolean logedin;
    
    /**
     * Konstruktor triedy User
     * @param nick meno pouzivatela
     * @param pass heslo pouzivatela
     */   
    public User(String nick, String pass){
        this.nick = nick;
        this.pass = pass;
        logedin =false;
    }

    public String getNick() {
        return nick;
    }

    public String getPass() {
        return pass;
    }
    
    /**
     * Nastavuje hodnotu prihlasenia
     * @param value true/false
     */
    public void logIn(boolean value){
        logedin = value;
    }
    /**
     * Zistuje, ci je uzivatel prihlaseny
     * @return 
     */
    public boolean isLogedIn(){
        return logedin;
    }
    
}
