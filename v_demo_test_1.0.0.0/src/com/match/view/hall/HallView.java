package com.match.view.hall;

import com.match.model.basic.constants.ClientConst;
import com.match.model.basic.constants.InitErrorInfoConst;
import com.match.model.basic.config.ConfigList;
import com.match.model.basic.exception.ConfigFileNotFoundException;
import com.match.model.basic.exception.ConfigNameNotFoundException;
import com.match.model.basic.tools.ConfigTools;
import com.match.view.dialog.ServerChooseDialog;
import com.match.model.basic.chat.message.ResultMessageHandling;
import com.match.model.basic.client.Client;
import com.match.model.basic.user.User;
import com.match.model.basic.hall.Hall;
import com.match.controller.ControllerHall;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.net.SocketException;
import java.util.Objects;
import java.util.TimerTask;
import java.util.Timer;

public class HallView extends Application {

    public static Hall hall;
    public static User user;
    public static Timer timer;
    public static Client client;
    public static Stage hallWindows;
    public static String initErrorInfo;
    public static ConfigList configList;
    private static boolean initError = false;
    public static ResultMessageHandling resultMessageHandling;


    public static void initBasicService(){
        user = new User();
        hall = new com.match.model.basic.hall.Hall(client);
        resultMessageHandling = new ResultMessageHandling();
    }

    public static void initOnlineChat(){
        try {
            new ConfigTools().selfCheck(); //配置文件自检
            configList = new ConfigList("server");
            client = new Client(configList.getConfig(ClientConst.IP),
                    Integer.parseInt(configList.getConfig(ClientConst.PORT)));
            client.initClient();
            initBasicService();
        }catch (InterruptedException | SocketException e) {
            if(e instanceof  InterruptedException)
              return;
            try {
                ServerChooseDialog serverChooseDialog = new ServerChooseDialog(
                        "服务器连接错误",
                        "无法连接到默认服务器，请选择一个新的服务器吧",
                        "输入新服务器的ip地址和端口(port)，格式(ip:port)：");
                client = serverChooseDialog.client;
                client.initClient();
                initBasicService();
            } catch (Exception e2) {
                initError = true;
                initErrorInfo = InitErrorInfoConst.INIT_NETWORK_ERROR;
                e2.printStackTrace();
            }
        }catch (ConfigNameNotFoundException | ConfigFileNotFoundException configError){
            initError = true;
            initErrorInfo = configError.getMessage();
        }
    }
    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        initOnlineChat();
        if(initError){
            initExceptionPage(primaryStage);
        }else {
            normalPage(primaryStage);
        }

    }

    public void normalPage(Stage primaryStage){
        hallWindows = primaryStage;
        Parent Hall;
        ControllerHall controllerHall = new ControllerHall();
        try {
            Hall = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/com/match/view/hall/ui/fxml/Hall.fxml")));
            Scene scene = new Scene(Hall);
            primaryStage.getIcons().add(new Image("com/match/view/hall/ui/ico/AppIco.png"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);//设置不能窗口改变大小
            primaryStage.setTitle("在线聊天");//设置标题
            primaryStage.show();
            getChatRoomListTime(controllerHall);
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    controllerHall.exitEvent();
                }
            });
            //隐藏则暂停获取
            primaryStage.setOnHidden(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    HallView.timer.cancel();
                }
            });
            //窗口显示就继续获取
            primaryStage.setOnShowing(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    getChatRoomListTime(controllerHall);
                }
            });


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initExceptionPage(Stage primaryStage){
        Parent exceptionPage;
        try {
            exceptionPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/match/view" +
                    "/offline/ui/fxml/OfflineMode.fxml")));
            Scene scene = new Scene(exceptionPage);
            primaryStage.getIcons().add(new Image("com/match/view/offline/ui/ico/EngineerIco.png"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);//设置不能窗口改变大小
            primaryStage.setTitle("初始化工程页");//设置标题
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.exit(0);
                }
            });

        }catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void getChatRoomListTime(ControllerHall controllerHall){
        Timer timer = new Timer();
        HallView.timer = timer;
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controllerHall.viewRoomList();
                    }

                });
            }
        }, 0, 2000);
    }

}