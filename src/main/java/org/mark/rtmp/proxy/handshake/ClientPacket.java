package org.mark.rtmp.proxy.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mark.rtmp.proxy.Packet;

public class ClientPacket {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class C0Packet implements Packet {
        public static int BYTE_LENGTH_OF_TIME = 1;

        protected ByteBuf version;

        public C0Packet() {
            version = Unpooled.wrappedBuffer(new byte[]{3});
        }

        public static int expectLength() {
            return BYTE_LENGTH_OF_TIME;
        }

        public ByteBuf encode() {
            return version;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class C1Packet implements Packet {
        public static int BYTE_LENGTH_OF_TIME = 4;

        public static int BYTE_LENGTH_OF_ZERO = 4;

        public static int BYTE_LENGTH_OF_RANDOM = 1528;

        protected ByteBuf time;

        protected ByteBuf random;

        public C1Packet(int time, byte[] random) {
            this.time = Unpooled.buffer(BYTE_LENGTH_OF_TIME);
            this.time.writeInt(time);

            this.random = Unpooled.wrappedBuffer(random);
        }

        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer(expectLength());
            res.writeBytes(time);
            res.writeInt(0);
            res.writeBytes(random);
            return res;
        }

        public static int expectLength() {
            return BYTE_LENGTH_OF_RANDOM + BYTE_LENGTH_OF_TIME + BYTE_LENGTH_OF_ZERO;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class C2Packet implements Packet {
        public static int BYTE_LENGTH_OF_TIME = 4;

        public static int BYTE_LENGTH_OF_TIME2 = 4;

        public static int BYTE_LENGTH_OF_RANDOM = 1528;

        ByteBuf time;

        ByteBuf time1;

        ByteBuf random;

        public C2Packet(int time, int time2, byte[] random) {
            this.time = Unpooled.buffer(BYTE_LENGTH_OF_TIME);
            this.time.writeInt(time);

            this.time1 = Unpooled.buffer(BYTE_LENGTH_OF_TIME2);
            this.time1.writeInt(time2);

            this.random = Unpooled.wrappedBuffer(random);
        }

        public ByteBuf encode() {
            ByteBuf res = Unpooled.buffer(expectLength());
            res.writeBytes(time);
            res.writeBytes(time1);
            res.writeBytes(random);
            return res;
        }

        public static int expectLength() {
            return BYTE_LENGTH_OF_RANDOM + BYTE_LENGTH_OF_TIME + BYTE_LENGTH_OF_TIME2;
        }
    }

}
