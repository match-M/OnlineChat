package com.match.view.dialog;

import com.match.model.basic.client.Client;
import javafx.scene.control.TextInputDialog;
import javafx.stage.StageStyle;

import java.util.Optional;

public class SearchDialog {
    private String title;
    private String header;
    private String contents;
    public SearchDialog(String title, String header, String contents){
        this.title = title;
        this.header = header;
        this.contents = contents;
    }

    public String getSearchContent(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(this.title);
        dialog.setHeaderText(this.header);
        dialog.setContentText(this.contents);
        Optional<String> result = dialog.showAndWait();
        String content = null;

        if(result.isPresent()) {
            try {
                content = result.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return content;
    }

}
