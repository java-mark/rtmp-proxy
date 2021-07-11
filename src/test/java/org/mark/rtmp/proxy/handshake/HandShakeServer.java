package org.mark.rtmp.proxy.handshake;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.mark.rtmp.proxy.research.netty.SimpleServer;
import org.mark.rtmp.proxy.utils.Common;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
public class HandShakeServer {
    private final int port;

    public HandShakeServer(int port) {
        this.port = port;
    }


    public static class ConnectionAdaptor extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("Handle connect from address={}", ctx.channel().remoteAddress());
            ctx.fireChannelActive();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.info("Channel inactive with={}", ctx.channel().remoteAddress());
            ctx.fireChannelInactive();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("Channel exceptionCaught:" + ctx.channel().remoteAddress(), cause);
        }
    }

    public static class HandShakeDecoder extends ByteToMessageDecoder {

        private final ClientPacketDecoder packetDecoder = new ClientPacketDecoderImpl();

        private ClientPacket.C0Packet c0 = null;

        private ClientPacket.C1Packet c1 = null;

        private ClientPacket.C2Packet c2 = null;

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

            if (handShakeDone()) {
                ctx.fireChannelRead(in);
                return;
            }

            if (c0 == null) {
                if (in.readableBytes() >= ClientPacket.C0Packet.expectLength()) {
                    c0 = packetDecoder.decodeC0(in);
                } else {
                    throw new IllegalAccessException(
                            "Input byte buf less then " + ClientPacket.C0Packet.expectLength() + " when decode c0");
                }
            }

            if (c0 != null && c1 == null) {
                if (in.readableBytes() >= ClientPacket.C1Packet.expectLength()) {
                    c1 = packetDecoder.decodeC1(in);
                } else {
                    throw new IllegalAccessException(
                            "Input byte buf less then " + ClientPacket.C1Packet.expectLength() + " when decode c1");
                }
            }

            if (c0 != null && c1 != null) {
                writeS0S1S2(ctx);
            }

            if (c2 == null) {
                if (in.readableBytes() >= ClientPacket.C2Packet.expectLength()) {
                    c2 = packetDecoder.decodeC2(in);
                    ctx.channel().pipeline().remove(this);
                } else {
                    throw new IllegalAccessException(
                            "Input byte buf less then " + ClientPacket.C2Packet.expectLength() + " when decode c2");
                }
            }

        }

        private boolean handShakeDone() {
            return c0 != null && c1 != null && c2 != null;
        }

        private void writeS0S1S2(ChannelHandlerContext ctx) {
            ByteBuf responseBuf = Unpooled.buffer(
                    ServerPacket.S1Packet.expectLength() + ServerPacket.S0Packet.expectLength()
            );
            responseBuf.writeBytes(new ServerPacket.S0Packet().encode());
            int time = (int) (new Date().getTime() / 1000);
            ServerPacket.S1Packet s1Packet = new ServerPacket.S1Packet(time,
                    Common.generateRandomByteArray(ServerPacket.S1Packet.BYTE_LENGTH_OF_RANDOM));
            responseBuf.writeBytes(s1Packet.encode());

            ServerPacket.S2Packet s2Packet = new ServerPacket.S2Packet(
                    c1.getTime().readInt(), time, c1.getRandom().array());
            responseBuf.writeBytes(s2Packet.encode());

            ctx.writeAndFlush(responseBuf);
        }
    }

    public static class ChunkDecoder extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf in = (ByteBuf) msg;



        }



    }

    public void run() throws InterruptedException {
        NioEventLoopGroup bossEventGroup = new NioEventLoopGroup();
        NioEventLoopGroup bizEventGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossEventGroup, bizEventGroup)
                    .channel(NioServerSocketChannel.class)
                    // 每次新连接过来都会通过这个initializer创建一个新的pipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ConnectionAdaptor())
                                    .addLast(new HandShakeServer.HandShakeDecoder())
                                    .addLast();
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

    public static void main(String[] args) throws InterruptedException {
        SimpleServer server = new SimpleServer(8000);
        server.run();
    }
}
