package org.mark.rtmp.proxy.amf;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class Amf0Decoder {
    /**
     * @return
     */
    public AMF0Declare.Amf0Marker decode(ByteBuf byteBuf) {
        int type = byteBuf.readByte() & 0xff;
        switch (type) {
            case 0:
                return new AMF0Declare.NumberMarker(decodeNumber(byteBuf));
            case 1:
                return new AMF0Declare.BooleanMarker(decodeBoolean(byteBuf));
            case 2:
                return new AMF0Declare.StringMarker(decodeString(byteBuf));
            case 3:
                return new AMF0Declare.ObjectMarker(decodeObject(byteBuf));
            case 5:
                return new AMF0Declare.NULLMarker();
            case 6:
                return new AMF0Declare.UndefinedMarker();
            case 7:
                return new AMF0Declare.ReferenceMarker();
            case 8:
                return new AMF0Declare.ECMAArrayMarker(decodeECMAArray(byteBuf));
            case 9:
                return new AMF0Declare.ObjectEndMarker(decodeObjectEnd(byteBuf));
            case 10:
                return new AMF0Declare.StaticArrayMarker(decodeStaticArray(byteBuf));
            case 11:
                return new AMF0Declare.DateMarker(decodeDate(byteBuf));
            case 12:
                return new AMF0Declare.LongStringMarker(decodeLongString(byteBuf));
            case 13:
                return new AMF0Declare.UnsupportedMarker();
            case 15:
            case 16:
            case 14:
            case 4:
            default:
                throw new UnsupportedOperationException("Unsupported amf type=" + type);
        }
    }

    private Date decodeDate(ByteBuf byteBuf) {
        final long dateValue = byteBuf.readLong();
        byteBuf.readShort(); // reserved; not support; should be set to 0x0000
        return new Date((long) Double.longBitsToDouble(dateValue));
    }

    private ByteBuf decodeObjectEnd(ByteBuf byteBuf) {
        return byteBuf.readBytes(3);
    }

    private boolean decodeBoolean(ByteBuf byteBuf) {
        return byteBuf.readByte() == AMF0Declare.BooleanMarker.BOOLEAN_TRUE;
    }

    private long decodeNumber(ByteBuf byteBuf) {
        return byteBuf.readLong();
    }

    private String decodeString(ByteBuf byteBuf) {
        short size = byteBuf.readShort();
        return new String(byteBuf.readBytes(size).array());
    }

    private String decodeLongString(ByteBuf byteBuf) {
        int size = byteBuf.readInt();
        return new String(byteBuf.readBytes(size).array());
    }

    private List<AMF0Declare.Amf0Marker> decodeStaticArray(ByteBuf byteBuf) {
        List<AMF0Declare.Amf0Marker> res = new ArrayList<>();

        int count = byteBuf.readInt();
        for (int i = 0; i < count; i++) {
            AMF0Declare.Amf0Marker marker = decode(byteBuf);
            res.add(marker);
        }

        return res;
    }

    private LinkedHashMap<String, AMF0Declare.Amf0Marker> decodeECMAArray(ByteBuf byteBuf) {
        int count = byteBuf.readInt();
        return decodeObject(byteBuf);
    }

    private LinkedHashMap<String, AMF0Declare.Amf0Marker> decodeObject(ByteBuf byteBuf) {

        LinkedHashMap<String, AMF0Declare.Amf0Marker> res = new LinkedHashMap<>();

        byte[] endMarker = new byte[3];

        while (byteBuf.isReadable()) {
            String key = decodeString(byteBuf);
            AMF0Declare.Amf0Marker marker = decode(byteBuf);

            res.put(key, marker);

            byteBuf.getBytes(0, endMarker);
            if (Arrays.equals(endMarker, AMF0Declare.ObjectEndMarker.OBJECT_END_MARKER)) {
                byteBuf.skipBytes(3);
                log.debug("end MAP / OBJECT, found object end marker [000009]");
                break;
            }
        }

        return res;
    }

}

