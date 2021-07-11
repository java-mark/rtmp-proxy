package org.mark.rtmp.proxy.research.nio;

import lombok.extern.slf4j.Slf4j;
import org.mark.rtmp.proxy.utils.Common;

@Slf4j
public class ByteBufferResearch {
    public static void main(String[] args) {
        byte[] input = new byte[]{10, 11, 11};
        assert Common.readIntMedium(input) == 658187;

    }
}
