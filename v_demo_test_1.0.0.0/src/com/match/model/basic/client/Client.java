package com.match.model.basic.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.match.model.basic.chat.ClientHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 在线聊天系统的客户端初始化和发送功能的实现
 * 采用netty框架
 * @author match
 */
public class Client {

    public String ip;
    public Integer port;
    public ChannelFuture channelFuture;

    public Client() { }

    public Client(String ip, int port) throws SocketException {
        this.ip = ip;
        this.port = port;
    }

    //初始化客户端
    public void initClient() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();//用于处理网络通信（读写）的线程组
        Bootstrap b = new Bootstrap();//创建客户端辅助类工具
        b.group(group)//绑定线程组
                .channel(NioSocketChannel.class)//设置通信渠道为TCP协议
                .handler(new ChannelInitializer<NioSocketChannel>() {
                             @Override
                             protected void initChannel(NioSocketChannel ch)
                                     throws Exception {
                                 ChannelPipeline pipeline = ch.pipeline();
                                 pipeline.addLast(new StringDecoder(Charset.forName("GBK")));
                                 pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));

                             /*    pipeline.addLast("encoder", new StringEncoder());
                                 pipeline.addLast("decoder", new StringDecoder());*/
                                 pipeline.addLast(new ClientHandler());
                             }
                         });
        channelFuture = b.connect(ip, port).sync();//异步建立连接
    }

    public void send(String sendMsg) throws Exception{
        Channel channel = channelFuture.channel();
        channel.writeAndFlush(sendMsg);
    }


}


