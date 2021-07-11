package org.mark.rtmp.proxy.handshake;

import io.netty.buffer.ByteBuf;

public class ServerPacket {

    public static class S0Packet extends ClientPacket.C0Packet {
        public S0Packet(ByteBuf version) {
            super(version);
        }

        public S0Packet() {
            super();
        }
    }

    public static class S1Packet extends ClientPacket.C1Packet {
        public S1Packet(ByteBuf time, ByteBuf random) {
            super(time, random);
        }

        public S1Packet(int time, byte[] random) {
            super(time, random);
        }
    }

    public static class S2Packet extends ClientPacket.C2Packet {
        public S2Packet(ByteBuf time, ByteBuf time2, ByteBuf random) {
            super(time, time2, random);
        }

        public S2Packet(int time, int time2, byte[] random) {
            super(time, time2, random);
        }
    }
}
