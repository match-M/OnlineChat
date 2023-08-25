package com.match.model.basic.chat;

import com.match.controller.ControllerChat;
import com.match.controller.ControllerHall;
import com.match.model.basic.chat.message.SystemMessageHanding;
import com.match.model.basic.chat.message.ResultMessageHandling;
import com.match.model.basic.hall.Hall;
import com.match.view.hall.HallView;
import com.match.model.basic.tools.ParsingTools;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private final ControllerChat CONTROLLERCHAT = new ControllerChat();
    public static boolean newRoomError = false;
    public static boolean register = false;
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        ControllerHall controllerHall = new ControllerHall();
        ParsingTools parsingTools = new ParsingTools(msg);
        String mode = parsingTools.getString("mode");
        Hall hall = HallView.hall;
        switch (mode) {
            case "getRoomList" :{
                Hall.roomNum = (int) parsingTools.get("roomNumbers");
                Hall.msg = msg;
                break;
            }
            case "search":{
                String result = parsingTools.getString("result");
                hall.setSearchResult(result);
                Platform.runLater(controllerHall::showSearchResult);
                break;
            }
            case "register" :{
                int id = (int) parsingTools.get("id");
                HallView.user.setId(id);
                register = true;
                break;
            }
            case "SystemMessage" :{
                SystemMessageHanding.systemMessage = parsingTools.getString("SysMsg");
                Platform.runLater(CONTROLLERCHAT::systemMessage);
                break;
            }
            case "chat" : {
                ResultMessageHandling.chatMessage = parsingTools.getString("message");
                ResultMessageHandling.chatUserName = parsingTools.getString("name");
                ResultMessageHandling.chatUserId = (int) parsingTools.get("id");
                Platform.runLater(CONTROLLERCHAT::otherMessage);
                break;

            }
            case "newRoom" : {
                String result = parsingTools.getString("result");
                if(result.equals("房间已存在！"))
                    newRoomError = true;
                break;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        System.out.println("exit");
    }

}
