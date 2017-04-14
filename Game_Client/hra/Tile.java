/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hra;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Trieda Tile sluzi na vytvorenie objektu kocky. Kocka ma nejaku hodnotu a 
 * moze sa otacat, teda otvarat a zatvarat
 * @author Miro
 * @version 1.0
 */
class Tile extends StackPane{
    
    public final int id;
    private final char value;
    private final Hra hra;
    
    private Text text = new Text();
    
    /**
     * Kontruktore triedy Tile. 
     * @param id identifikator kocky
     * @param value hodnota kocky
     * @param hra hra, ku ktorej sa kocka priradi
     */
    public Tile(int id, char value, Hra hra){
        this.id = id;
        this.value = value;
        this.hra = hra;
        Rectangle border = new Rectangle(50, 50);
        border.setFill(null);
        border.setStroke(Color.BLACK);

        text.setText(String.valueOf(value));
        text.setFont(Font.font(30));
        text.setOpacity(0);

        setAlignment(Pos.CENTER);
        getChildren().addAll(border, text);

        setOnMouseClicked(e -> { 
            if(!hra.isMyTurn())
                return;
            if(isOpen())
                return;
            
            if(hra.getSelected1() == null){
                hra.select1(this);
                open(() -> {});            
            }else{
                Tile other = hra.getSelected1();
                if(this.getTileIDint() == other.getTileIDint())
                    return;
                open(() -> {
                    hra.select2(this);                    
                    if(!jeRovnaka(other)){
                        other.close();
                        this.close();                        
                    }else{
                        hra.updateMojeSkore();
                    }     
                    hra.setMyTrun(false);
                    hra.deselect1();
                    hra.deselect2();
                });
            }
        }
        );

        close();
    }
    
    /**
     * Porovnava hotnotu kocky s druhou
     * @param other druha kocka
     * @return rovnaka/ina
     */
    public boolean jeRovnaka(Tile other){
        return text.getText().equals(other.text.getText());
    } 
    
    /**
     * Porovnava identifikator kocky
     * @param other druha kocka
     * @return rovnaka/ina
     */
    public boolean jeRovnakeID(Tile other){
        return id == other.getTileIDint();
    }
    
    /**
     * Zistuje ci je kocka otvorena
     * @return otvorena/zatvorena
     */
    public boolean isOpen(){
        return text.getOpacity() == 1;
    }    
    
    /**
     * Otvara kocku
     * @param action prazdna akcia
     */
    public void open(Runnable action) {
        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), text);
        ft.setToValue(1);
        ft.setOnFinished(e -> action.run());
        ft.play();
    }
    
    /**
     * Zatvara kocku
     */
    public void close() {
        FadeTransition ft = new FadeTransition(Duration.seconds(0.2), text);
        ft.setToValue(0);
        ft.play();

    }
    
    /**
     * Vrati identifikator (int) kocky
     * @return int - identifikator kocky 
     */
    public int getTileIDint(){
        return id;
    }
    
    /**
     * Vrati identifikator (String) kocky
     * @return String - identifikator kocky
     */
    public String getTileID(){
        return Integer.toString(id);
    }
}
