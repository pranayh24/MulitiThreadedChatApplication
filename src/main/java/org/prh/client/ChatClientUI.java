package org.prh.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientUI extends Application {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private TextArea chatArea;
    private TextField messageField;
    private String username;


    @Override
    public void start(Stage primaryStage) {
        chatArea = new TextArea();
        chatArea.setEditable(false); // Prevent editing the chat area
        messageField = new TextField();
        messageField.setPromptText("Type your message...");
        Button sendButton = new Button("Send");

        VBox root = new VBox(10, chatArea, messageField, sendButton);
        Scene scene = new Scene(root, 400, 300);
        String css = getClass().getResource("/application.css") != null
                ? getClass().getResource("/application.css").toExternalForm()
                : null;

        if (css != null) {
            scene.getStylesheets().add(css);
        } else {
            System.out.println("CSS file not found!");
        }


        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start the client connection in a new thread
        new Thread(this::startClient).start();

        // Action for send button
        sendButton.setOnAction(e -> sendMessage());
        messageField.setOnAction(e -> sendMessage());

    }

    // Start the client and connect to the server
    private void startClient(){
        try{
            socket = new Socket("localhost", 8188);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);

            // Ask for username
            Platform.runLater(() -> {
                TextInputDialog dialog= new TextInputDialog("Username");
                dialog.setHeaderText("Enter your username:");
                dialog.setContentText("Username:");
                dialog.showAndWait().ifPresent(name -> username = name);
                out.println(username); // send the username to the server
            });

            new Thread(this::listenForMessages).start();
        }
        catch(Exception e){
            showErrorAlert("Error connecting to server. Please try again later.");
            e.printStackTrace();
        }
    }

    // Listen for messages and display them in chat area
    private void listenForMessages(){
        String message;
        try {
            while ((message = in.readLine()) != null) {
                String finalMessage = message;
                Platform.runLater(() -> chatArea.appendText(finalMessage + "\n"));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Send a message to the server
    private void sendMessage(){
        String message = messageField.getText();
        if(!message.trim().isEmpty()){
            out.println(message); // Send the message to the server
            messageField.clear(); // Clear the message field
        }
    }

    @Override
    public void stop() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String message){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
