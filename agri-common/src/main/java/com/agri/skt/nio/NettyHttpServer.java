package com.agri.skt.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

public class NettyHttpServer {

    int port;

    public NettyHttpServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup mainGroup = new NioEventLoopGroup();

        EventLoopGroup subGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.option(ChannelOption.SO_BACKLOG, 1024);
            server.group(mainGroup, subGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(this.port)
//                    .localAddress(8080)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        protected void initChannel(SocketChannel channel) throws Exception {
                            System.out.println("IP:" + channel.localAddress().getHostName());
                            System.out.println("Port:" + channel.localAddress().getPort());

                            channel.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
                            channel.pipeline().addLast(new EchoServerHandler());
                            channel.pipeline().addLast(new ByteArrayEncoder());
                        }
                    });
            ChannelFuture cf = server.bind().sync();
            System.out.println(NettyHttpServer.class + " 启动正在监听： " + cf.channel().localAddress());
            cf.channel().closeFuture().sync();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            mainGroup.shutdownGracefully().sync();
            subGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyHttpServer(8080).start();
    }
}

