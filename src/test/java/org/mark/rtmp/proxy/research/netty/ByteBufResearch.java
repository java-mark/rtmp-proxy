package org.mark.rtmp.proxy.research.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.core.config.plugins.convert.HexConverter;
import org.mark.rtmp.proxy.utils.Common;

public class ByteBufResearch {
    public static void main(String[] args) {
        ByteBuf byteBuf1 = Unpooled.wrappedBuffer(new byte[]{(byte) 0xff});

        ByteBuf byteBuf = Unpooled.copyMedium(0xffffff);
        System.out.println(Common.bytesToHex(byteBuf.array()));
    }
}
