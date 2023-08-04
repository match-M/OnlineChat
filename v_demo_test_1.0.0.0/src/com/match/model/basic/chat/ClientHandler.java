package com.match.model.basic.chat;

import com.match.controller.ControllerChat;
import com.match.model.basic.chat.message.SystemMessageHanding;
import com.match.model.basic.chat.message.ResultMessageHandling;
import com.match.model.basic.constants.SignUpError;
import com.match.model.basic.constants.UserOperationError;
import com.match.model.basic.hall.Hall;
import com.match.view.dialog.ErrorDialog;
import com.match.view.dialog.PromptDialog;
import com.match.view.hall.HallView;
import com.match.model.basic.tools.ParsingTools;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private final ControllerChat CONTROLLERCHAT = new ControllerChat();
    public static boolean register = false;
    public static boolean newRoomError = false;
    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {

        ParsingTools parsingTools = new ParsingTools(msg);
        String mode = parsingTools.getString("mode");
        System.out.println(msg);
        switch (mode) {
            case "getRoomList" :{
                Hall.roomNum = (int) parsingTools.get("roomNumbers");
                Hall.msg = msg;
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
