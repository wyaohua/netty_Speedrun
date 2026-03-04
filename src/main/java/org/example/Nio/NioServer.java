package org.example.Nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 1.三大组件
 *  1.1 Channel 就是消息的通道
 *  1.2 buffer 消息的载体
 *  1.3 selector监听多个channel的状态， 实现多路复用的核心；
 *
 *  //核心问题： ByteBuffer大小的设置  过大会导致粘包现象， 过小导致半包现象；这个是TCP导致的
 *  // * 描述  为了解决版本一的 粘包和半包问题；需要在客户端和服务端之间指定一些规则，通过这些规则保证一条完整消息的获取；
 *  * 规则：每条消息的第一个字节设置为消息的长度；或者其他规则 能解决问题即可
 *  
 *
 *
 */
public class NioServer {

    public static void main(String[] args) throws IOException {
        //服务端channel （大马路）
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false); //读取是非阻塞的
        ssc.bind(new InetSocketAddress("localhost",8080));


        //在serverSocket上注册一个 selector ,并且监听连接事件
        Selector selector = Selector.open();

        //注册监听，注册了一个ServerSocketChannel对应OP_ACCEPT 连接事件；
        ssc.register(selector,SelectionKey.OP_ACCEPT);

        while (true){
            //阻塞函数，会一直监听事件，例如 有人连接，有人发数据来，它就醒来；
             selector.select();
             //集合里是所有的触发事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //遍历每一个
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove(); //处理完这个key，要把它从集合移除掉；

                //如果有新连接；
                if (key.isAcceptable()){
                    //连接事件对应的只有 ServerSocketChannel 所以强转为 ServerSocketChannel 处理新的连接
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
                    //从ServerSocketChannel上获取 连接的客户端
                    SocketChannel client = serverSocketChannel.accept();
                    client.configureBlocking(false);
                    //监听器上注册了一个 SocketChannel 对应可读事件
                    client.register(selector,SelectionKey.OP_READ);

                }
                //说明有数据可读
                if (key.isReadable()){
                    //可读事件 只对应了 SocketChannel ，所以强转为SocketChannel类型；
                    SocketChannel channel = (SocketChannel)key.channel();
                    //开始处理数据
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int length = channel.read(byteBuffer);
                    if (length == -1){
                        System.out.println("客户端 断开了连接" +channel.getRemoteAddress());
                        channel.close();

                    }else{
                        byteBuffer.flip();
                        byte[] buffer =new byte[byteBuffer.remaining()];
                        byteBuffer.get(buffer);
                        String message =new String(buffer);
                        System.out.println(message);
                    }

                }


            }
        }

    }
}
