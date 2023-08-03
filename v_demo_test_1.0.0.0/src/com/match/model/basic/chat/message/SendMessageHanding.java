package com.match.model.basic.chat.message;

import com.match.model.basic.client.Client;
import com.match.model.basic.tools.GeneratingTools;
import com.match.model.basic.user.User;

import static com.alibaba.fastjson.JSON.toJSONString;

public class SendMessageHanding {
    private User user;
    private Client client;
    private String sendMessage;
    private String roomName;
    private GeneratingTools generatingTools = new GeneratingTools();

    public SendMessageHanding(Client client, User user) {
        this.user = user;
        this.client = client;
    }

    public void setRoomName(String roomName){
        this.roomName = roomName;
    }

    public void message(String sendMessage){
        this.sendMessage = sendMessage;
        this.sendMessage(handing());
    }

    public String handing(){
      /*  generatingTools.json("chatRoom", roomName);
        generatingTools.json("name", user.getName());
        generatingTools.json("id", user.getId());*/
        generatingTools.json("message", this.sendMessage);
        return toJSONString(generatingTools.getJson());
    }

    public void sendMessage(String message){
        try {
            client.send(message);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
