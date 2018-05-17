/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaconsoleservice;

import be.vives.oop.mqtt.chatservice.IMqttMessageHandler;
import be.vives.oop.mqtt.chatservice.MqttChatService;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author gebruiker
 */
public class FXMLDocumentController implements Initializable, IMqttMessageHandler {
    private SerialLineReceiver receiver; 
    private MqttChatService chatService;
    private final int COM_PORT = 0; //ComePort value - 1
    
    @FXML
    private Button COMButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //receiver.setComPortIndex(COMPort);
        
        chatService = new MqttChatService();
        chatService.setMessageHandler(this);
        
        try {
            this.receiver = new SerialLineReceiver(COM_PORT, 115200, true);
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        receiver.setLineListener(new SerialPortLineListener() {
            @Override
            public void serialLineEvent(SerialData data) {
                System.out.println("Received data from the serial port: " + data.getDataAsString());
                try {
                    sendMessage(data.getDataAsString());
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        disconnectClientOnClose();
    }    

    @Override
    public void messageArrived(String channel, String message) {
        System.out.println("message");
    }
    
    private void sendMessage (String message) throws InterruptedException {
        chatService.sendMessage(message);
    }
    
    private boolean isNumber(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException exeption) {
            return false;
        }
    }
    
    private void disconnectClientOnClose() {
        // Source: https://stackoverflow.com/a/30910015
        COMButton.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
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
