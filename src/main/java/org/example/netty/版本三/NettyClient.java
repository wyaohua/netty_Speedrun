package org.example.netty.版本三;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.TimeUnit;

/**
 * 文件名：NettyClient
 * 作者：huahua
 * 时间：2026/3/5 00:16
 * 描述  我们要让客户端发送一个hello 后， 返回给客户端一个hello world
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
                socketChannel.pipeline()
                        .addLast(new LineBasedFrameDecoder(1024))  //客户端为了能够接收服务器的消息，加一个于/n为界限，协议解析的包
                        .addLast(new StringDecoder()) //解码 ，负责接收服务器传过来的数据
                        //处理String
                        .addLast(new StringEncoder()).addLast(new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println("接收到服务器的响应数据："+ msg);

                            }
                        });
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
                    connectFuture.channel().writeAndFlush("hello"  +"\n");
                },0,1, TimeUnit.SECONDS);

            }
        });

    }
}
