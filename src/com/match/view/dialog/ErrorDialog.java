package com.match.view.dialog;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class ErrorDialog {
    public ErrorDialog(String errorContent) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("错误");
        alert.setContentText(errorContent);
        alert.showAndWait();
    }
}
