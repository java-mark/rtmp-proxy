package org.mark.rtmp.proxy.header;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

/**
 * The Chunk Basic Header encodes the chunk stream ID and the chunk type
 * (represented by fmt field in the figure below). Chunk type
 * determines the format of the encoded message header. Chunk Basic
 * Header field may be 1, 2, or 3 bytes, depending on the chunk stream
 * ID.
 */
public class BasicHeader {

    @Getter
    private ChunkStreamType fmt;

    @Getter
    private int csid;

    @Getter
    private final int byteLength;

    public BasicHeader(ByteBuf buffer) {
        byteLength = decode(buffer);
    }

    private int decode(ByteBuf buffer) {
        int headerLength = 1;

        int fmtInt = (buffer.getByte(0) & 0xff) >> 6;
        if (fmtInt == 0) {
            fmt = ChunkStreamType.TYPE0;
        } else if (fmtInt == 1) {
            fmt = ChunkStreamType.TYPE1;
        } else if (fmtInt == 2) {
            fmt = ChunkStreamType.TYPE2;
        } else {
            fmt = ChunkStreamType.TYPE3;
        }

        csid = (buffer.getByte(0) & 0x3f);

        if (csid == 0) {
            // 2 byte form
            csid = buffer.getByte(1) & 0xff + 64;
            headerLength += 1;
        } else if (csid == 1) {
            // 3 byte form
            this.csid = (buffer.getByte(2) & 0xff) << 8 + (buffer.getByte(1) & 0xff) + 64;
            headerLength += 2;
        }


        return headerLength;
    }


    public enum ChunkStreamType {
        TYPE0(11),
        TYPE1(7),
        TYPE2(3),
        TYPE3(0);

        @Getter
        private final int chunkMessageLength;

        ChunkStreamType(int chunkMessageLength) {
            this.chunkMessageLength = chunkMessageLength;
        }
    }
}
