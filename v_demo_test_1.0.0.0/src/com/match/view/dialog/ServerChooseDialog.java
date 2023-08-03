package com.match.view.dialog;

import com.match.model.basic.client.Client;

import javafx.scene.control.TextInputDialog;
import javafx.stage.StageStyle;

import java.net.SocketException;
import java.util.Optional;

public class ServerChooseDialog {
    public Client client;
    public ServerChooseDialog(String title, String Header, String contents) throws SocketException{
        TextInputDialog dialog = new TextInputDialog();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(title);
        dialog.setHeaderText(Header);
        dialog.setContentText(contents);
        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()) {
            try {
                String server = result.get();
                String ip = server.substring(0, server.indexOf(":"));
                String port = server.substring(server.indexOf(":") + 1);
                this.client = new Client(ip, Integer.parseInt(port));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.exit(0);
        }

    }
}
