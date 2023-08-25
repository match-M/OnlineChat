package com.match.view.dialog;

import com.match.model.basic.hall.Register;
import com.match.view.hall.HallView;
import javafx.scene.control.TextInputDialog;

import com.match.model.basic.user.User;
import com.match.model.basic.client.Client;
import javafx.stage.StageStyle;
import java.util.Optional;

public class RegisterDialog {

    public User user;

    public RegisterDialog(Client client) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("注册");
        dialog.setHeaderText("新用户注册");
        dialog.setContentText("用户名");
        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()) {
            user = new User();
            String userName = result.get();
            register(userName, client);

        }else{
            user = HallView.user;
        }
    }
    public void register(String userName, Client client){
        if(userName == null || userName.length() == 0 || userName.trim().equals("")) {
            user = HallView.user;
            return;
        }
        user.setName(userName);
        Register register = new Register(client, user);
        register.singUp();

    }

}
