package org.mark.rtmp.proxy.handshake;

import io.netty.buffer.ByteBuf;

public interface ClientPacketDecoder {
    ClientPacket.C1Packet decodeC1(ByteBuf buf);

    ClientPacket.C0Packet decodeC0(ByteBuf buf);

    ClientPacket.C2Packet decodeC2(ByteBuf buf);
}
