package org.mark.rtmp.proxy.handshake;

import io.netty.buffer.ByteBuf;

public class ClientPacketDecoderImpl implements ClientPacketDecoder {
    @Override
    public ClientPacket.C1Packet decodeC1(ByteBuf buf) {
        ByteBuf time = buf.readBytes(ClientPacket.C1Packet.BYTE_LENGTH_OF_TIME);
        ByteBuf random = buf.readBytes(ClientPacket.C1Packet.BYTE_LENGTH_OF_RANDOM);
        return new ClientPacket.C1Packet(time, random);
    }

    @Override
    public ClientPacket.C0Packet decodeC0(ByteBuf buf) {
        ByteBuf version = buf.readBytes(ClientPacket.C0Packet.BYTE_LENGTH_OF_TIME);
        return new ClientPacket.C0Packet(version);
    }

    @Override
    public ClientPacket.C2Packet decodeC2(ByteBuf buf) {
        ByteBuf time = buf.readBytes(ClientPacket.C2Packet.BYTE_LENGTH_OF_TIME);
        ByteBuf time2 = buf.readBytes(ClientPacket.C2Packet.BYTE_LENGTH_OF_TIME2);
        ByteBuf random = buf.readBytes(ClientPacket.C2Packet.BYTE_LENGTH_OF_RANDOM);

        return new ClientPacket.C2Packet(time, time2, random);
    }

}
