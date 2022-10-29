package com.agri.skt.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {

    public static void main(String[] args) {
        try(ServerSocket server = new ServerSocket(8080);) {
            Socket socket = server.accept();
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String content;

            while ((content = br.readLine()) != null && !content.equals("")) {
                System.out.println(content);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }


    }
}
