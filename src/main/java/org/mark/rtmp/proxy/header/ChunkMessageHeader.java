package org.mark.rtmp.proxy.header;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

@Getter
public class ChunkMessageHeader {

    public static final int MAX_TIMESTAMP = 0XFFFFFF;

    public final BasicHeader basicHeader;

    private int timestamp;

    private int messageLength;

    private short messageTypeId;

    private int messageStreamId;

    private int timestampDelta;

    private int extendedTimestamp;

    public ChunkMessageHeader(BasicHeader basicHeader, ByteBuf headerContent) {
        this.basicHeader = basicHeader;
        decode(headerContent);
    }

    private void decode(ByteBuf headerContent) {
        switch (basicHeader.getFmt()) {
            case TYPE0:
                timestamp = headerContent.readMedium();
                messageLength = headerContent.readMedium();
                messageTypeId = (short) (headerContent.readByte() & 0xff);
                messageStreamId = headerContent.readMediumLE();
                if (timestamp == MAX_TIMESTAMP) {
                    extendedTimestamp = headerContent.readInt();
                }
                break;
            case TYPE1:
                timestampDelta = headerContent.readMedium();
                messageLength = headerContent.readMedium();
                messageTypeId = (short) (headerContent.readByte() & 0xff);
                if (timestampDelta == MAX_TIMESTAMP) {
                    extendedTimestamp = headerContent.readInt();
                }
                break;
            case TYPE2:
                timestampDelta = headerContent.readMedium();
                if (timestampDelta == MAX_TIMESTAMP) {
                    extendedTimestamp = headerContent.readInt();
                }
                break;
            case TYPE3:
            default:
                break;
        }
    }

    private boolean containExtendedTimestamp() {
        switch (basicHeader.getFmt()) {
            case TYPE0:
                return timestamp == MAX_TIMESTAMP;
            case TYPE1:
            case TYPE2:
                return timestampDelta == MAX_TIMESTAMP;
            case TYPE3:
            default:
                return false;
        }
    }

    public int getLength() {
        return basicHeader.getByteLength()
                + basicHeader.getFmt().getChunkMessageLength()
                + (containExtendedTimestamp() ? 4 : 0);
    }
}
