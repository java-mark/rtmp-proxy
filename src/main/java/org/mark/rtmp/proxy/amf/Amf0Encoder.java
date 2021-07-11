package org.mark.rtmp.proxy.amf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.List;

public class Amf0Encoder {
    public ByteBuf encode(List<AMF0Declare.Amf0Marker> markers) {
        ByteBuf byteBuf = Unpooled.buffer();
        for (AMF0Declare.Amf0Marker marker : markers) {
            byteBuf.writeBytes(marker.encode());
        }
        return byteBuf;
    }

    public ByteBuf encode(AMF0Declare.Amf0Marker marker) {
        return marker.encode();
    }
}
