package org.example.netty.版本二;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.TimeUnit;

/**
 * 文件名：NettyClient
 * 作者：huahua
 * 时间：2026/3/5 00:16
 * 描述 基于版本一 增加了  以/n 来分割，遇到/n就代表是一条完整的消息
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
                //拿到channel 的线程
                EventLoop eventLoop = connectFuture.channel().eventLoop();
                //执行定时任务 ，每过1秒打印发送一句话
                eventLoop.scheduleAtFixedRate(()->{
                    connectFuture.channel().writeAndFlush("hello" + System.currentTimeMillis() +"\n");
                },0,1, TimeUnit.SECONDS);

            }
        });

    }
}
