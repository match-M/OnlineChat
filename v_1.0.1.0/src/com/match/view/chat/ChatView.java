package com.match.view.chat;

import com.match.view.hall.HallView;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.match.controller.ControllerChat;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

public class ChatView {
    public static Stage chatWindow;
    public static Stage hallWindows;
    public static HallView hallView = new HallView();
    private final ControllerChat CONTROLLERCHAT = new ControllerChat();
    public void showChatRoom(String roomName) throws IOException {
        hallWindows = HallView.hallWindows;
        Parent chatWindowFxml = FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource("/com/match/view/chat/ui/fxml/Chat.fxml")));
        Scene scene = new Scene(chatWindowFxml);
        chatWindow = new Stage();
        chatWindow.getIcons().add(new Image("com/match/view/hall/ui/ico/AppIco.png"));
        chatWindow.setTitle(roomName);
        chatWindow.setScene(scene);
        chatWindow.setResizable(false);
        chatWindow.show();
        init(roomName);
        //监听窗口是否被关闭
        chatWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                CONTROLLERCHAT.exitEvent();
            }
        });

    }
    public void init(String roomName){
        CONTROLLERCHAT.sendMessageHanding.setRoomName(roomName);
    }

}
