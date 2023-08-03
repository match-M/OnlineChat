package com.match.model.basic.tools;

import com.match.model.basic.chat.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetWorkTools {

    public boolean connectionTesting(String ip, int port){
        boolean testError = false;
        NioEventLoopGroup group = new NioEventLoopGroup();//用于处理网络通信（读写）的线程组
        Bootstrap b = new Bootstrap();//创建客户端辅助类工具
        b.group(group)//绑定线程组
                .channel(NioSocketChannel.class)//设置通信渠道为TCP协议
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch)
                            throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ClientHandler());
                    }
                });
        try {
            b.connect(ip, port).sync();
        } catch (Exception e) {
            testError = true;
            e.printStackTrace();
        }
        b.clone();
        group.shutdownGracefully();
        return testError;
    }

    /**
     * 本地回环测试
     * @return true-本地回环没问题，false-本地回环出错
     */
    public boolean localTest(){
        int  timeOut =  3000 ;  //超时应该在3钞以上
        boolean localStat = false;
        try {
            localStat = InetAddress.getByName("127.0.0.1").isReachable(timeOut); // 当返回值是true时，说明host是可用的，false则不可。
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localStat;
    }

    public int intranetTest(){
        int stat = 0;
        boolean intranetStat;
        int  timeOut =  3000 ;  //超时应该在3钞以上
        String end = ".1";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String intranetIp = addr.getHostAddress(); //获取本机ip地址
            String ipEnd = intranetIp.substring(intranetIp.length() - 3); //获取本机ip的后三位
            String ipStart = intranetIp.substring(0, intranetIp.length() - 3);
            if(ipStart.endsWith(".")){
                end = "1";
            }
            if(ipStart.startsWith("127")){
                stat = -1;
                return stat;
            }
            String gatewayAddress = intranetIp.substring(0, intranetIp.indexOf(ipEnd)) + end; //获取网关地址
            intranetStat = InetAddress.getByName(gatewayAddress).isReachable(timeOut); // 当返回值是true时，说明host是可用的，false则不可
            if(intranetStat) stat = 1;
        } catch (UnknownHostException e) {
            stat = -1;
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stat;
    }

    public boolean outerNetTest(){
        int  timeOut =  3000 ;  //超时应该在3钞以上
        boolean baidu = false;
        boolean bilibili = false;
        try {
            baidu = InetAddress.getByName("www.baidu.com").isReachable(timeOut);
            bilibili =  InetAddress.getByName("www.bilibili.com").isReachable(timeOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baidu || bilibili;
    }
}
