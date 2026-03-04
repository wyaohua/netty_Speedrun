package org.example.Nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
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
 */
public class NioServer {

    public static void main(String[] args) throws IOException {
        //服务端channel （大马路）
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false); //读取是非阻塞的
        ssc.bind(new InetSocketAddress("loaclhost",8080));


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
