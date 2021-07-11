package org.mark.rtmp.proxy.research.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * reference
 * https://netty.io/wiki/user-guide-for-4.x.html
 */
public class SimpleServer {
    public static class HandlerChannel extends ChannelInboundHandlerAdapter {
        boolean state = false;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("status=" + state);


            ByteBuf in = (ByteBuf) msg;
            System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));

//            System.out.println("Yes, A new client in = " + ctx.name());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }


    private final int port;

    public SimpleServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        NioEventLoopGroup bossEventGroup = new NioEventLoopGroup();
        NioEventLoopGroup bizEventGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossEventGroup, bizEventGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HandlerChannel());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = serverBootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            bizEventGroup.shutdownGracefully();
            bossEventGroup.shutdownGracefully();
        }
    }

    /**
     * test method:
     * telnet localhost 8000
     * <p>
     * <p>
     * everything you input will output by server.
     *
     * @param args args
     * @throws InterruptedException when server was interrupt
     */
    public static void main(String[] args) throws InterruptedException {
        SimpleServer server = new SimpleServer(8000);
        server.run();
    }
}
