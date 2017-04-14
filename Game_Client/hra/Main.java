/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hra;

import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * GUI hry pexeso. Vytvara scenu pre prihlasenie a scenu hry
 * @author Miro
 * @version 1.0
 */
public class Main extends Application {
    
    private Hra hra;
    
    private final int POCET = 16; //Parne cislo * 2 EX: 4, 8, 16, 32 !musi byt rovnake so serverom
    private final int RIADOK = 4; // Pocet kociek na riadok
    
    private String name;
    private String pass;
    private String ip;
    
    private Stage primaStage;
    private Scene scene;
    private Scene uvod;
    
    private BorderPane main;
    private TextArea chat;
    private TextField input;
    private Label mojeData;
    private Label superData;
    private Button btnNovaHra;
    
    private TextField nameInput;
    private PasswordField passInput;
    private TextField ipInput;
    private Label info;
    
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Zaciatok");
        this.primaStage = primaryStage;     
        main = new BorderPane();
        
        ///////////////////////////////////
        //Chat/////////////////////////////
        //////////////////////////////////
        chat = new TextArea();
        chat.setEditable(false);
        chat.setPrefHeight(200); 
        input = new TextField();
        input.setDisable(true);
        input.setPromptText("Správa pre súpera.. Potvrdte klávesou ENTER");
        //INPUT ACTION///////////////////////
        input.setOnAction(e -> {
            String message = input.getText();
            input.clear();
            chat.appendText("Ja: "+message+"\n");
            this.hra.sendData("Message:"+message);
        });   
        //FINAL////////////////////////////////        
        VBox vBoxChat = new VBox(5);
        vBoxChat.getChildren().addAll(chat,input);        
        main.setBottom(vBoxChat);
        ///////////////////////////////
        //Skore a control/////////////
        //////////////////////////////
        //Skore Label///////////////////////////////////////////
        Label skore = new Label("Skóre");
        skore.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        skore.setPadding(new Insets(0));
        //Moje skore////////////////////////////////////////////        
        Label moje = new Label("Ja: ");
        moje.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        moje.setPadding(new Insets(0));
        
        mojeData = new Label("0");
        mojeData.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        mojeData.setPadding(new Insets(0));
        
        HBox mojeSkore = new HBox();
        mojeSkore.setPadding(new Insets(0, 0, 0, 0));
        mojeSkore.getChildren().addAll(moje, mojeData);
        //Super skore///////////////////////////////////////////
        Label superlabel = new Label("Súper: ");
        superlabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        superlabel.setPadding(new Insets(0));
        
        superData = new Label("0");
        superData.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        superData.setPadding(new Insets(0));
        
        HBox superSkore = new HBox();
        superSkore.setPadding(new Insets(0, 0, 0, 0));
        superSkore.getChildren().addAll(superlabel, superData);
        //Nova Hra Button///////////////////////////////////////
        btnNovaHra = new Button("Nová hra");     
        btnNovaHra.setDisable(true);
        btnNovaHra.setOnAction(e ->{
            this.hra.sendData("Hra:newgame");
        } ); 
        btnNovaHra.setMinWidth(40);
        //Final//////////////////////////////////////////////        
        VBox skoreControl = new VBox(10);
        skoreControl.setPadding(new Insets(10, 10, 5, 5));
        skoreControl.getChildren().addAll(skore,mojeSkore,superSkore,btnNovaHra);
        main.setRight(skoreControl);
        ////////////////////////////////////////////////////////
        //END SKORE A CONTORLS/////////////////////////////////
        ////////////////////////////////////////////////////////         
        scene = new Scene(main, 300, 450);
        input.requestFocus();
        
        ///////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////
        //UVODNA OBRAZOVKA/////////////////////////////////////////
        ///////////////////////////////////////////////////////////
        Label nameLabel = new Label("Meno:");
        
        nameInput = new TextField();
        nameInput.setPromptText("Vaše meno");
        nameInput.setMaxWidth(200);
        nameInput.setOnAction(e ->{
            connect();
        } ); 
        /////////////////////////////////
        Label passLabel = new Label("Heslo:");
        
