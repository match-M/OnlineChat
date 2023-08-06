package com.match.controller;

import com.match.model.basic.constants.DefaultConfigFileName;
import com.match.model.basic.constants.DefaultConfigName;
import com.match.model.basic.exception.ConfigNameNotFoundException;
import com.match.view.hall.HallView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import com.match.view.dialog.ErrorDialog;
import com.match.view.dialog.PromptDialog;
import com.match.model.basic.config.ConfigList;
import com.match.model.basic.tools.NetWorkTools;
import com.match.model.basic.tools.ConfigTools;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ControllerOffline implements Initializable {

    public boolean configError = false;
    public Button exit;
    public Button finish;
    public TextField hostPort;
    public Button localTest;
    public Button intranetTest;
    public Button outerNetTest;
    public TextField ipAddress;
    public Button configFileRepair;
    public Label file_repair_result;
    public Button configFileCheck;
    public Label file_check_result;
    public Button connectionTesting;
    public Label portExceptionPrompt;
    public Label initExceptionPrompt;
    public Label connectionTesting_result;
    public Label networkTest_local_result;
    public Label networkTest_intranet_result;
    public Label networkTest_outerNet_result;


    public ConfigTools configTools = new ConfigTools();
    public ConfigList configList = new ConfigList();
    public NetWorkTools netWorkTools = new NetWorkTools();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initExceptionPrompt.setText(HallView.initErrorInfo);
        hostPort.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    Integer.parseInt(newValue);
                    portExceptionPrompt.setText("");
                }catch (NumberFormatException e){
                    viewResult(portExceptionPrompt, Color.RED, "请输入数字！");
                }

            }
        });
    }

    public void btn_localTestEvent(){
        boolean localStat = netWorkTools.localTest();
        if (localStat){
            viewResult(networkTest_local_result, Color.GREEN, "✔");
        }else {
            viewResult(networkTest_local_result, Color.RED, "×");
        }
    }

    public void btn_intranetTestEvent() {
        try {
            configList = new ConfigList(); //防止出现配置文件丢失修复后配置依然无法找到
            String value = configList.getConfig(DefaultConfigName.VIEW_INTRANET_TEST_PROMPT);
            int result = Integer.parseInt(value);
            String[] keys = new String[]{DefaultConfigName.VIEW_INTRANET_TEST_PROMPT};
            Object[] results = new Object[1];
            if (result == 1) {
                result = new PromptDialog().resultDialog("网络诊断警告",
                        "这是一个试验功能，网关不是以”192.168.xx.1“格式的用户，\n" +
                                "内网联通性检测的结果不一定准确！", "不再显示该弹窗");
                results[0] = result;
                configTools = new ConfigTools(DefaultConfigFileName.USER_SETTING_CONFIG);
                configTools.upConfig(keys, results);
            }
        }catch (ConfigNameNotFoundException | NullPointerException e){
            e.printStackTrace();
        }finally {
            int intranetStat = netWorkTools.intranetTest();
            switch (intranetStat) {
                case -1: {
                    viewResult(networkTest_intranet_result, Color.ORANGE, "×");
                    break;
                }
                case 0: {
                    viewResult(networkTest_intranet_result, Color.RED, "×");
                    break;
                }
                case 1: {
                    viewResult(networkTest_intranet_result, Color.GREEN, "✔");
                    break;
                }
            }
        }
    }

    public void btn_outerNetTestEvent(){
        boolean outerNetStat = netWorkTools.outerNetTest();
        if(outerNetStat){
            viewResult(networkTest_outerNet_result, Color.GREEN, "✔");
        }else{
            viewResult(networkTest_outerNet_result, Color.RED, "×");
        }
    }

    public void btn_configFileCheck(){
        boolean statException = false;
        int statCode;
        HashMap<String, HashMap<String, Integer>> checkRes = configTools.getConfigFileCheckResult();
        HashMap<String ,Integer> code;
        for(String key : DefaultConfigFileName.CONFIG_NAME_LIST){
            code = checkRes.get(key);
            key = key+"Code";
            statCode = code.get(key);
            if(statCode != 1) {
                statException = true;
                break;
            }
        }
        if(statException) {
            viewResult(file_check_result, Color.RED, "×");
            return;
        }
        viewResult(file_check_result, Color.GREEN, "✔");
    }

    public void btn_configFileRepairEvent(){
        boolean flag = configTools.repair();
        if(flag){
            viewResult(file_repair_result, Color.GREEN, "✔");
            return;
        }
        viewResult(file_repair_result, Color.RED, "×");
    }

    public void btn_connectionTestingEvent(){
        String ip = ipAddress.getText();
        int port = Integer.parseInt(hostPort.getText());
        boolean connectionRes = netWorkTools.connectionTesting(ip, port);
        if(!connectionRes){
            viewResult(connectionTesting_result, Color.GREEN, "✔");
            finish.setDisable(false);
            return;
        }
        viewResult(connectionTesting_result, Color.RED, "×");
        finish.setDisable(true);
    }

    public void btn_finishEvent(){
        String ip = ipAddress.getText();
        int port = Integer.parseInt(hostPort.getText());
        configTools.writeConfig(ip, port);
    }

    public void btn_exitEvent(){
        System.exit(0);
    }

    public void viewResult(Label resultText, Color color, String result){
        resultText.setTextFill(color);
        resultText.setText(result);
    }


}
