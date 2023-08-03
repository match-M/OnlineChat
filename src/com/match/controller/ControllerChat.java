package com.match.controller;

import com.match.model.basic.constants.UiPrompt;
import com.match.model.basic.chat.message.ResultMessageHandling;
import com.match.model.basic.chat.message.SendMessageHanding;
import com.match.model.basic.chat.message.SystemMessageHanding;
import com.match.model.basic.client.Client;
import com.match.model.basic.user.User;
import com.match.view.chat.ChatView;
import com.match.view.hall.HallView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ControllerChat implements Initializable{
    public Button exit;
    public Button send;
    public TextArea inText;

    public User user = HallView.user;
    public Label errorPrompt = new Label();
    public Client client = HallView.client;
    public ListView<HBox> chatBox = new ListView<>();
    public SystemMessageHanding systemMessageHanding = new SystemMessageHanding();
    public static ObservableList<HBox> message = FXCollections.observableArrayList();
    public ResultMessageHandling resultMessageHandling = new ResultMessageHandling();
    public SendMessageHanding sendMessageHanding = new SendMessageHanding(client, user);


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatBox.setItems(message);
        chatBox.getStylesheets().add("com/match/view/chat/ui/css/ChatBox.css");
        chatBox.setCellFactory(new Callback<ListView<HBox>, ListCell<HBox>>() {
            public ListCell<HBox> call(ListView<HBox> param) {
                // TODO Auto-generated method stub
                return new ListCell<HBox>() {
                    @Override
                    //只定义编辑单元格一定要重写的方法
                    protected void updateItem(HBox item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            this.setGraphic(item);
                        }

                    }
                };
            }
        });
        inText.setWrapText(true);
        inText.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                if(e.isControlDown())
                    lineFeed(); //换行
                else
                    sendBtnEvent();
            }
        });
        Tooltip.install(send, new Tooltip(UiPrompt.SEND_BUTTON));
        Tooltip.install(exit, new Tooltip(UiPrompt.ROOM_EXIT_BUTTON));
    }

    public void otherMessage(){
        Label userInfo_id = new Label("id:" + resultMessageHandling.getId());
        Label userInfo_name = new Label(resultMessageHandling.getName());
        Label msg = new Label(resultMessageHandling.getMessage());

        userInfo_id.getStylesheets().add("com/match/view/chat/ui/css/UserInfo.css");
        userInfo_name.getStylesheets().add("com/match/view/chat/ui/css/UserInfo.css");
        msg.getStylesheets().add("com/match/view/chat/ui/css/ResultMessage.css");
        msg.setId("resMessage");


        HBox hBox = chatBoxLayout(userInfo_id, userInfo_name, msg, Pos.TOP_LEFT, Pos.BOTTOM_LEFT, 1);

        message.add(hBox);
        viewNewMessage();

    }

    public void systemMessage(){
        Label sysMsg = new Label(systemMessageHanding.getSystemMessage());

        sysMsg.getStylesheets().add("com/match/view/chat/ui/css/SystemMessage.css");

        HBox hBox = chatBoxLayout(sysMsg, Pos.BOTTOM_CENTER, Pos.BOTTOM_CENTER);

        message.add(hBox);
        viewNewMessage();

    }

    public void sendBtnEvent(){
        String sendMsg = inText.getText();
        if(sendMsg == null || sendMsg.length() == 0 || sendMsg.trim().equals("")){
            errorPrompt.setText("发送内容不能为空！");
            return;
        }
        errorPrompt.setText("");
        inText.clear();
        sendMessageHanding.message(sendMsg);
        Label userInfo_id = new Label( "id:"+user.getId());
        Label userInfo_name = new Label(user.getName());
        Label msg = new Label(sendMsg);

        userInfo_id.getStylesheets().add("com/match/view/chat/ui/css/UserInfo.css");
        userInfo_name.getStylesheets().add("com/match/view/chat/ui/css/UserInfo.css");
        msg.getStylesheets().add("com/match/view/chat/ui/css/SendMessage.css");
        msg.setId("sendMessage");

        HBox hBox = chatBoxLayout(userInfo_id, userInfo_name, msg, Pos.TOP_RIGHT, Pos.BOTTOM_RIGHT, 0);

        message.add(hBox);
        viewNewMessage();

    }



    public HBox chatBoxLayout(Label userInfo_id, Label userInfo_name, Label msg,
                              Pos align1, Pos align2, int order){
        HBox hBox = new HBox();
        VBox vBox = new VBox();
        vBox.getChildren().add(userInfo_id);
        vBox.getChildren().add(userInfo_name);
        vBox.setAlignment(align1);
        if(order == 0) {
            hBox.getChildren().add(msg);
            hBox.getChildren().add(vBox);
        }else {
            hBox.getChildren().add(vBox);
            hBox.getChildren().add(msg);
        }
        hBox.setPadding(new Insets(10));
        hBox.setAlignment(align2);

        return hBox;

    }

    public void viewNewMessage(){
        chatBox.scrollTo(message.size()+3);
    }

    public void lineFeed(){
        int caretPosition = inText.getCaretPosition();
        // 获得输入文本，此文本不包含刚刚输入的换行符
        String text = inText.getText();
        // 获得光标两边的文本
        String front = text.substring(0, caretPosition);
        String end = text.substring(caretPosition);
        // 在光标处插入换行符
        inText.setText(front + System.lineSeparator() + end);
        // 将光标移至换行符
        inText.positionCaret(caretPosition + 1);
    }

    public HBox chatBoxLayout(Label msg, Pos align1, Pos align2){
        HBox hBox = new HBox();
        VBox vBox = new VBox();
        vBox.getChildren().add(msg);
        vBox.setAlignment(align1);
        hBox.getChildren().add(vBox);
        hBox.setAlignment(align2);

        return hBox;

    }

    public void exitEvent(){
        sendMessageHanding.message("exit(room);");
        message.clear();
        ChatView.chatWindow.close();
        ChatView.hallWindows.show();
    }



}
