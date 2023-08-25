package com.match.model.basic.hall;

import com.match.model.basic.user.User;
import com.match.model.basic.client.Client;
import com.match.model.basic.tools.ParsingTools;
import com.match.model.basic.tools.GeneratingTools;

import static com.alibaba.fastjson.JSON.toJSONString;

public class Register {


    private User user;
    private Client client;

    GeneratingTools generatingTools = new GeneratingTools();

    public Register(Client client, User user) {
        this.user = user;
        this.client = client;
    }

    public void singUp(){
        this.upload();
    }

    public void upload() {
        try {
            generatingTools.json("mode", "register");
            generatingTools.json("name", user.getName());
            client.send(toJSONString(generatingTools.getJson()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
