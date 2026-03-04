package org.example.netty.版本三;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 文件名：NettyServer
 * 作者：huahua
 * 时间：2026/3/4 23:46
 * 描述  我们要让客户端发送一个hello 后， 返回给客户端一个hello world
 */
public class NettyServer {


    public static void main(String[] args) {
        //模拟数据库 ，每个channel都有对应的一个数据库
        Map<Channel, List<String>> db = new ConcurrentHashMap<>();


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
                //1.加上以换行符来分割消息；
                // 2.然后 String的解码器，将接收到的字节转为String
                //3.然后传给channelRead0；
                socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new StringDecoder())
                        .addLast(new StringEncoder()) //增加一个出站的处理器，处理我们写回客户端的数据
                        .addLast(new ResponseHandler())
                        .addLast(new DbHandler(db));
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


    static  class  ResponseHandler  extends   SimpleChannelInboundHandler<String>{
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(msg);
            String message = msg+ " world\n";
            //写回客户端,这是一个出站的过程
            ctx.channel().writeAndFlush(message);
            //让消息传给后面的handler
            ctx.fireChannelRead(msg);
        }
    }

    static class DbHandler extends   SimpleChannelInboundHandler<String> {
        private  Map<Channel, List<String>> db ;

        public DbHandler(Map<Channel, List<String>> db ){
            this.db =db;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            List<String> messageList = db.computeIfAbsent(ctx.channel(), k -> new ArrayList<>());
            messageList.add(msg);

        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel() +" 注册的回调");
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel() +" 解除注册");
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel() +" 可以使用了");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            List<String> strings = db.get(ctx.channel());
            System.out.println("不活跃了，打印所有的数据");
            System.out.println(strings);
        }

    }
}
