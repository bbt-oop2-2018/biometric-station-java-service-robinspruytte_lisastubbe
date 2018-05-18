/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxmlmqttchatclient;

import be.vives.oop.mqtt.chatservice.IMqttMessageHandler;
import be.vives.oop.mqtt.chatservice.MqttChatService;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ChatClientController implements Initializable, IMqttMessageHandler {
    
    @FXML private Button send;
    @FXML private TextArea message;
    @FXML private TextArea conversation;
    @FXML private TextField nicknameInputField;
    @FXML private TextField channelInputField;
    
    // Allows us to use the wrapper for sending chat messages via MQTT
    private MqttChatService chatService;
    private final Random random = new Random();
    private final int min = 0;
    private final int max = 100;
    private int time = 0;
    
    @FXML
    private void ChannelVariablesUpdateHandeler(ActionEvent event){
        if (!channelInputField.getText().equals("") && !channelInputField.getText().equals(chatService.getChannelName())) {
            chatService.switchChannel(channelInputField.getText());
            conversation.setText("");
        }
        
        if (!nicknameInputField.getText().equals("")) {
            chatService.changeClientid(nicknameInputField.getText());
        }
    }
    
    @FXML
    private void handleSend(ActionEvent event) {
        try {
            sendMessage();
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    public void handleTypedKey(KeyEvent key){
       if (key.getCode().toString().equals("ENTER")) {
           try {
               sendMessage();
           } catch (InterruptedException ex) {
               Logger.getLogger(ChatClientController.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Create a chat service and allow this class to receive messages
        chatService = new MqttChatService();
        chatService.setMessageHandler(this);
        
        // When the user closes the window we need to disconnect the client
        disconnectClientOnClose();
    }    

    // This method is called if a chat message is received from mqtt
    @Override
    public void messageArrived(String channel, String message) {
        conversation.appendText(message + "\n");
    }
    
    private void sendMessage () throws InterruptedException {
        time++;
        int hartBeat = random.nextInt(max + 1 - min) + min;
        double accelx = (random.nextInt(max + 1 - min) + min)/10;
        double accely = (random.nextInt(max + 1 - min) + min)/10;
        double accelz = (random.nextInt(max + 1 - min) + min)/10;
        int temperature = random.nextInt(max + 1 - min) + min;
        int fingerprint = random.nextInt(max + 1 - min) + min;
        
        chatService.sendMessage("{ \"timestamp\": " + time + ", \"bpm\": " + hartBeat + ", \"temperature\": " + temperature + ", \"accellcx\": " + accelx + ", \"accellcy\": " + accely + ", \"accellcz\": " + accelz + "}");
        System.out.println("{ \"timestamp\": " + time + ", \"bpm\": " + hartBeat + ", \"temperature\": " + temperature + ", \"accellcx\": " + accelx + ", \"accellcy\": " + accely + ", \"accellcz\": " + accelz + "}");
//        int Time = 140;
//        for (int i = 0; i < 140; i++) {
//            Time = i++;
//            int hartBeat = random.nextInt(max + 1 - min) + min;
//            double accelx = (random.nextInt(max + 1 - min) + min)/10;
//            double accely = (random.nextInt(max + 1 - min) + min)/10;
//            double accelz = (random.nextInt(max + 1 - min) + min)/10;
//            int temperature = random.nextInt(max + 1 - min) + min;
//            int fingerprint = random.nextInt(max + 1 - min) + min;
//            chatService.sendMessage("{ \"timestamp\": " + Time + ", \"bpm\": " + hartBeat + ", \"temperature\": " + temperature + ", \"accellcx\": " + accelx + ", \"accellcy\": " + accely + ", \"accellcz\": " + accelz + "}");
//            System.out.println("{ \"timestamp\": " + Time + ", \"bpm\": " + hartBeat + ", \"temperature\": " + temperature + ", \"accellcx\": " + accelx + ", \"accellcy\": " + accely + ", \"accellcz\": " + accelz + "}");
//            try{
//                Thread.sleep(1000);
//            }
//            catch(InterruptedException e){
//                System.out.println("main thread interrupted");
//            }
//        }
//        message.setText("");
    }
    
    private void disconnectClientOnClose() {
        // Source: https://stackoverflow.com/a/30910015
        send.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                // scene is set for the first time. Now its the time to listen stage changes.
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        // stage is set. now is the right time to do whatever we need to the stage in the controller.
                        ((Stage) newWindow).setOnCloseRequest((event) -> {
                            chatService.disconnect();
                        });
                    }
                });
            }
        });
    }
}
