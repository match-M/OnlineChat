package com.match.model.basic.hall;

import com.match.model.basic.client.Client;
import com.match.model.basic.tools.ParsingTools;
import com.match.model.basic.tools.GeneratingTools;
import com.match.model.basic.user.User;
import com.match.view.hall.HallView;

import java.util.ArrayList;

import static com.alibaba.fastjson.JSON.toJSONString;

public class Hall {
    private Client client;
    private GeneratingTools generatingTools = new GeneratingTools();
    public static String ResMsg_newRoom = " ";
    public static String msg = " ";
    public static int roomNum;
    public User user;

    public Hall() { }
    public Hall(Client client){
        this.client = client;
    }

    public void selectRoom(String name){
        try {
            user = HallView.user;
            generatingTools.json("mode", "selectRoom");
            generatingTools.json("id", user.getId());
            generatingTools.json("userName", user.getName());
            generatingTools.json("chatRoom", name);
            client.send(toJSONString(generatingTools.getJson()));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean newRoom(String name){
        try{
            boolean newRoomHavaState = false;
            generatingTools.json("mode", "newRoom");
            generatingTools.json("NewRoomName", name);
            client.send(toJSONString(generatingTools.getJson()));
            if(ResMsg_newRoom.equals("房间已存在！")){
                newRoomHavaState = true;
                ResMsg_newRoom = " ";
            }
            return newRoomHavaState;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> roomList(){
        try {
            generatingTools.initJson();
            int roomNumbers;
            ArrayList<String> roomName = new ArrayList<>();
            generatingTools.json("mode", "getRoomList");
            client.send(toJSONString(generatingTools.getJson()));
            ParsingTools parsingTools = new ParsingTools(msg);
            roomNumbers = roomNum;
            for(int i = 1; i <= roomNumbers; i++){
                roomName.add(parsingTools.getString("room"+i));
            }
            Thread.sleep(100);
            return roomName;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
