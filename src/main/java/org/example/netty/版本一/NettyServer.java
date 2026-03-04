package org.example.netty.版本一;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 文件名：NettyServer
 * 作者：huahua
 * 时间：2026/3/4 23:46
 * 描述 最简单的一个Netty的服务器和客户端
 */
public class NettyServer {


    public static void main(String[] args) {

        ServerBootstrap serverBootstrap =  new ServerBootstrap();

        //定义两个线程组，一个是boss ，一个是work
        serverBootstrap.group(new NioEventLoopGroup(),new NioEventLoopGroup());

        //服务器所以用的是ServerSocketChannel
        serverBootstrap.channel(NioServerSocketChannel.class);

        //每一个socketChannel都是独立的，拥有自己的上下文；每个一socketChannel都有自己独立的pipeline；
        //这个就是对每一个socketChannel，设置一套默认的handler链条
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                //配置pipeline
                //1.加上 String的解码器，将接收到的字节转为String
                //2.然后传给channelRead0；
                socketChannel.pipeline().addLast(new StringDecoder())
                        .addLast(new SimpleChannelInboundHandler<String>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
                        System.out.println(msg);
                    }
                });
            }
        });

        ChannelFuture bindFuture = serverBootstrap.bind(8080);
        bindFuture.addListener(f->{
            if (f.isSuccess()) {
                System.out.println("服务器监听8080端口，成功了");
            }else{
                System.out.println("服务器监听8080端口，失败了");
            }
        });

    }
}
