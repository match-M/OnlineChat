package com.match.view.dialog;

import javafx.scene.control.Alert;

public class UserDialog {

    public UserDialog(String name, int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("用户页");
        alert.setContentText("这是您的用户名:"+name+"\n这是您的ID:"+id);
        alert.showAndWait();
    }
}
