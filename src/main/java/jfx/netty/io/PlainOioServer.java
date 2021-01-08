package jfx.netty.io;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * PlainOioServer
 * 阻塞 I/O
 *
 * @author cxy
 * @date 2021/01/07
 */
public class PlainOioServer {

    public void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port);     // 1
        for (; ; ) {
            final Socket clientSocket = socket.accept();        // 2
            System.out.println("Accepted connection from" + clientSocket);

            new Thread(() -> {                                  // 3
                OutputStream out;
                try {
                    out = clientSocket.getOutputStream();
                    out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
                    out.flush();
                    clientSocket.close();                       // 5

                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        clientSocket.close();
                    } catch (IOException ex) {
                        // ignore on close
                    }
                }
            }).start();                                         // 6
        }
    }
}
