package com.match.controller;

import com.match.model.basic.chat.ClientHandler;
import com.match.model.basic.constants.SignUpError;
import com.match.model.basic.constants.UiPrompt;
import com.match.model.basic.constants.UserOperationError;
import com.match.model.basic.tools.GeneratingTools;
import com.match.view.chat.ChatView;
import com.match.view.dialog.SearchDialog;
import com.match.view.dialog.PromptDialog;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import com.match.view.dialog.UserDialog;

import java.net.URL;
import java.util.*;

import static com.alibaba.fastjson.JSON.toJSONString;

public class ControllerHall implements Initializable {

    public Button back;
    public Button newRoom;
    public Button exitApp;
    public AnchorPane Hall;
    public Button search;
    public Button registerUser;

    public Hall hall = HallView.hall;
    public HallView hallView = new HallView();
    public Client client = HallView.client;
    public ChatView chatView = new ChatView();
    public ListView<String> roomList = new ListView<>();
    public GeneratingTools generatingTools = new GeneratingTools();
    public static ObservableList<String> chatRoomList = FXCollections.observableArrayList();
    public static ObservableList<String> searchResult = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tooltip.install(registerUser, new Tooltip(UiPrompt.SIGN_UP_BUTTON));
        Tooltip.install(newRoom, new Tooltip(UiPrompt.NEW_ROOM_BUTTON));
        Tooltip.install(search, new Tooltip(UiPrompt.SEARCH_ROOM_BUTTON));
        Tooltip.install(back, new Tooltip((UiPrompt.BACK_BUTTON)));
        Tooltip.install(exitApp, new Tooltip(UiPrompt.APP_EXIT_BUTTON));

        roomList.setItems(chatRoomList);
        roomList.setEditable(false);
        roomList.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) ->{
                    roomList.setOnMouseClicked(event -> {
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                            try {
                                if(HallView.user.getName() == null) {
                                    new ErrorDialog(UserOperationError.UNREGISTERED);
                                    return;
                                }
                                String roomName = String.valueOf(roomList.getFocusModel().getFocusedItem());
                                roomName = roomName.substring(0, roomName.indexOf("(")); //避免出现把人数信息也一并上传到服务器，导致加入房间失败
                                selectRoom(roomName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
        );

        searchResult.addListener(new ListChangeListener<String>(){
            @Override
            public void onChanged(Change<? extends String> change) {
                while(change.next()){
                    if(change.wasAdded()){
                        //监听到添加动作，说明用户进行了搜索，所以停止房间列表获取任务
                        HallView.timer.cancel();
                    }
                    if(change.wasRemoved()){
                        //监听到删除动作，说明用户使用了back后退按钮，退出了搜索页，所以开始房间列表获取任务
                        hallView.getChatRoomListTime(ControllerHall.this);
                    }
                }
            }
        });
    }


    public void newRoomBtnEvent(){
        if(HallView.user.getName() == null) {
            new ErrorDialog(UserOperationError.UNREGISTERED);
            return;
        }
        generatingTools.initJson();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("新建聊天室");
        dialog.setHeaderText("新聊天室的名字");
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            String name = result.get();
            hall.newRoom(name);
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(ClientHandler.newRoomError) {
            new ErrorDialog(UserOperationError.CHATROOM_ALREADY_EXISTS);
            ClientHandler.newRoomError = false;
        }

    }

    public void showSearchResult(){
        this.backBtnEvent(); //先删除搜索结果列表里原有的数据
        String result = hall.getSearchResult();
        chatRoomList.clear();
        chatRoomList.add(result);
        searchResult.add(result);
    }

    public void searchBtnEvent(){
        String chatRoomName = new SearchDialog("搜索聊天室", "搜索聊天室", "输入聊天室的名称")
                .getSearchContent();
        if(chatRoomName == null) return;
        hall.searchRoom(chatRoomName);
    }

    public void backBtnEvent(){
        if(searchResult.size() == 0) return;
        searchResult.remove(0);
    }

    public void showRoomList(){
        generatingTools.initJson();
        ArrayList<String> roomNameList = hall.roomList();
        chatRoomList.clear();
        chatRoomList.addAll(roomNameList);
    }

    public void registerUserEvent(){
        //登录检查
        if(HallView.user.getName() != null) {
            new UserDialog(HallView.user.getName(), HallView.user.getId());
            return;
        }
        RegisterDialog registerDialog = new RegisterDialog(client);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(registerDialog.user == HallView.user) return; //如果相等说明点的是取消，防止跳出错误弹窗
        if(!ClientHandler.register || HallView.user.getId() == 10000){
            System.out.println(ClientHandler.register);
            System.out.println(HallView.user.getId());
            new PromptDialog("注册错误", SignUpError.SIGN_UP_FAIL);
            HallView.user.setName(null);
            return;
        }
        HallView.user.setName(registerDialog.user.getName());

    }

    public void selectRoom(String roomName){
        if(HallView.user.getName() == null){
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
