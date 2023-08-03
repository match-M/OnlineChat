package com.match.view.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.util.Objects;
import java.util.Optional;

public class PromptDialog {

    public PromptDialog(){}
    public PromptDialog(String title, String prompt) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setContentText(prompt);
        alert.showAndWait();
    }
    public int resultDialog(String title, String prompt, String buttonText){
        ButtonType button;
        ButtonType finish;
        int result = 1;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setContentText(prompt);
        button = new ButtonType(buttonText);
        finish = new ButtonType("确定");
        alert.getButtonTypes().setAll(button, finish);
        Optional<ButtonType> buttonResult = alert.showAndWait();
        if(buttonResult.isPresent()&&buttonResult.get() == button)
            result = 0;
        return result;
    }
}
