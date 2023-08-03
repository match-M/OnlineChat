package com.match.controller;

import com.match.model.basic.constants.SignUpError;
import com.match.model.basic.constants.UiPrompt;
import com.match.model.basic.constants.UserOperationError;
import com.match.model.basic.tools.GeneratingTools;
import com.match.view.chat.ChatView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import com.match.model.basic.hall.Hall;
import com.match.model.basic.client.Client;
import com.match.view.hall.HallView;
import com.match.view.dialog.ErrorDialog;
import com.match.view.dialog.RegisterDialog;
import com.match.view.dialog.PromptDialog;
import com.match.view.dialog.UserDialog;
import com.match.model.basic.user.User;

import java.net.URL;
import java.util.*;

import static com.alibaba.fastjson.JSON.toJSONString;

public class ControllerHall implements Initializable {
    public Button newRoom;
    public Button exitApp;
    public AnchorPane Hall;
    public Button registerUser;
    public Hall hall = HallView.hall;
    public User user = HallView.user;
    public Client client = HallView.client;
    public ChatView chatView = new ChatView();
    public ListView<String> roomList = new ListView<>();
    public GeneratingTools generatingTools = new GeneratingTools();
    public static ObservableList<String> chatRoomList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tooltip.install(registerUser, new Tooltip(UiPrompt.SIGN_UP_BUTTON));
        Tooltip.install(newRoom, new Tooltip(UiPrompt.NEW_ROOM_BUTTON));
        Tooltip.install(exitApp, new Tooltip(UiPrompt.APP_EXIT_BUTTON));

        roomList.setItems(chatRoomList);
        roomList.setEditable(false);
        roomList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                try {
                    String roomName = String.valueOf(roomList.getFocusModel().getFocusedItem());
                    selectRoom(roomName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public void newRoomBtnEvent(){
        if(user.getName() == null) {
            new ErrorDialog(UserOperationError.UNREGISTERED);
        }else {
            generatingTools.initJson();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("新建聊天室");
            dialog.setHeaderText("新聊天室的名字");
            Optional<String> result = dialog.showAndWait();
            boolean exist = false;
            if(result.isPresent()){
                String name = result.get();
                exist = hall.newRoom(name);
            }
            if(exist){
                new ErrorDialog(UserOperationError.CHATROOM_ALREADY_EXISTS);
            }
        }
    }

    public void viewRoomList(){
        generatingTools.initJson();
        ArrayList<String> roomNameList = hall.roomList();
        chatRoomList.clear();
        chatRoomList.addAll(roomNameList);
    }

    public void registerUserEvent(){
        //登录检查
        if(user.getName() == null) {
            RegisterDialog registerDialog = new RegisterDialog(client);
            user = registerDialog.user;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(HallView.user.getId() == 10000){
                new PromptDialog("注册错误", SignUpError.SIGN_UP_FAIL);
                user.setName(null);
                return;
            }
            HallView.user.setName(user.getName());
        }else {
            new UserDialog(HallView.user.getName(), HallView.user.getId());
        }
    }

    public void selectRoom(String roomName){
        if(user.getName() == null){
            new ErrorDialog(UserOperationError.UNREGISTERED);
            return;
        }
        hall.selectRoom(roomName);
        HallView.hallWindows.hide();
        try {
            chatView.showChatRoom(roomName);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void exitEvent(){
        generatingTools.initJson();
        try {
            generatingTools.json("mode", "exit");
            if(HallView.user.getId() >= 10001){
                generatingTools.json("id", HallView.user.getId());
            }else {
                generatingTools.json("id", "null");
            }
            client.send(toJSONString(generatingTools.getJson()));
            System.exit(0);
        }catch (Exception e) {
            System.exit(0); //防止出现网络异常导致程序无法正常退出
            throw new RuntimeException(e);
        }
    }



}
