package com.agri.skt.bio;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MyClient {

    public static void main(String[] args) {
        try(Socket socket = new Socket("127.0.0.1", 8080)) {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);

            writer.write("用户名：admin;密码：123");
            writer.flush();
            socket.shutdownOutput();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
