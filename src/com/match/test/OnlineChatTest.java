package com.match.test;

import com.match.model.basic.tools.ConfigTools;
import com.match.model.basic.config.ConfigList;
import javafx.application.Application;
import javafx.stage.Stage;
import com.match.view.chat.ChatView;
import java.util.Objects;


public class OnlineChatTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new ChatView().showChatRoom("hello");
    }
}
