package org.example.bio;


import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 当前版本二
 * Bio 面向流的阻塞io
 */
public class BioServer {


    public static void main(String[] args) throws Exception {
        //服务器
        ServerSocket serverSocket = new ServerSocket(8080);

        while (true){
            //等待连接客户端，返回一个客户端socket，处理客户端的信息
            Socket accept = serverSocket.accept();

            //获取客户端的消息
            InputStream inputStream = accept.getInputStream();
            byte[] buffer = new byte[1024];
            int length ;
            while ((length =inputStream.read(buffer)) != -1){
                String message = new String(buffer, 0, length);
                System.out.println(message);
            }
            System.out.println("客户端断开连接~");
        }




    }
}
/**
 *
 *  版本一： 服务器端，只能接收一个客户端的请求，处理完就结束
 *
 * public static void main(String[] args) throws Exception {
 *         //服务器
 *         ServerSocket serverSocket = new ServerSocket(8080);
 *
 *
 *
 *             //等待连接客户端，返回一个客户端socket，处理客户端的信息
 *             Socket accept = serverSocket.accept();
 *
 *             //获取客户端的消息
 *             InputStream inputStream = accept.getInputStream();
 *             byte[] buffer = new byte[1024];
 *             int length ;
 *             while ((length =inputStream.read(buffer)) != -1){
 *                 String message = new String(buffer, 0, length);
 *                 System.out.println(message);
 *             }
 *             System.out.println("客户端断开连接~");
 *
 *
 *     }
 */


/**
 * 版本二：增加while循环 让服务器能循环不停的接收请求，
 *  特点：（传统的BIO模型，阻塞IO）  面向的是流；
 *  问题： 1.只能一个一个来处理
 *        2。其中有个客户端不断开连接，那么服务端就要也一直等待，无法处理后续的连接请求
 *
 *
 * public static void main(String[] args) throws Exception {
 *         //服务器
 *         ServerSocket serverSocket = new ServerSocket(8080);
 *
 *         while (true){
 *             //等待连接客户端，返回一个客户端socket，处理客户端的信息
 *             Socket accept = serverSocket.accept();
 *
 *             //获取客户端的消息
 *             InputStream inputStream = accept.getInputStream();
 *             byte[] buffer = new byte[1024];
 *             int length ;
 *             while ((length =inputStream.read(buffer)) != -1){
 *                 String message = new String(buffer, 0, length);
 *                 System.out.println(message);
 *             }
 *             System.out.println("客户端断开连接~");
 *         }
 *
 *
 *
 *
 *     }
 * */