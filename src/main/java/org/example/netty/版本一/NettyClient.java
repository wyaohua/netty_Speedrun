package org.example.netty.版本一;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 文件名：NettyClient
 * 作者：huahua
 * 时间：2026/3/5 00:16
 * 描述 最简单的一个Netty的服务器和客户端
 */
public class NettyClient {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        //因为客户端不需要连接的Boss，所以只有一个work
        bootstrap.group(new NioEventLoopGroup());
        //我们客户端的channel是 SocketChannel
        bootstrap.channel(NioSocketChannel.class);
        //配置 pipeline 出站的操作
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new StringEncoder());
            }
        });
        ChannelFuture connectFuture = bootstrap.connect("localhost", 8080);
        connectFuture.addListener(f->{
            if (f.isSuccess()){
                System.out.println("客户端连接服务器，成功了");
                connectFuture.channel().writeAndFlush("hello 服务器");
            }
        });

    }
}
