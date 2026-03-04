package org.example.bio;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BioClient {
    public static void main(String[] args) throws Exception {


        Thread thread1 = new Thread(() -> {
            try {
                sendHello();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread theard2 = new Thread(() -> {
            try {
                sendHello();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread1.start();
        thread1.join();
        theard2.start();
        theard2.join();

    }

    private static void sendHello() throws IOException {
        //客户端的socket
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost",8080));
        OutputStream outputStream = socket.getOutputStream();
        for (int i = 0; i < 10; i++) {
            outputStream.write(("hello" + i).getBytes());
            outputStream.flush();
        }
        socket.close();
    }
}