        passInput = new PasswordField();
        passInput.setPromptText("Vaše heslo");
        passInput.setMaxWidth(200);
        passInput.setOnAction(e ->{
            connect();
        } ); 
        //////////////////////////////////
        Label ipLabel = new Label("IP:");
        
        ipInput = new TextField();
        ipInput.setText("localhost");
        ipInput.setPromptText("Príklad: localhost, 192.168.0.2");
        ipInput.setMaxWidth(200);
        ipInput.setOnAction(e ->{
            connect();
        } ); 
        /////////////////////////////////
        Button btnPripojit = new Button("Pripojiť");
        btnPripojit.setMaxWidth(200);
        btnPripojit.setOnAction(e ->{
            connect();
        } ); //
        ///////////////////////////////
        info = new Label();
        ////////////////////////////////////
        
        VBox vBoxUvod = new VBox(2);
        vBoxUvod.getChildren().addAll(nameLabel,nameInput,passLabel,passInput,ipLabel,ipInput,btnPripojit,info);
        vBoxUvod.setAlignment(Pos.CENTER_LEFT);
        
        HBox root = new HBox();
        
        root.getChildren().add(vBoxUvod);
        root.setAlignment(Pos.CENTER);
        
        uvod = new Scene(root,300,450);
        
        primaryStage.setTitle("Client");
        primaryStage.setScene(uvod);
        primaryStage.setMinHeight(480);
        primaryStage.setMinWidth(310);
        primaryStage.show();
        
        /*
          */       
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Zoberie vstupy z formulary a vytvory vlakno hry
     */
    private void connect(){
        chat.clear();
        name = nameInput.getText().trim().toLowerCase();
        pass = passInput.getText().trim();
        ip = ipInput.getText().trim();
                
        Thread thread = new Hra(this, name, pass, ip);
        thread.setDaemon(true);
        thread.start();       
        
    }
    
    /**
     * Nastavi scenu hry
     */
    public void setSceneHra(){
        primaStage.setScene(scene);
    }
    /**
     * Nastavi scneu uvod
     */
    public void setSceneUvod(){
        primaStage.setScene(uvod);
    }
    
    /**
     * Nastavi premenu hra
     * @param hra spustena hra
     */
    public void setHra(Hra hra){
        this.hra = hra;
    }
    
    /**
     * Zobrazi spravu v chate
     * @param sprava 
     */
    public void zobrazSpravu(String sprava){
        chat.appendText(sprava+ "\n");
    }
    
    /**
     * Updatuje skore hraca
     * @param skore skore hraca
     */
    public void UpdateMojeSkore(String skore){
        mojeData.setText(skore);
    }
    
    /**
     * Updatuje skore oponenta
     * @param skore skore oponenta
     */
    public void UpdateSuperSkore(String skore){
        superData.setText(skore);
    }
    
    /**
     * Vytvori a zobrazi hraciu dosku na zakladae parametrov hry
     * @param tiles 
     */
    public void zobrazDosku(List tiles){
        Pane hraciaDoska = new HraciaDoska(tiles, POCET, RIADOK);
        hraciaDoska.setTranslateX(5);
        hraciaDoska.setTranslateY(5);
        main.setCenter(hraciaDoska);
        primaStage.setScene(scene);
    }
    
    /**
     * Zablokuje tlacidlo Nova Hra
     * @param value true/false
     */
    public void disableButton(boolean value){
        btnNovaHra.setDisable(value);
    }
    
    /**
     * Zablokuje vstup chatu
     * @param value true/false
     */
    public void disabeInput(boolean value){
        input.setDisable(value);
    }
    
    /**
     * Vrati Pocet kociek hry
     * @return pocet kociek 
     */
    public int getPocet(){
        return POCET;
    }
    
    /**
     * Nastavi info spravu na uvodnej scene
     * @param value text info spravy
     */
    public void setInfo(String value){
        info.setText(value);
        info.setTextFill(Color.RED);
    }
    
    /**
     * Vymaze spravu na uvodnej scene
     */
    public void clearInfo(){
        info.setText("");
    }
    
    /**
     * Zistuje, ci je aktivna scena hra
     * @return true/false 
     */
    public boolean isGameScene(){
        if(primaStage.getScene().equals(scene)){
            return true;
        }
        return false;
    }
    
}
