/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hra;

import java.util.List;
import javafx.scene.layout.Pane;

/**
 * Trieda sluzi na vytvorenie GUI hracej dosky zo zoznamu kociek
 * @author Miro
 * @version 1.0
 */
public class HraciaDoska extends Pane{
    /**
     * Konsturktor triedy HraciaDoska
     * @param tiles zoznam kociek
     * @param pocet celkovy pocet kociek
     * @param riadok pocet riadkov
     */
    public HraciaDoska (List<Tile> tiles, int pocet, int riadok){
        for(int i = 0; i < pocet; i++){
            Tile tile = tiles.get(i);
            tile.setTranslateX(50 * (i % riadok ));
            tile.setTranslateY(50 * (i / riadok ));
            getChildren().add(tile);           
       }
    }
}
