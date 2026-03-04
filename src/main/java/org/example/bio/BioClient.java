package org.example.bio;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BioClient {
    public static void main(String[] args) throws Exception {


        Thread jerry = new Thread(() -> {
            try {
                sendHello();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        },"jerry");

        Thread tom = new Thread(() -> {
            try {
                sendHello();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        jerry.start();
        jerry.join();
        tom.start();
        tom.join();

    }

    private static void sendHello() throws IOException {
        //客户端的socket
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost",8080));
        OutputStream outputStream = socket.getOutputStream();
        for (int i = 0; i < 10; i++) {
            outputStream.write((Thread.currentThread().getName()+"hello " + i).getBytes());
            outputStream.flush();
        }
        socket.close();
    }
}
